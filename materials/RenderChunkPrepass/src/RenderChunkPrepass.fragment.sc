$input v_bitangent, v_color0, v_lightmapUV, v_normal, v_tangent, v_texcoord0, v_position, v_worldPos
#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
    $input v_frontFacing, v_pbrTextureId
#endif

#include <bgfx_shader.sh>
#include <bgfx_compute.sh>

struct PBRTextureData {
    float colourToMaterialUvScale0;
    float colourToMaterialUvScale1;
    float colourToMaterialUvBias0;
    float colourToMaterialUvBias1;
    float colourToNormalUvScale0;
    float colourToNormalUvScale1;
    float colourToNormalUvBias0;
    float colourToNormalUvBias1;
    int flags;
    float uniformRoughness;
    float uniformEmissive;
    float uniformMetalness;
    float uniformSubsurface;
    float maxMipColour;
    float maxMipMer;
    float maxMipNormal;
};

uniform vec4 GlobalRoughness;
uniform vec4 LightDiffuseColorAndIlluminance;
uniform vec4 LightWorldSpaceDirection;
uniform vec4 MaterialID;
// uniform vec4 SubPixelOffset;
uniform vec4 ViewPositionAndTime;

SAMPLER2D_AUTOREG(s_LightMapTexture);
SAMPLER2D_AUTOREG(s_MatTexture);
SAMPLER2D_AUTOREG(s_SeasonsTexture);

#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)

BUFFER_RO_AUTOREG(s_PBRData, PBRTextureData);

