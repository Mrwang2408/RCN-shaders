// https://github.com/OEOTYAN/useless-shaders/blob/master/shaders/glsl/renderchunk.fragment


$input v_color0, v_texcoord0, v_lightmapUV, v_position, v_worldpos, v_fog

#ifndef NO_FOG


#endif


#include <bgfx_shader.sh>
#include <defines.sh>
#include <RCN_overlays.h>
#include <RCN_glow.h>
#include <RCN_apply.h>


SAMPLER2D(s_MatTexture, 0);
SAMPLER2D(s_SeasonsTexture, 1);
SAMPLER2D(s_LightMapTexture, 2);

uniform vec4 ViewPositionAndTime;


void main() {
    vec4 diffuse;
	vec4 color = v_color0;
	float time = ViewPositionAndTime.w;
	
	
	
#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
    diffuse.rgb = vec3(1.0, 1.0, 1.0);
	color = vec4(1.0,1.0,1.0,1.0);
#else
	diffuse = texture2D(s_MatTexture, v_texcoord0);
	#ifdef ALPHA_TEST
		bool needDiscard = false;
		if (diffuse.a < 0.5) {
			needDiscard = true;
		};
	#endif
	
	#if defined(SEASONS) && (defined(OPAQUE) || defined(ALPHA_TEST))
		diffuse.rgb *= mix(vec3(1.0, 1.0, 1.0), texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0, v_color0.b);
		diffuse.rgb *= v_color0.aaa;
	#else
		//diffuse *= v_color0;
	#endif
	
	
	vec2 lightUV = v_lightmapUV;
	bool needLightMap = true;
	
	vec3 chunkPos = v_position;  //chunkPos：每区块坐标（16x16x16）
	
	//vec3 normal = normalize(cross(dFdx(chunkPos), dFdy(chunkPos)));
	bool isNormal = (normalize(cross(dFdx(chunkPos), dFdy(chunkPos))).y > 0.99);
	//bool isNormal = (abs(normal.x) > 0.99 || abs(normal.y) > 0.99 || abs(normal.z) > 0.99);
	//bool isRsDust = (isNormal && (v_color0.r > v_color0.g + v_color0.b));
	bool isRsDust = (v_color0.r > v_color0.g + v_color0.b);
	
	float checkDistance = length(v_worldpos);	
	
	bool isRsOverlay = false;
	int setChunkBorder = 0;
	int setLiOverlay = 0;
	
		
	/*	
		//lightUV.x = 1.0 - lightUV.x;
		//lightUV.y = 1.0 - lightUV.y;
		//lightUV.x = 1.0;
		
		if (checkDistance <= 16.0) {
			lightUV.x += (1.0625 - (checkDistance / 16.0));
		};
	*/	
	
	//if (isNormal) {diffuse.rgb = vec3(0.0,1.0,0.0);};
	
	//diffuse.rgb = abs(normal.rgb);
	
	
#endif

#ifndef TRANSPARENT
	diffuse.a =  v_color0.a;
	//diffuse.a =  1.0;
#endif




#if (defined(OPAQUE) || defined(ALPHA_TEST) || defined(TRANSPARENT)) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))

	#ifdef ORE_TEST
	vec4 oreTest = texture2DLod(s_MatTexture, v_texcoord0, 0.0);
  

   // vec3 glow = nlGlow(s_MatTexture, v_texcoord0, diffuse, v_extra.a);


  
    if (GLOW_PIXEL(oreTest)) {
    color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-oreTest.a)/(0.995-0.9875));
	//diffuse.rgb = max(diffuse.rgb, NL_GLOW_TEX*(0.995-oreTest.a)/(0.995-0.9875));
  };
  
    diffuse.rgb *= color.rgb;
	
	//diffuse.rgb += glow;
		
		
  /*
if (!(oreTest.a == 1.00)){
	if (oreTest.a > 0.03 && oreTest.a < 0.06) {
		diffuse.rgb = ((vec3(1.0, 1.0, 1.0)+0.95) * diffuse.rgb);
		//diffuse.rgb = (vec3(1.0, 0.0, 0.0);
		needLightMap = false;
	};
	if (oreTest.a > 0.9875 && oreTest.a < 0.9925) {
		diffuse.rgb = ((vec3(1.0, 1.0, 1.0)+0.45) * diffuse.rgb);
		//diffuse.rgb = (vec3(0.0, 1.0, 0.0);
		needLightMap = false;
	};
	if (oreTest.a > 0.948 && oreTest.a < 0.966) {
		diffuse.rgb = ((vec3(0.6, 0.6, 0.6)+0.3) * diffuse.rgb);
		//diffuse.rgb = (vec3(0.0, 0.0, 1.0);
		needLightMap = false;
	};
	
	#ifdef OPAQUE
		if (oreTest.a < 0.02) {
			//discard;
		};
	#endif
	
};
*/

	//vec3 glow = nlGlow(s_MatTexture, v_texcoord0, diffuse, 1.0);


	#endif
	
