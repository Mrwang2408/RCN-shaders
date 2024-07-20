// https://github.com/OEOTYAN/useless-shaders/blob/master/shaders/glsl/renderchunk.fragment


$input v_color0, v_texcoord0, v_lightmapUV, v_position, v_worldpos

#ifndef NO_FOG
	//$input v_fog
	//uniform vec4 FogColor;

#endif


#include <bgfx_shader.sh>
#include <defines.sh>

SAMPLER2D(s_MatTexture, 0);
SAMPLER2D(s_SeasonsTexture, 1);
SAMPLER2D(s_LightMapTexture, 2);


uniform vec4 ViewPositionAndTime;


void main() {
    vec4 diffuse;
	
#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
    diffuse.rgb = vec3(1.0, 1.0, 1.0);
#else
	diffuse = texture2D(s_MatTexture, v_texcoord0);
	#ifdef ALPHA_TEST
		bool needDiscard = false;
		bool isRedstoneDust = false;
		if (diffuse.a < 0.5) {
			needDiscard = true;
		};
	#endif
	
	#if defined(SEASONS) && (defined(OPAQUE) || defined(ALPHA_TEST))
		diffuse.rgb *= mix(vec3(1.0, 1.0, 1.0), texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0, v_color0.b);
		diffuse.rgb *= v_color0.aaa;
	#else
		diffuse *= v_color0;
	#endif
	
	bool needLightMap = true;

	vec3 chunkPos = v_position;
	vec3 cp = fract(chunkPos);
	//vec3 cp = 1.0 - fract(chunkPos);  //是否倒转字体

		cp.x = cp.x * 3.0 - 1.1;
		cp.z = cp.z * 3.0 - 1.1;

	vec3 normal = normalize(cross(dFdx(chunkPos), dFdy(chunkPos)));

#endif

#ifndef TRANSPARENT
    diffuse.a =  v_color0.a;
#endif




#if defined(ORE_TEST) && (defined(OPAQUE) || defined(ALPHA_TEST) || defined(TRANSPARENT)) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))

  vec4 oreTest = texture2DLod(s_MatTexture, v_texcoord0, 0.0);
  
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

#endif




#if (defined(OPAQUE) || defined(ALPHA_TEST) || defined(TRANSPARENT)) && !(defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))

	#ifdef LIGHT_OVERLAY
	int setLiOverlay = 0;
	#endif

	#ifdef ALPHA_TEST
	bool isRsOverlay = false;
	if(normal.y>0.99 &&
		v_color0.r > v_color0.g + v_color0.b) {
		isRedstoneDust = true;
	
		#if defined(REDSTONE_OVERLAY) && !(defined(SEASONS))
		needLightMap = false; 
		if (
			(cp.x <= 0.7 && cp.x >= 0.1 && cp.z <= 0.08 && cp.z >= 0.05) ||
			(cp.x <= 0.7 && cp.x >= 0.1 && cp.z <= 0.2 && cp.z >= 0.17)) {
			isRsOverlay = true;  //预绘制两条横线
		} else if (v_color0.r > 0.2930392 && v_color0.r < 0.3030392 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度0
			};
		} else if (v_color0.r > 0.999 && v_color0.g > 0.1910784 && v_color0.g < 0.2010784 && v_color0.b < 0.005) {
			if (
			(cp.x <= 0.7 && cp.x >= 0.1 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.55 && cp.z >= 0.25) || 
			(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.45) || 
			(cp.x <= 0.3 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度15
			};
		} else if (v_color0.r > 0.4342157 && v_color0.r < 0.4442157 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(1.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.55 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.35) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度1
			};
		} else if (v_color0.r > 0.4734314 && v_color0.r < 0.4834314 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(2.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.45) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.55 && cp.z >= 0.25) || 
			(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度2
			};
		} else if (v_color0.r > 0.5126471 && v_color0.r < 0.5226471 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(3.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度3
			};
		} else if (v_color0.r > 0.5518628 && v_color0.r < 0.5618628 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(4.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45)) {
			isRsOverlay = true;  //信号强度4
			};
		} else if (v_color0.r > 0.595 && v_color0.r < 0.605 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(5.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.55 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
			(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度5
			};
		} else if (v_color0.r > 0.6342157 && v_color0.r < 0.6442157 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(6.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.55 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.35) || 
			(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度6
			};
		} else if (v_color0.r > 0.6734314 && v_color0.r < 0.6834314 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(7.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.65 && cp.z >= 0.55)) {
			isRsOverlay = true;  //信号强度7
			};
		} else if (v_color0.r > 0.7126471 && v_color0.r < 0.7226471 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(8.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.25) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65) || 
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度8
			};
		} else if (v_color0.r > 0.7518628 && v_color0.r < 0.7618628 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(9.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
			(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.35) || 
			(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
			(cp.x <= 0.55 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) || 
			(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度9
			};
		} else if (v_color0.r > 0.795 && v_color0.r < 0.805 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(10.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) || 
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) ||
			(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.25) ||
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25)) {
			isRsOverlay = true;  //信号强度10
			};
		} else if (v_color0.r > 0.8342157 && v_color0.r < 0.8442157 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(11.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.4 && cp.x >= 0.1 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度11
			};
		} else if (v_color0.r > 0.8734314 && v_color0.r < 0.8834314 && (v_color0.g + v_color0.b) < 0.005) {
			if (
			(mod(floor(12.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) ||
			(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.45) ||
			(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.55 && cp.z >= 0.25) ||
			(cp.x <= 0.3 && cp.x >= 0.1 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度12
			};
		} else if (v_color0.r > 0.9126471 && v_color0.r < 0.9226471 && v_color0.g > 0.01852941 && v_color0.g < 0.02852941 && v_color0.b < 0.005) {
			if (
			(mod(floor(13.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) ||
			(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) ||
			(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65)) {
			isRsOverlay = true;  //信号强度13
			};
		} else if (v_color0.r > 0.9518628 && v_color0.r < 0.9618627 && v_color0.g > 0.1008824 && v_color0.g < 0.1108824 && v_color0.b < 0.005) {
			if (
			(mod(floor(14.0 / exp2(floor(6.666667 * (cp.x - 0.1)))), 2.0) >= 0.5 && cp.z <= 0.15 && cp.z >= 0.1) ||
			(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) ||
			(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) ||
			(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) ||
			(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) ||
			(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.45) ||
			(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45)) {
			isRsOverlay = true;  //信号强度14
			};
		};
		#endif
	};
		#ifdef REDSTONE_OVERLAY
		if (isRsOverlay) {
			diffuse.rgb = mix(diffuse.rgb, vec3(1.0, 1.0, 1.0), 0.75);
			needDiscard = false;  //修复字体被裁剪的问题
			needLightMap = false;  //使字体不再变暗
		};
		#endif
	
	#endif

	
	

	#ifdef LIGHT_OVERLAY
	float checkDistance = length(v_worldpos);	
	if(normal.y>0.99 &&
		#ifdef ALPHA_TEST
		!(isRedstoneDust) && 
		#endif
		#ifdef OPAQUE
		checkDistance < 32.0
		#else
		checkDistance < 16.0
		#endif
		) {
		float testLightMap;
		
        cp.x = cp.x - 0.13;
		
		if (cp.x <= 0.7 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) {
			setLiOverlay = 1;
			testLightMap = v_lightmapUV.x; //中心区域选择方块光照
		} else {
			cp.x = ((cp.x * 2.0) + 0.55);
			cp.z = ((cp.z * 2.0) - 0.25);
			if (cp.x <= 0.7 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) {
				setLiOverlay = 5;
				testLightMap = v_lightmapUV.y;  //右下方选择天空光照
			};
		};
			
		if (!(setLiOverlay == 0)) {
			if (testLightMap <0.0625) {
				if (
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25)||
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.25)||
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)||
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 2;  //光照等级0
				};
			} else if (testLightMap >= 0.9375) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.55 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.3 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 1;  //光照等级15
				};
			} else if (testLightMap >= 0.0625 && testLightMap < 0.125) {
				if (
				(cp.x <= 0.55 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) ||
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级1
				};
			} else if (testLightMap >= 0.125 && testLightMap < 0.1875) {
				if (
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.55 && cp.z >= 0.25) || 
				(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级2
				};
			} else if (testLightMap >= 0.1875 && testLightMap < 0.25) {
				if (
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级3
				};
			} else if (testLightMap >= 0.25 && testLightMap < 0.3125) {
				if (
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45)) {
				setLiOverlay += 1;  //光照等级4
				};
			} else if (testLightMap >= 0.3125 && testLightMap < 0.375) {
				if (
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.55 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 1;  //光照等级5
				};
			} else if (testLightMap >= 0.375 && testLightMap < 0.4375) {
				if (
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.55 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.45 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 1;  //光照等级6
				};
			} else if (testLightMap >= 0.4375 && testLightMap < 0.5) {
				if (
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.65 && cp.z >= 0.55)) {
				setLiOverlay += 1;  //光照等级7
				};
			} else if (testLightMap >= 0.5 && testLightMap < 0.5625) {
				if (
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 1;  //光照等级8
				};
			} else if (testLightMap >= 0.5625 && testLightMap < 0.625) {
				if (
				(cp.x <= 0.45 && cp.x >= 0.35 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.35 && cp.x >= 0.25 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.55 && cp.x >= 0.45 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.55 && cp.x >= 0.25 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.55 && cp.x >= 0.35 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级9
				};
			} else if (testLightMap >= 0.625 && testLightMap < 0.6875) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25)) {
				setLiOverlay += 1;  //光照等级10
				};
			} else if (testLightMap >= 0.6875 && testLightMap < 0.75) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.4 && cp.x >= 0.1 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级11
				};
			} else if (testLightMap >= 0.75 && testLightMap < 0.8125) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.55 && cp.z >= 0.25) || 
				(cp.x <= 0.3 && cp.x >= 0.1 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级12
				};
			} else if (testLightMap >= 0.8125 && testLightMap < 0.875) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45) || 
				(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.2 && cp.z <= 0.75 && cp.z >= 0.65)) {
				setLiOverlay += 1;  //光照等级13
				};
			} else if (testLightMap >= 0.875 && testLightMap < 0.9375) {
				if (
				(cp.x <= 0.7 && cp.x >= 0.4 && cp.z <= 0.35 && cp.z >= 0.25) || 
				(cp.x <= 0.6 && cp.x >= 0.5 && cp.z <= 0.75 && cp.z >= 0.35) || 
				(cp.x <= 0.7 && cp.x >= 0.6 && cp.z <= 0.75 && cp.z >= 0.65) || 
				(cp.x <= 0.2 && cp.x >= 0.1 && cp.z <= 0.75 && cp.z >= 0.25) || 
				(cp.x <= 0.4 && cp.x >= 0.3 && cp.z <= 0.75 && cp.z >= 0.45) || 
				(cp.x <= 0.3 && cp.x >= 0.2 && cp.z <= 0.55 && cp.z >= 0.45)) {
				setLiOverlay += 1;  //光照等级14
				};
			};
			#ifdef ALPHA_TEST
			if (!(setLiOverlay == 1 || setLiOverlay == 5)) {
				//needLightMap = false; 
				needDiscard = false;  
			};
			#endif
        };	
    };
	#endif