vec2 octWrap(vec2 v) {
    return (1.0 - abs(v.yx)) * ((2.0 * step(0.0, v)) - 1.0);
}
vec2 ndirToOctSnorm(vec3 n) {
    vec2 p = n.xy * (1.0 / (abs(n.x) + abs(n.y) + abs(n.z)));
    p = (n.z < 0.0) ? octWrap(p) : p;
    return p;
}
vec2 ndirToOctUnorm(vec3 n) {
    vec2 p = ndirToOctSnorm(n);
    return p * 0.5f + 0.5f;
}
vec3 octToNdirSnorm(vec2 p) {
    vec3 n = vec3(p.xy, 1.0 - abs(p.x) - abs(p.y));
    n.xy = (n.z < 0.0) ? octWrap(n.xy) : n.xy;
    return normalize(n);
}
vec3 octToNdirUnorm(vec2 p) {
    vec2 pSnorm = p * 2.0f - 1.0f;
    return octToNdirSnorm(pSnorm);
}
float saturatedLinearRemapZeroToOne(float value, float zeroValue, float oneValue) {
    return clamp((((value) * (1.f / (oneValue - zeroValue))) + -zeroValue / (oneValue - zeroValue)), 0.0, 1.0);
}
float packMetalnessSubsurface(float metalness, float subsurface) {
    if (metalness > subsurface) {
        return (128.0 / 255.0) + (127.0 / 255.0) * metalness;
    } else {
        return (127.0 / 255.0) - (127.0 / 255.0) * subsurface;
    }
}
vec2 calculateMotionVector(vec3 worldPosition, vec3 previousWorldPosition) {
    vec4 screenSpacePos = mul(u_viewProj, vec4(worldPosition, 1.0));
    screenSpacePos /= screenSpacePos.w;
    screenSpacePos = screenSpacePos * 0.5 + 0.5;
    vec4 prevScreenSpacePos = mul(u_prevViewProj, vec4(previousWorldPosition, 1.0));
    prevScreenSpacePos /= prevScreenSpacePos.w;
    prevScreenSpacePos = prevScreenSpacePos * 0.5 + 0.5;
    return screenSpacePos.xy - prevScreenSpacePos.xy;
}
vec3 calculateTangentNormalFromHeightmap(sampler2D heightmapTexture, vec2 heightmapUV, float mipLevel) {
    vec3 tangentNormal = vec3(0.f, 0.f, 1.f);
    const float kHeightMapPixelEdgeWidth = 1.0f / 12.0f;
    const float kHeightMapDepth = 4.0f;
    const float kRecipHeightMapDepth = 1.0f / kHeightMapDepth;
    float fadeForLowerMips = saturatedLinearRemapZeroToOne(mipLevel, 2.f, 1.f);
    if (fadeForLowerMips > 0.f)
    {
        vec2 widthHeight = vec2(textureSize(heightmapTexture, 0));
        vec2 pixelCoord = heightmapUV * widthHeight;
        {
            const float kNudgePixelCentreDistEpsilon = 0.0625f;
            const float kNudgeUvEpsilon = 0.25f / 65536.f;
            vec2 nudgeSampleCoord = fract(pixelCoord);
            if (abs(nudgeSampleCoord.x - 0.5) < kNudgePixelCentreDistEpsilon)
            {
                heightmapUV.x += (nudgeSampleCoord.x > 0.5f) ? kNudgeUvEpsilon : -kNudgeUvEpsilon;
            }
            if (abs(nudgeSampleCoord.y - 0.5) < kNudgePixelCentreDistEpsilon)
            {
                heightmapUV.y += (nudgeSampleCoord.y > 0.5f) ? kNudgeUvEpsilon : -kNudgeUvEpsilon;
            }
        }
        vec4 heightSamples = textureGather(heightmapTexture, heightmapUV, 0);
        vec2 subPixelCoord = fract(pixelCoord + 0.5f);
        const float kBevelMode = 0.0f;
        vec2 axisSamplePair = (subPixelCoord.y > 0.5f) ? heightSamples.xy : heightSamples.wz;
        float axisBevelCentreSampleCoord = subPixelCoord.x;
        axisBevelCentreSampleCoord += ((axisSamplePair.x > axisSamplePair.y) ? kHeightMapPixelEdgeWidth : -kHeightMapPixelEdgeWidth) * kBevelMode;
        ivec2 axisSampleIndices = ivec2(clamp(vec2(axisBevelCentreSampleCoord - kHeightMapPixelEdgeWidth, axisBevelCentreSampleCoord + kHeightMapPixelEdgeWidth) * 2.f, 0.0, 1.0));
        tangentNormal.x = (axisSamplePair[axisSampleIndices.x] - axisSamplePair[axisSampleIndices.y]);
        axisSamplePair = (subPixelCoord.x > 0.5f) ? heightSamples.zy : heightSamples.wx;
        axisBevelCentreSampleCoord = subPixelCoord.y;
        axisBevelCentreSampleCoord += ((axisSamplePair.x > axisSamplePair.y) ? kHeightMapPixelEdgeWidth : -kHeightMapPixelEdgeWidth) * kBevelMode;
        axisSampleIndices = ivec2(clamp(vec2(axisBevelCentreSampleCoord - kHeightMapPixelEdgeWidth, axisBevelCentreSampleCoord + kHeightMapPixelEdgeWidth) * 2.f, 0.0, 1.0));
        tangentNormal.y = (axisSamplePair[axisSampleIndices.x] - axisSamplePair[axisSampleIndices.y]);
        tangentNormal.z = kRecipHeightMapDepth;
        tangentNormal = normalize(tangentNormal);
        tangentNormal.xy *= fadeForLowerMips;
    }
    return tangentNormal;
}
vec2 getPBRDataUV(vec2 surfaceUV, vec2 uvScale, vec2 uvBias) {
    return (((surfaceUV) * (uvScale)) + uvBias);
}

#endif

