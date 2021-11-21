#include "ShaderConstants.fxh"

struct VS_Input
{
    float3 position : POSITION;
    float4 color : COLOR;
};


struct PS_Input
{
    float4 position : SV_Position;
    float4 color : COLOR;
    float3 worldpos : worldpos;
};

ROOT_SIGNATURE
void main(in VS_Input VSInput, out PS_Input PSInput)
{
	//VSInput.position.y += abs(VSInput.position.x) > 0.001f ? -.628f : .372f;
    PSInput.worldpos = VSInput.position;
	PSInput.position = mul(WORLDVIEWPROJ, float4(VSInput.position, 1));
	PSInput.position.z=PSInput.position.w*0.99999;
    PSInput.color = lerp( CURRENT_COLOR, FOG_COLOR, VSInput.color.r );

}