// https://github.com/OEOTYAN/useless-shaders/blob/master/shaders/glsl/renderchunk.fragment

$input v_color0, v_texcoord0, v_lightmapUV, v_position, v_worldpos, v_fog

#include <bgfx_shader.sh>
#include <defines.sh>
#include <RCN_config.h>
#include <RCN_apply.h>
//#include <RCN_glow.h>

//precision mediump float;
//precision highp int;

SAMPLER2D(s_LightMapTexture, 0);
SAMPLER2D(s_MatTexture, 1);
SAMPLER2D(s_SeasonsTexture, 2);

uniform vec4 RenderChunkFogAlpha;
uniform vec4 FogAndDistanceControl;
uniform vec4 ViewPositionAndTime;
uniform vec4 FogColor;

void main() {
    vec4 diffuse;
	vec4 lightmap;
	vec4 color = v_color0;
	vec2 lightUV = v_lightmapUV;
	vec3 chunkPos = v_position;  //chunkPos：每区块坐标（16x16x16）
	
	vec3 worldPos = v_worldpos.xyz;
	
	float time = ViewPositionAndTime.w;
	
	vec4 albedo = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 texCol = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 result = vec4(0.0, 0.0, 0.0, 0.0);
	
	/*
	vec4 FogColor = v_fog;
	vec3 cpos = v_position.xyz;
	vec3 wpos = v_worldpos.xyz;
	vec2 fogControl = v_FogC.xy;  //fogc  //fogControl
	float waterFlag = v_FogC.z;  //wflag  //waterFlag
	*/
	
#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
    diffuse.rgb = vec3(1.0, 1.0, 1.0);
	color = vec4(1.0,1.0,1.0,1.0);
	// return;
#else
	diffuse = texture2D(s_MatTexture, v_texcoord0);
	lightmap = texture2D(s_LightMapTexture, v_lightmapUV);
	texCol = diffuse;
	albedo = texCol * color;
	
	bool needDiscard = false;
	#ifdef ALPHA_TEST
		//bool needDiscard = false;
		if (diffuse.a < 0.5) {
			needDiscard = true;
		};
	#endif //ALPHA_TEST
	
	#if defined(SEASONS) && (defined(ALPHA_TEST) || defined(OPAQUE))
		diffuse.rgb *= mix(vec3(1.0, 1.0, 1.0), texture2D(s_SeasonsTexture, color.xy).rgb * 2.0, color.b);
		diffuse.rgb *= color.aaa;
	#else
		diffuse *= color;
		//diffuse.rgb *= color.rgb;
		//diffuse.a = color.a;
		//diffuse.rgb *= color.aaa;  //1.21.120.23
	#endif //SEASONS
	
	
	bool needLightMap = true;
	
	
	//vec3 normal = normalize(cross(dFdx(chunkPos), dFdy(chunkPos)));
	bool isNormal = (normalize(cross(dFdx(chunkPos), dFdy(chunkPos))).y > 0.99);
	//bool isNormal = (abs(normal.x) > 0.99 || abs(normal.y) > 0.99 || abs(normal.z) > 0.99);
	//bool isRsDust = (isNormal && (color.r > color.g + color.b));
	bool isRsDust = (color.r > color.g + color.b);
	
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
	
	
#endif //DEPTH_ONLY
	
	
#ifndef TRANSPARENT
	diffuse.a = color.a;
	//diffuse.a =  1.0;
#endif //TRANSPARENT
	
	
	// (14/255) - (35/255)
	// factor = (x*255.0-14)/(35-14)
	vec3 baselight = texture2D(s_LightMapTexture, vec2(0.0, 0.0)).rgb;
	if (baselight.r < 0.141176) {
		float setlight = baselight.r * 12.142857 - 0.666666;
		//lightmap.rgb = lightmap.rgb * (1.0 - setlight) + setlight;
		lightmap.rgb = mix(lightmap.rgb, vec3(1.0,1.0,1.0), setlight);
 	};
	
	
//RCN_glow
#ifdef ORE_TEST
	vec4 oreTest = texture2DLod(s_MatTexture, v_texcoord0, 0.0);
	//int oreTest = int(texture2DLod(s_MatTexture, v_texcoord0, 0.0).a * 255.0);
	// vec3 glow = nlGlow(s_MatTexture, v_texcoord0, diffuse, v_extra.a);
	/*
    if (GLOW_PIXEL(oreTest)) {
    color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-oreTest.a)/(0.995-0.9875));
	//diffuse.rgb = max(diffuse.rgb, NL_GLOW_TEX*(0.995-oreTest.a)/(0.995-0.9875));
	};
	*/
	//diffuse.rgb *= color.rgb;
	//diffuse.rgb += glow;
	
	/*
//if (!(oreTest.a == 1.00)){
//255=1.00, 254=0.996078, 253=0.992156, 252=0.988235, 251=0.984313
#ifndef TRANSPARENT
if (oreTest.a < 0.995){
	if (oreTest.a > 0.03 && oreTest.a < 0.06) {
		//diffuse.rgb = ((vec3(1.0, 1.0, 1.0)+0.95) * diffuse.rgb);
		//diffuse.rgb = (vec3(1.0, 0.0, 0.0);
		//needLightMap = false;
	};
	if (oreTest.a > 0.9875 && oreTest.a < 0.9925) {
		//diffuse.rgb = ((vec3(1.0, 1.0, 1.0)+0.45) * diffuse.rgb);
		//diffuse.rgb = (vec3(0.0, 1.0, 0.0);
		//needLightMap = false;
	};
	if (oreTest.a > 0.948 && oreTest.a < 0.966) {
		//diffuse.rgb = ((vec3(0.6, 0.6, 0.6)+0.3) * diffuse.rgb);
		//diffuse.rgb = (vec3(0.0, 0.0, 1.0);
		//needLightMap = false;
	};
	if (oreTest.a > 0.9875 && oreTest.a < 0.995) {
		if (oreTest.a < 0.995 ) {
		//color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-diffuse.a)/(0.995-0.9875));
		};
	};
	#ifdef OPAQUE
		if (oreTest.a < 0.2) {
			//needDiscard = true;  //矿物透视Xray3
			//discard;
		};
	#endif //OPAQUE
};
#endif //TRANSPARENT
};
	*/
	/*
	vec3 glow = diffuse.rgb * diffuse.rgb * 0.5;
	if (oreTest == 252) {
		//diffuse.rgb += glow;
		//needLightMap = false;
		color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-diffuse.a)/(0.995-0.9875));
	} else if (oreTest == 253) {
		//diffuse.rgb += glow * 0.4;
		//needLightMap = false;
		color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-diffuse.a)/(0.995-0.9875));
	};
	if (oreTest < 252) {
		//diffuse.rgb = vec3(1.0);
		//needLightMap = false;
		color.rgb = max(color.rgb, NL_GLOW_TEX*(0.995-diffuse.a)/(0.995-0.9875));
		
	};
	*/
	// vec3 glow = nlGlow(s_MatTexture, v_texcoord0, diffuse, 1.0);
	
	//lightmap.rgb = 0.6 + 0.4 * lightmap.rgb;
	//lightmap.rgb = 0.3 + 0.7 * lightmap.rgb;
	//lightmap.rgb = 0.4375 + 0.5625 * lightmap.rgb;
	
	if (oreTest.a > 0.988 && oreTest.a < 0.993) {
		vec3 glow = oreTest.rgb * oreTest.rgb;
		if (oreTest.a > 0.989) {
			glow *= 0.375;
		};
		needLightMap = false;
		diffuse.rgb *= (2.0 - color.rgb);
		diffuse.rgb += glow * 0.5;
		diffuse.rgb *= 0.375 + 0.625 * lightmap.rgb;  //lightmap_factor
	};
	/*
	vec3 glow = glowDetect(oreTest.rgba) * NL_GLOW_TEX;
	//diffuse.rgb *= diffuse.rgb;
	//diffuse.rgb *= color.rgb;
	vec3 lightmap_factor = 0.6 + 0.4 * lightmap.rgb;
	if (glow.rgb != vec3(0.0)) {
		needLightMap = false;
		diffuse.rgb *= (2.0 - color.rgb);
		diffuse.rgb += glow;
		diffuse.rgb *= lightmap_factor;
	};
	*/
	
#endif //ORE_TEST
//endif_RCN
	
	//diffuse *= color;


//RCN_overlays
	#ifdef CHUNK_BORDERS
	setChunkBorder = runChunkBorder(chunkPos.xyz);  //运行函数
	#endif //CHUNK_BORDERS
	
	#ifdef REDSTONE_OVERLAY
	if (isRsDust) {
		needLightMap = false;  //使红石粉发光
		if (isNormal && isRsDust) {
			isRsOverlay = runRsOverlay(color.rgb, chunkPos);  //运行函数
		};
		if (isRsOverlay) {
			needDiscard = false;  //修复字体被裁剪的问题
			needLightMap = false;  //使字体不再变暗
		};
	};
	#endif //REDSTONE_OVERLAY

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
		//#ifdef ALPHA_TEST
		if (setLiOverlay != 0) {
			needDiscard = false;  
			//needLightMap = false; 
		};
		//#endif
	};
	#endif //LIGHT_OVERLAY