void main() {

    vec4 ViewRect = u_viewRect;
    mat4 Proj = u_proj;
    mat4 View = u_view;
    vec4 ViewTexel = u_viewTexel;
    mat4 InvView = u_invView;
    mat4 InvProj = u_invProj;
    mat4 ViewProj = u_viewProj;
    mat4 InvViewProj = u_invViewProj;
    mat4 PrevViewProj = u_prevViewProj;

    mat4 World = u_model[0];
    mat4 WorldView = u_modelView;
    mat4 WorldViewProj = u_modelViewProj;
    vec4 PrevWorldPosOffset = u_prevWorldPosOffset;
    vec4 AlphaRef4 = u_alphaRef4;
    float AlphaRef = u_alphaRef4.x;

//    StandardTemplate_Opaque_Frag(fragmentInput, fragmentOutput);

    vec3 bitangent = v_bitangent;
    vec4 color0 = v_color0;
    vec2 lightmapUV = v_lightmapUV;
    vec3 normal = v_normal;
    vec3 tangent = v_tangent;
    vec2 texcoord0 = v_texcoord0;
    vec3 worldPos = v_worldPos;

    #if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
        int frontFacing = int(gl_FrontFacing);
        int pbrTextureId = v_pbrTextureId;
    #endif

    // fragmentOutput.Color0 = vec4(0, 0, 0, 0); 
    // fragmentOutput.Color1 = vec4(0, 0, 0, 0); 
    // fragmentOutput.Color2 = vec4(0, 0, 0, 0);

    // texcoord0 = fragInput.texcoord0;
    // surfaceInput.Color = fragInput.color0.xyz;
    // surfaceInput.Alpha = fragInput.color0.a;

// struct StandardSurfaceOutput 
    // vec3 Albedo = vec3(1, 1, 1);
    // float Alpha = 1.0;

    // float Metallic = 0.0;
    // float Roughness = 1.0;
    // float Emissive = 0.0;
    // float Subsurface = 0.0;

    // float Occlusion = 0.0;

    // vec3 AmbientLight = vec3(0.0, 0.0, 0.0);
    // vec3 ViewSpaceNormal = vec3(0, 1, 0);

#if defined(DEPTH_ONLY)||defined(DEPTH_ONLY_OPAQUE)

    #ifdef DEPTH_ONLY //AlphaTest
        vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);
        const float ALPHA_THRESHOLD = 0.5;
        if (diffuse.a < ALPHA_THRESHOLD) {
            discard;
        };
    #endif

    #ifdef DEPTH_ONLY_OPAQUE
        //nothing
    #endif

    gl_FragData[0] = vec4(0.0,0.0,0.0,0.0);
    return;

#endif


#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);

    #ifdef GEOMETRY_PREPASS_ALPHA_TEST
        const float ALPHA_THRESHOLD = 0.5;
        if (diffuse.a < ALPHA_THRESHOLD) {
            discard;
        };
    #endif

    #ifdef SEASONS
    //applySeasons
        diffuse.rgb *= mix(
            vec3(1.0, 1.0, 1.0), 
            texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0, 
            v_color0.b
        );
        diffuse.rgb *= v_color0.aaa;
        diffuse.a = 1.0;
    #else
        diffuse.rgb *= v_color0.rgb;
        diffuse.a *= v_color0.a;
    //
    #endif

    // surfaceOutput.Albedo = diffuse.rgb;
    // surfaceOutput.Alpha = diffuse.a;
#endif

const int kInvalidPBRTextureHandle = 0xffff;
const int kPBRTextureDataFlagHasMaterialTexture = (1 << 0);
const int kPBRTextureDataFlagHasSubsurfaceChannel = (1 << 1);
const int kPBRTextureDataFlagHasNormalTexture = (1 << 2);
const int kPBRTextureDataFlagHasHeightMapTexture = (1 << 3);

#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)

