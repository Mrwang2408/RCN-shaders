$input a_position, a_color0, a_texcoord0, a_indices, a_normal
#ifdef INSTANCING
    $input i_data0, i_data1, i_data2
#endif

$output v_color0, v_fog, v_light, v_texcoord0

#include <bgfx_shader.sh>
#include <defines.sh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>

uniform vec4 ColorBased;
uniform vec4 ChangeColor;
uniform vec4 UseAlphaRewrite;
uniform vec4 TintedAlphaTestEnabled;
uniform vec4 MatColor;
uniform vec4 OverlayColor;
uniform vec4 TileLightColor;
uniform vec4 MultiplicativeTintColor;
uniform vec4 FogColor;
uniform vec4 FogControl;
uniform vec4 ActorFPEpsilon;
uniform vec4 LightDiffuseColorAndIntensity;
uniform vec4 LightWorldSpaceDirection;
uniform vec4 HudOpacity;
uniform vec4 UVAnimation;
uniform mat4 Bones[8];

void main() {
    mat4 World = u_model[0];
    
    //StandardTemplate_InvokeVertexPreprocessFunction
    World = mul(World, Bones[int(a_indices)]);

    vec2 texcoord0 = a_texcoord0;
    texcoord0 = applyUvAnimation(texcoord0, UVAnimation);

    float lightIntensity = calculateLightIntensity(World, vec4(a_normal.xyz, 0.0), TileLightColor);
    lightIntensity += OverlayColor.a * 0.35;
    vec4 light = vec4(lightIntensity, lightIntensity, lightIntensity, 1.0);
    #ifndef NIGHT_VISION
    light.rgb *= TileLightColor.rgb;
    #endif
    
    //StandardTemplate_VertSharedTransform
    vec3 worldPosition;
    #ifdef INSTANCING
        mat4 model = mtxFromCols(i_data0, i_data1, i_data2, vec4(0.0, 0.0, 0.0, 1.0));
        worldPosition = instMul(model, vec4(a_position, 1.0)).xyz;
    #else
        worldPosition = mul(World, vec4(a_position, 1.0)).xyz;
    #endif
    
    vec4 position;// = mul(u_viewProj, vec4(worldPosition, 1.0));

    //StandardTemplate_InvokeVertexOverrideFunction
    position = jitterVertexPosition(worldPosition);
    float cameraDepth = position.z;
    float fogIntensity = calculateFogIntensity(cameraDepth, FogControl.z, FogControl.x, FogControl.y);
    vec4 fog = vec4(FogColor.rgb, fogIntensity);

    #if defined(DEPTH_ONLY)
        v_texcoord0 = vec2(0.0, 0.0);
        v_color0 = vec4(0.0, 0.0, 0.0, 0.0);
    #else
        v_texcoord0 = texcoord0;
        v_color0 = a_color0;
    #endif

    v_fog = fog; 
    v_light = light;
    gl_Position = position;
}