//endif_RCN


#ifdef ALPHA_TEST
    if (needDiscard) {
        discard;
		return;
    };
#endif //ALPHA_TEST

#ifdef OPAQUE
    if (needDiscard) {
        //discard;
		//return;
    };
#endif //OPAQUE


#if !(defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY))
	#ifndef NIGHT_VISION
	if (needLightMap) {
		//diffuse.rgb *= texture2D(s_LightMapTexture,lightUV).rgb;
		diffuse.rgb *= lightmap.rgb;  //设置夜视
	};
	#endif
#endif


//RCN_apply
//-----------------------------------
	//diffuse = result;
	#ifdef REDSTONE_OVERLAY  //RCN
	if (isRsDust) {
		diffuse.rgb=applyRsOverlay(albedo.rgb,isRsOverlay);
		vec3 ambient_factor = 0.6875 + 0.375 * lightmap.rgb;
		diffuse.rgb *= ambient_factor;
		//diffuse.r = (diffuse.r - 0.1) * 1.111;
		//diffuse.rgb = vec3(1.0);
		//diffuse.rgb = fract(chunkPos);
		
	};
	#endif  //REDSTONE_OVERLAY
//-----------------------------------
	#ifdef CHUNK_BORDERS //RCN
	if (setChunkBorder!=0){
		//diffuse.rgb=applyChunkBorder(diffuse.rgb,setChunkBorder);
		//diffuse.rgb = mix(diffuse.rgb + 0.5, vec3(1.0) - diffuse.rgb, smoothstep(0.3, 0.6, max(diffuse.r, max(diffuse.g, diffuse.b))));
		if (setChunkBorder == 1) {
		diffuse.rgb = ((diffuse.rgb * 2.5) * (vec3(1.0, 1.0, 1.0) - diffuse.rgb)); 
		} else {
		vec4 ChunkColor = checkChunkColor(setChunkBorder);
		diffuse.rgb = mix(diffuse.rgb, ChunkColor.rgb, ChunkColor.a);
		};
	};
	#endif  //CHUNK_BORDERS