#endif



			//     aaaaaaa
			//    f       b
			//    f       b
			//    f       b
			//     ggggggg
			//    e       c
			//    e       c
			//    e       c
			//     ddddddd
			//


#if defined(ALPHA_TEST) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))

	#ifdef REDSTONE_OVERLAY
	if (isRsDust) {
		needLightMap = false;  //使红石粉发光
		if (isNormal && isRsDust) {
			isRsOverlay = runRsOverlay(v_color0.rgb, chunkPos);  //运行函数
		};
		if (isRsOverlay) {
			needDiscard = false;  //修复字体被裁剪的问题
			needLightMap = false;  //使字体不再变暗
		};
	};
	#endif
	
#endif

#if ((defined(OPAQUE) || defined(TRANSPARENT)) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))) || (defined(ALPHA_TEST) && !(defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS)))

	#ifdef LIGHT_OVERLAY
	if(isNormal &&
		#ifdef ALPHA_TEST
		!(isRsDust) && 
		#endif
		#ifdef OPAQUE
		checkDistance < 32.0
		#else
		checkDistance < 16.0
		#endif
	) {
		setLiOverlay = runLiOverlay(lightUV, chunkPos);  //运行函数
		#ifdef ALPHA_TEST
		if (setLiOverlay != 0) {
			needDiscard = false;  
			//needLightMap = false; 
		};
		#endif
	};
	#endif

#endif

#ifdef ALPHA_TEST
    if (needDiscard) {
        discard;
		return;
    };
#endif



#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
	diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;
#else
	#ifndef NIGHT_VISION
	if (needLightMap) {
		diffuse.rgb *= texture2D(s_LightMapTexture,lightUV).rgb;
		//diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;  //设置夜视
	};
	#endif
#endif



#if ((defined(OPAQUE) || defined(TRANSPARENT)) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))) || (defined(ALPHA_TEST) && !(defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS)))

	#ifdef CHUNK_BORDERS
	setChunkBorder = runChunkBorder(chunkPos.xyz);  //运行函数
	diffuse.rgb = applyChunkBorder(diffuse.rgb, setChunkBorder);
	#endif
	
	#ifdef REDSTONE_OVERLAY
	//if (isNormal && isRsDust && isRsOverlay) {
	if (isRsDust) {
		diffuse.rgb = applyRsOverlay(diffuse.rgb, isRsOverlay);
	};
	#endif
		
	#ifdef LIGHT_OVERLAY
	if (isNormal && checkDistance < 32.0) {
		diffuse.rgba = applyLiOverlay(diffuse.rgba, setLiOverlay);
	};
	#endif
	
#endif



#ifndef NO_FOG

    diffuse.rgb = mix(diffuse.rgb,v_fog.rgb,v_fog.a);
	
#endif




//============================================================================
//============================================================================
//============================================================================




#ifdef DEBUG_UV

    bool isWater = false;
    #if !defined(SEASONS) && !defined(ALPHA_TEST)
        isWater = v_color0.a > 0.4 && v_color0.a < 0.6;
    #endif
		

		
	//查看 texture atlas 纹理图集
	
	vec2 debuguv ;
