//by Mrwang2408
//<RCN_apply.h>



vec3 applyChunkBorder(vec3 diffuse, int set) {
	if (set != 0) {
		if (set == 1) {
			return ((diffuse / 0.4) * (vec3(1.0, 1.0, 1.0) - diffuse));  //每方块刻度白线
		};
		if (set == 2) {
			return mix(diffuse, vec3(0.1, 0.1, 1.0), 0.17); //其于部分蓝色显示
		};
		if (set == 3) {
			return mix(diffuse, vec3(0.0, 1.0, 0.0), 0.26); //y轴绿色显示
		};
		if (set == 4) {
			return mix(diffuse, vec3(0.8, 0.0, 0.2), 0.22); //x轴红色显示
		};
		if (set == 5) {
			return mix(diffuse, vec3(0.0, 0.0, 0.8), 0.22); //z轴蓝色显示
		};
	};
	return diffuse.rgb;
}	


vec3 applyRsOverlay(vec3 diffuse, bool isRsOverlay) {
	if (isRsOverlay) {
		return mix(diffuse, vec3(1.0, 1.0, 1.0), 0.75);  //红石粉白色字体
	};
	return diffuse;
}		
		

vec4 applyLiOverlay(vec4 diffuse, int set) {
	if (set != 0) {
		if (set == 2) {
			return mix (diffuse, vec4(0.0, 1.0, 0.0, 0.8), 0.4);  //方块光照-有光-绿色
		};
		if (set == 3) {
			return mix (diffuse, vec4(1.0, 0.0, 0.0, 0.65), 0.23);  //方块光照-无光-红色
		};
		if (set == 4) {
			return vec4(mix (diffuse.rgb, vec3(0.8, 0.5, 0.0), 0.45), 0.0);  //边缘线橙色指示
		};
		if (set == 6) {
			return mix (diffuse, vec4(0.6, 1.0, 0.0, 0.9), 0.4);  //天空光照-有光-黄绿
		};
		if (set == 7) {
			return mix (diffuse, vec4(0.1, 0.1, 1.0, 0.65), 0.3);  //天空光照-无光-蓝色
		};
		if (set == 8) {
			return vec4(mix (diffuse.rgb, vec3(0.1, 0.1, 0.8), 0.45), 0.0);  //边缘线蓝色指示
		}; 
	};
	return diffuse;
}