//-----------------------------------
	#ifdef LIGHT_OVERLAY  //RCN
	if (checkDistance<32.0){
		//diffuse.rgba=applyLiOverlay(diffuse.rgba,setLiOverlay);
		vec4 LiOverlay = checkLiOverlay(setLiOverlay);
		diffuse.rgb = mix(diffuse.rgb, LiOverlay.rgb, LiOverlay.a);
	};
	#endif //LIGHT_OVERLAY
	//result = diffuse;
//-----------------------------------
//endif_RCN


#ifndef NO_FOG
    //diffuse.rgb = mix(diffuse.rgb,v_fog.rgb,v_fog.a);
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
		//diffuse = texture2D(s_MatTexture, debuguv);
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
	
	};
	
	//	diffuse = vec4(1.0,1.0,1.0,1.0);
	//	diffuse = vec4(0.5,0.5,0.5,1.0);
	//	diffuse = vec4(0.0);zSXzdsx
	
	//diffuse = result;
	
	//diffuse.rgb = texture2D(s_LightMapTexture, v_lightmapUV.xy/16.0).rgb;	
	
	float uvx = floor(v_lightmapUV.x * 255.0);
	float uvy = fract(v_lightmapUV.x * 255.0);
	//diffuse.rgb = vec3_splat(uvy);
	
	//diffuse.rgb = v_color0.rgb;
	//diffuse.rgb = v_color0.aaa;
	//diffuse.a = 1.0;
	
	//float gray = dot(diffuse.rgb, vec3(0.299, 0.587, 0.114));
	//diffuse.rgb = vec3_splat(gray);
	
	if ( dFdx(lightUV)+dFdy(lightUV) != lightUV*2.0 ) {
		//diffuse.rgb = vec3(1.0);
	}; 
	
	/*
	vec2 texCoords = v_texcoord0;
	vec2 dx = dFdx(texCoords);
	vec2 dy = dFdy(texCoords);
	float deltaMaxSqr = max(dot(dx, dx), dot(dy, dy));
	float mipLevel = 0.5 * log2(deltaMaxSqr);
	diffuse.rgb = vec3(mipLevel);
	*/
	/*
	float textureSize = 4096.0; // 假设纹理是1024x1024
    
    // 计算纹理坐标在屏幕空间中的梯度
    vec2 dx = dFdx(v_texcoord0);
    vec2 dy = dFdy(v_texcoord0);
    
    // 计算在纹理空间中的最大变化率
    float maxDelta = max(length(dx * textureSize), length(dy * textureSize));
    
    // 计算mipmap级别：log2(纹理空间变化率)
    float mipLevel = log2(maxDelta);
	
	//diffuse = texture2D(s_MatTexture, v_texcoord0, mipmapLevel);
	
	diffuse.rgb = vec3(mipLevel, mipLevel*4096.0, mipLevel * 10.0);
	*/
	
    vec2 colorChangeX = dFdx(lightUV);
    vec2 colorChangeY = dFdy(lightUV);
    
    // 计算每个通道的总变化
    vec2 totalChange = abs(colorChangeX) + abs(colorChangeY);
    
    // 判断是否所有通道的变化都很小
    float maxChange = max(totalChange.x, totalChange.y);
    float threshold = 0.000001;
    
    if (maxChange < threshold) {
        // 纯色区域
        //diffuse = vec4(1.0, 0.0, 0.0, 1.0);
    } else {
        // 渐变色或有颜色变化的区域
        //diffuse = vec4(0.0, 1.0, 0.0, 1.0);
    }
	
	vec3 viewDir = normalize(worldPos - ViewPositionAndTime.xyz);
    vec3 boardPlane = normalize(vec3(viewDir.z, 0.0, -viewDir.x));
	
	//diffuse.rgb = fract(worldPos);
	//diffuse.rgb = boardPlane;
	
    gl_FragColor = diffuse;
}