#endif


#ifdef ALPHA_TEST
    if (needDiscard) {
        discard;
    };
#endif


#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
	diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;
#else
	#ifndef NIGHT_VISION
	if (needLightMap) {
		diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;  //设置是否夜视
	};
	#endif
#endif



#if (defined(OPAQUE) || defined(ALPHA_TEST) || defined(TRANSPARENT)) && !(defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))

	#ifdef CHUNK_BORDERS
	cp = fract(chunkPos.xyz);
	if (
		((chunkPos.x < 0.0625 || chunkPos.x > 15.9375) && (chunkPos.z < 0.0625 || chunkPos.z > 15.9375)) || 
		((chunkPos.y < 0.0625 || chunkPos.y > 15.9375) && (chunkPos.x < 0.0625 || chunkPos.x > 15.9375)) || 
		((chunkPos.y < 0.0625 || chunkPos.y > 15.9375) && (chunkPos.z < 0.0625 || chunkPos.z > 15.9375))) {
			if (chunkPos.x < 0.0625 && chunkPos.z < 0.0625) {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.0, 1.0, 0.0), 0.26); //y轴绿色显示
			} else if (chunkPos.z < 0.0625 && chunkPos.x < 15.9375) {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.8, 0.0, 0.2), 0.22); //x轴红色显示
			} else if (chunkPos.x < 0.0625) {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.0, 0.0, 0.8), 0.22); //z轴蓝色显示
			} else {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.1, 0.1, 1.0), 0.17); //其于部分蓝色显示
			};
	} else if (
		((chunkPos.x < 0.03125 || chunkPos.x > 15.96875) || (chunkPos.z < 0.03125 || chunkPos.z > 15.96875)) && (
		((cp.x < 0.03125 || cp.x > 0.96875) && (cp.y < 0.03125 || cp.y > 0.96875)) || 
		((cp.x < 0.03125 || cp.x > 0.96875) && (cp.z < 0.03125 || cp.z > 0.96875)) ||
		((cp.y < 0.03125 || cp.y > 0.96875) && (cp.z < 0.03125 || cp.z > 0.96875)))) {
			diffuse.rgb = ((diffuse.rgb / 0.4) * (vec3(1.0, 1.0, 1.0) - diffuse.rgb));  //其它部分白线显示
	};
	#endif
	
	
	#ifdef LIGHT_OVERLAY
	if (checkDistance < 32.0) {
		if (!(setLiOverlay == 0)) {
			if (setLiOverlay == 3) {
				diffuse = mix (diffuse, vec4(1.0, 0.0, 0.0, 0.65), 0.23);  //方块光照-无光
			};
			if (setLiOverlay == 2) {
				diffuse = mix (diffuse, vec4(0.0, 1.0, 0.0, 0.8), 0.4);  //方块光照-有光
			};
			if (setLiOverlay == 7) {
				diffuse = mix (diffuse, vec4(0.1, 0.1, 1.0, 0.65), 0.3);  //天空光照-无光
			};
			if (setLiOverlay == 6) {
				diffuse = mix (diffuse, vec4(0.6, 1.0, 0.0, 0.9), 0.4);  //天空光照-有光
			};
		};
		
		if (v_lightmapUV.x > 0.0615 && v_lightmapUV.x < 0.0625) {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.8, 0.5, 0.0), 0.45);  //边缘线橙色指示
		};
		if (v_lightmapUV.y > 0.0615 && v_lightmapUV.y < 0.0625) {
				diffuse.rgb = mix (diffuse.rgb, vec3(0.1, 0.1, 0.8), 0.45);  //边缘线蓝色指示
		}; 
	};
	#endif
	
	
#endif



#ifndef NO_FOG
    //diffuse.rgb = mix(diffuse.rgb,v_fog.rgb,v_fog.a);
#endif

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
	cp = fract(v_position.xyz);
	if (
	((cp.x < 0.03125 || cp.x > 0.96875) && (cp.y < 0.03125 || cp.y > 0.96875)) || 
	((cp.x < 0.03125 || cp.x > 0.96875) && (cp.z < 0.03125 || cp.z > 0.96875)) ||
	((cp.y < 0.03125 || cp.y > 0.96875) && (cp.z < 0.03125 || cp.z > 0.96875))) {
		
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

//	diffuse.rgb = v_color0.rgb;
//	diffuse.rgb = v_color0.aaa;
	

#endif	

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
	
	//diffuse = vec4(1.0,1.0,1.0,1.0);
	
    gl_FragColor = diffuse;
} 