//applyPBRValuesToSurfaceOutput
    if (pbrTextureId == kInvalidPBRTextureHandle) {
        return;
    };
    PBRTextureData pbrTextureData = s_PBRData[v_pbrTextureId];
    vec2 normalUVScale = vec2(pbrTextureData.colourToNormalUvScale0, pbrTextureData.colourToNormalUvScale1);
    vec2 normalUVBias = vec2(pbrTextureData.colourToNormalUvBias0, pbrTextureData.colourToNormalUvBias1);
    vec2 materialUVScale = vec2(pbrTextureData.colourToMaterialUvScale0, pbrTextureData.colourToMaterialUvScale1);
    vec2 materialUVBias = vec2(pbrTextureData.colourToMaterialUvBias0, pbrTextureData.colourToMaterialUvBias1);

    vec3 tangentNormal = vec3(0, 0, 1);
    if ((pbrTextureData.flags & kPBRTextureDataFlagHasNormalTexture) == kPBRTextureDataFlagHasNormalTexture)
    {
        vec2 uv = getPBRDataUV(texcoord0, normalUVScale, normalUVBias);
        tangentNormal = texture2D(s_MatTexture, uv).xyz * 2.f - 1.f;
    } 
    else if ((pbrTextureData.flags & kPBRTextureDataFlagHasHeightMapTexture) == kPBRTextureDataFlagHasHeightMapTexture)
    {
        vec2 normalUv = getPBRDataUV(texcoord0, normalUVScale, normalUVBias);
        float normalMipLevel = min(pbrTextureData.maxMipNormal - pbrTextureData.maxMipColour, pbrTextureData.maxMipNormal);
        tangentNormal = calculateTangentNormalFromHeightmap(s_MatTexture, normalUv, normalMipLevel);
    };

    float metalness = pbrTextureData.uniformMetalness;
    float emissive = pbrTextureData.uniformEmissive;
    float linearRoughness = pbrTextureData.uniformRoughness;
    float subsurface = pbrTextureData.uniformSubsurface;

    if ((pbrTextureData.flags & kPBRTextureDataFlagHasMaterialTexture) == kPBRTextureDataFlagHasMaterialTexture)
    {
        vec2 uv = getPBRDataUV(texcoord0, materialUVScale, materialUVBias);
        vec4 texel = texture2D(s_MatTexture, uv).rgba;
        // Here Modify
        //texel = vec4(0.9, texel.g, 0.0, 0.5);
        
        metalness = texel.r;
        emissive = texel.g;
        linearRoughness = texel.b;
        if ((pbrTextureData.flags & kPBRTextureDataFlagHasSubsurfaceChannel) == kPBRTextureDataFlagHasSubsurfaceChannel) {
            subsurface = texel.a;
        };
    };
    
    if (frontFacing != 0) {
        normal = -normal;
    };
    mat3 tbn = mtxFromRows(
        normalize(tangent),
        normalize(bitangent),
        normalize(normal)
    );
    tbn = transpose(tbn);
  
    // surfaceOutput.Metallic = metalness;  
    // surfaceOutput.Roughness = linearRoughness;
    // surfaceOutput.Emissive = emissive;
    // surfaceOutput.Subsurface = subsurface;

    //surfaceOutput.ViewSpaceNormal = mul(tbn, tangentNormal).xyz;
//
#endif

//primaryLight
    // vec3 worldLightDirection = LightWorldSpaceDirection.xyz;
    // vec3 ViewSpaceDirection = mul(u_view, vec4(worldLightDirection, 0)).xyz;
    // vec3 Intensity = LightDiffuseColorAndIlluminance.rgb * LightDiffuseColorAndIlluminance.w;
  
    //compositingOutput.mLitColor

    // mLitColor = stdOutput.Albedo;
    // diffuse = vec4(mLitColor.xyz, stdOutput.Alpha);

    // vec3 mLitColor = diffuse.rgb
    // diffuse = vec4(mLitColor.xyz, diffuse.a);

//RenderChunkFinalColorFallback


vec4 fragOutput_0 = vec4(0.0,0.0,0.0,0.0);
vec4 fragOutput_1 = vec4(0.0,0.0,0.0,0.0); 
vec4 fragOutput_2 = vec4(0.0,0.0,0.0,0.0);
//     gl_FragData[0] = fragOutput_0;
//     gl_FragData[1] = fragOutput_1;
//     gl_FragData[2] = fragOutput_2;

#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
// RenderChunkGeometryPrepass
// applyPrepassSurfaceToGBuffer(

    gl_FragData[0].rgb = diffuse.rgb;
    gl_FragData[0].a = packMetalnessSubsurface(metalness, subsurface);

    vec3 worldPosition = worldPos.xyz;
    vec3 prevWorldPosition = worldPos.xyz - u_prevWorldPosOffset.xyz;

    vec3 ViewSpaceNormal = mul(tbn, tangentNormal).xyz;
    vec3 viewNormal = normalize(ViewSpaceNormal).xyz;

    gl_FragData[1].xy = ndirToOctSnorm(viewNormal);
    gl_FragData[1].zw = calculateMotionVector(worldPosition, prevWorldPosition);
   
    float ambientBlockLight = lightmapUV.x;
    float ambientSkyLight = lightmapUV.y;
    gl_FragData[2] = vec4(
        emissive,
        ambientBlockLight,
        ambientSkyLight,
        linearRoughness
    );
//
#else

    // gl_FragData[0] = fragmentOutput.Color0; 
    // gl_FragData[1] = fragmentOutput.Color1; 
    // gl_FragData[2] = fragmentOutput.Color2; 

    gl_FragData[0] = vec4(0.0,0.0,0.0,0.0);
    gl_FragData[1] = vec4(0.0,0.0,0.0,0.0);
    gl_FragData[2] = vec4(0.0,0.0,0.0,0.0);

#endif
}