//	debuguv.x = v_worldpos.x / 4.0 + 0.5;
//	debuguv.y = v_worldpos.z / 8.0 + 0.3;
	debuguv.x = v_worldpos.x / 8.0 + 0.5;
	debuguv.y = v_worldpos.z / 4.0 + 0.4;

	if (debuguv.x > 0.0 && debuguv.y > 0.0 && debuguv.x < 1.0 && debuguv.y < 1.0 &&
	(v_position.y > 15.99 || isWater)){
		diffuse = texture2D(s_MatTexture, debuguv);
	};	
	

	

	if (debuguv.y>0.406 && debuguv.y<0.532){
		//diffuse.rgb=vec3(3.0,3.0,3.0);
	};
	if (v_texcoord0.y>0.406 && v_texcoord0.y<0.532){
		//diffuse.rgb=vec3(3.0,3.0,3.0);
	};
	
	//diffuse.rgb = floor(v_position.xyz)/vec3(16.0,16.0,16.0);
	
	#if (defined(OPAQUE) || defined(ALPHA_TEST) || defined(TRANSPARENT)) 
	bPos = fract(v_position.xyz);
	if (
	((bPos.x < 0.03125 || bPos.x > 0.96875) && (bPos.y < 0.03125 || bPos.y > 0.96875)) || 
	((bPos.x < 0.03125 || bPos.x > 0.96875) && (bPos.z < 0.03125 || bPos.z > 0.96875)) ||
	((bPos.y < 0.03125 || bPos.y > 0.96875) && (bPos.z < 0.03125 || bPos.z > 0.96875))) {
		
		//diffuse.rgb = ViewPositionAndTime.www;
		
	};
	#endif
	
	
	//基于纹理图集的马赛克效果低清材质包
	
	vec2 matTest;
	vec4 matTest2;
	
	float grade = 1.0 * 1.0;
	
//	matTest = floor(v_texcoord0 * vec2(grade, grade*2.0)) / vec2(grade, grade*2.0);
//	matTest = floor(v_texcoord0 * vec2(grade*2.0, grade)) / vec2(grade*2.0, grade);
//	matTest2 = texture2D(s_MatTexture,  matTest);
	
//	matTest2 = texture2D(s_MatTexture, v_texcoord0 - vec2(0.03125*1.0, 0.03125*0.5));
//	matTest2 = texture2D(s_MatTexture, v_texcoord0 - vec2(0.03125*0.5, 0.03125*1.0));

//	matTest2.a = 1.0;
//	matTest2.rgb *= v_color0.rgb;
//	diffuse.rgb = matTest2.rgb;

	
	#ifdef ALPHA_TEST
	if (v_color0.g*1.5>v_color0.r+v_color0.b||v_color0.r*1.5>v_color0.g+v_color0.b) {
		//diffuse.rgb = vec3(0.0,0.0,1.0);
	};
	#endif


#endif	



	//	diffuse.rgb = v_color0.rgb;
	//	diffuse.rgb = v_color0.aaa;
		
	//	diffuse.a =  v_color0.a;
	//	diffuse.a =  1.0;
	
	
	vec2 uv0 = v_texcoord0;
	if (fract(v_position.y) < 0.01) {
		
	//	diffuse.rgb = texture2D(s_MatTexture, v_position.xz/16.0).rgb;	
	//	diffuse.rgb = texture2D(s_MatTexture, v_position.xz/vec2(512.0,256.0)).rgb;
	
	//	float uvt1 = fract(((uv0.y-=((uv0.y>0.0234375)?0.0234375:0.0)) * 25.0));
	//	float uvt1 = fract(uv0.y * 25.0);
	//	diffuse.rgb = vec3(uvt1,uvt1,uvt1);

	//	diffuse.rgb = texture2D(s_MatTexture, fract(v_position.xz)).rgb;	
	//	diffuse.rgb = texture2D(s_LightMapTexture, v_position.xz/16.0).rgb;		
	//	diffuse.rgb = texture2D(s_SeasonsTexture, v_position.xz/16.0).rgb;	
		
	}
	
	//	diffuse = vec4(1.0,1.0,1.0,1.0);
	//	diffuse = vec4(0.5,0.5,0.5,1.0);
	//	diffuse = vec4(0.0);
	
    gl_FragColor = diffuse;
} 











