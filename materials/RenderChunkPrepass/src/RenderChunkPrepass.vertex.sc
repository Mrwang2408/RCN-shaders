$input a_color0, a_normal, a_position, a_tangent, a_texcoord0, a_texcoord1
#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
    $input a_texcoord4
#endif
#ifdef INSTANCING
    $input i_data1, i_data2, i_data3
#endif

$output v_bitangent, v_color0, v_lightmapUV, v_normal, v_tangent, v_texcoord0, v_position, v_worldPos
#if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
    $output v_frontFacing, v_pbrTextureId
#endif

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>

uniform vec4 GlobalRoughness;
uniform vec4 LightDiffuseColorAndIlluminance;
uniform vec4 LightWorldSpaceDirection;
uniform vec4 MaterialID;
// uniform vec4 SubPixelOffset;  //TAAUtil
uniform vec4 ViewPositionAndTime;

//#define a_texcoord1 vec2(fract(a_texcoord1.x*15.9375)+0.0001,floor(a_texcoord1.x*15.9375)*0.0625+0.0001)

void main() {
    vec3 worldPos;

    #if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
        int frontFacing = 0;
        int pbrTextureId = 0;
    #endif

    vec4 position;
    vec2 texcoord0 = a_texcoord0;
    vec4 color0 = a_color0;
    vec2 lightmapUV = a_texcoord1;
    // mat4 model = u_model[0];

    #ifdef INSTANCING
        mat4 model;
        model[0] = vec4(i_data1.x, i_data2.x, i_data3.x, 0);
        model[1] = vec4(i_data1.y, i_data2.y, i_data3.y, 0);
        model[2] = vec4(i_data1.z, i_data2.z, i_data3.z, 0);
        model[3] = vec4(i_data1.w, i_data2.w, i_data3.w, 1);
        worldPos = instMul(model, vec4(a_position, 1.0)).xyz;
    #else
        worldPos = mul(u_model[0], vec4(a_position, 1.0)).xyz;
    #endif

    #ifdef RENDER_AS_BILLBOARDS
        color0 = vec4(1.0, 1.0, 1.0, 1.0);
        //transformAsBillboardVertex
        worldPos += vec3(0.5, 0.5, 0.5);
        vec3 forward = normalize(worldPos - ViewPositionAndTime.xyz);
        vec3 right = normalize(cross(vec3(0.0, 1.0, 0.0), forward));
        vec3 up = cross(forward, right);
        vec3 offsets = color0.xyz;
        worldPos -= up * (offsets.z - 0.5) + right * (offsets.x - 0.5);
    #endif

    vec3 normal = vec3(0.0, 0.0, 0.0);
    vec3 tangent = vec3(0.0, 0.0, 0.0);
    vec3 bitangent = vec3(0.0, 0.0, 0.0);
    float cameraDepth = length(ViewPositionAndTime.xyz - worldPos);
 
    #if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
        position = jitterVertexPosition(worldPos);  //TAAUtil
        pbrTextureId = int(a_texcoord4) & 0xffff;
        vec3 n = a_normal.xyz;
        vec3 t = a_tangent.xyz;
        vec3 b = cross(n, t) * a_tangent.w;
        normal = mul(u_model[0], vec4(n, 0.0)).xyz;
        tangent = mul(u_model[0], vec4(t, 0.0)).xyz;
        bitangent = mul(u_model[0], vec4(b, 0.0)).xyz;
    #else
        position = mul(u_viewProj, vec4(worldPos, 1.0));
    #endif

    v_bitangent = bitangent;
    v_color0 = color0;
    v_lightmapUV = lightmapUV;
    v_normal = normal;
    v_tangent = tangent;
    v_texcoord0 = texcoord0;
    v_worldPos = worldPos;

    v_position = a_position;

    #if defined(GEOMETRY_PREPASS_ALPHA_TEST)||defined(GEOMETRY_PREPASS)
        v_frontFacing = frontFacing;
        v_pbrTextureId = pbrTextureId;
    #endif
    
    gl_Position = position;
}