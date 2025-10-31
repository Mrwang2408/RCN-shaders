//https://github.com/Mrwang2408/RCN-shaders/

//by Mrwang2408
//<RCN_apply.h>

vec3 applyRsOverlay(vec3 diffuse, bool isRsOverlay) {
	if (isRsOverlay) {return mix(diffuse, vec3(1.0, 1.0, 1.0), 0.75);  //红石粉白色字体
	};
	return diffuse;
}


vec4 checkChunkColor(int set) {
	if (set == 0) {
	//} else if (set == 1) {return vec4(0.0, 0.0, 0.0, 0.00); //Placeholder
	} else if (set == 2) {return vec4(0.1, 0.1, 1.0, 0.17); //其于部分蓝色显示
	} else if (set == 3) {return vec4(0.0, 1.0, 0.0, 0.26); //y轴绿色显示
	} else if (set == 4) {return vec4(0.8, 0.0, 0.2, 0.22); //x轴红色显示
	} else if (set == 5) {return vec4(0.0, 0.0, 0.8, 0.22); //z轴蓝色显示
	};
	return vec4(0.0, 0.0, 0.0, 0.00);
}

vec4 checkLiOverlay(int set) {
	if (set == 0) {
	} else if (set == 2) {return vec4(0.0, 1.0, 0.0, 0.30);  //方块光照-有光-绿色
	} else if (set == 3) {return vec4(1.0, 0.0, 0.0, 0.20);  //方块光照-无光-红色
	} else if (set == 6) {return vec4(0.6, 1.0, 0.0, 0.30);  //天空光照-有光-黄绿
	} else if (set == 7) {return vec4(0.1, 0.1, 1.0, 0.25);  //天空光照-无光-蓝色
	} else if (set == 4) {return vec4(0.8, 0.5, 0.0, 0.45);  //边缘线橙色指示
	} else if (set == 8) {return vec4(0.1, 0.1, 0.8, 0.45);  //边缘线蓝色指示
	};
	return vec4(0.0, 0.0, 0.0, 0.00);
}


  //#define NL_GLOW_TEX 2.2
  #define NL_GLOW_TEX 0.5
/*
  // Texture alpha: diffuse.a
  // 252/255 = max glow
  // 253/255 = partial glow
vec3 glowDetect(vec4 diffuse) {
  if (GLOW_PIXEL(diffuse)) {
    return  diffuse.rgb * (0.995-diffuse.a)/(0.995-0.9875);
  }
  return vec3(0.0,0.0,0.0);
}
*/
vec3 glowDetect(vec4 texCol) {
  if (texCol.a > 0.988 && texCol.a < 0.993) {
    vec3 glow = texCol.rgb * texCol.rgb;
    if (texCol.a > 0.989) {
      glow *= 0.4;
    };
    return glow;
  };
  return vec3(0.0,0.0,0.0);
}

//+
//Modify by Mrwang2408
//QQ:3308116191
//https://github.com/Mrwang2408/RCN-shaders
//useless-shaders by @OEOTYAN

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
	//lightmap.rgb = 0.6 + 0.4 * lightmap.rgb;
	//lightmap.rgb = 0.3 + 0.7 * lightmap.rgb;
*/

//-----------------------------------

/*
vec3 applyChunkBorder(vec3 diffuse, int set) {
	if (set == 0) {
	} else if (set == 1) {return ((diffuse / 0.4) * (vec3(1.0, 1.0, 1.0) - diffuse));  //每方块刻度白线
	} else if (set == 2) {return mix(diffuse, vec3(0.1, 0.1, 1.0), 0.17); //其于部分蓝色显示
	} else if (set == 3) {return mix(diffuse, vec3(0.0, 1.0, 0.0), 0.26); //y轴绿色显示
	} else if (set == 4) {return mix(diffuse, vec3(0.8, 0.0, 0.2), 0.22); //x轴红色显示
	} else if (set == 5) {return mix(diffuse, vec3(0.0, 0.0, 0.8), 0.22); //z轴蓝色显示
	};
	return diffuse.rgb;
}

vec4 applyLiOverlay(vec4 diffuse, int set) {
	if (set == 0) {
	} else if (set == 2) {return mix (diffuse, vec4(0.0, 1.0, 0.0, 0.80), 0.40);  //方块光照-有光-绿色
	} else if (set == 3) {return mix (diffuse, vec4(1.0, 0.0, 0.0, 0.65), 0.23);  //方块光照-无光-红色
	} else if (set == 6) {return mix (diffuse, vec4(0.6, 1.0, 0.0, 0.90), 0.40);  //天空光照-有光-黄绿
	} else if (set == 7) {return mix (diffuse, vec4(0.1, 0.1, 1.0, 0.65), 0.30);  //天空光照-无光-蓝色
	} else if (set == 4) {return vec4(mix (diffuse.rgb, vec3(0.8, 0.5, 0.0), 0.45), 0.0);  //边缘线橙色指示
	} else if (set == 8) {return vec4(mix (diffuse.rgb, vec3(0.1, 0.1, 0.8), 0.45), 0.0);  //边缘线蓝色指示
	};
	return diffuse;
}
*/

//-----------------------------------

int runChunkBorder(vec3 cp) {
	vec3 bp = fract(cp);
	vec3 cr = 8.0 - abs(cp - 8.0);
	vec3 br = 0.5 - abs(bp - 0.5);
	float cn = min(cr.x, cr.z);
	float bn = max(br.x, br.z);
	
	if ( int(cr.x < 0.0625) + int(cr.y < 0.0625) + int(cr.z < 0.0625) >= 2 ) {
		if (cp.x < 0.0625 && cp.z < 0.0625) {
			return 3; //y轴绿色显示
		} else if (cp.z < 0.0625 && cp.x < 15.9375) {
			return 4; //x轴红色显示
		} else if (cp.x < 0.0625) {
			return 5; //z轴深蓝色显示
		} else {
			return 2; //其于部分蓝色显示
		};
	} else if (( cr.x < 0.09375 || cr.z < 0.09375 ) && (
		int(br.x < 0.03125) + int(br.y < 0.03125) + int(br.z < 0.03125) >= 2 )) {
		return 1;  //每方块刻度白线
	};
	return 0;
	
/*	
	if (cn < 0.0625) {
		//return 3;
	};
	if (cn < 0.09375 && bn < 0.03125) {
		//return 1;
	};
	return 0;
*/
}

/*
vec3 cp = position.xyz;
if (
	((cp.x < 0.0625 || cp.x > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375)) || 
	((cp.y < 0.0625 || cp.y > 15.9375) && (cp.x < 0.0625 || cp.x > 15.9375)) || 
	((cp.y < 0.0625 || cp.y > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375))) {
	return 1
};
*/
/*
int runChunkBorder(vec3 cp) {
	vec3 bp = fract(cp);
	vec3 cr = 8.0 - abs(cp - 8.0);
	vec3 br = 0.5 - abs(bp - 0.5);
	float cn = min(cr.x, cr.z);
	float bn = max(br.x, br.z);
	// (cr.x < 0.0625) + (cr.y < 0.0625) + (cr.z < 0.0625) >= 2 
	// (br.x < 0.0625) + (br.y < 0.0625) + (br.z < 0.0625) >= 2 
	if (
	( cr.x < 0.0625 && cr.z < 0.0625 ) || 
	( cr.y < 0.0625 && cr.x < 0.0625 ) || 
	( cr.y < 0.0625 && cr.z < 0.0625 )) {
		if (cp.x < 0.0625 && cp.z < 0.0625) {
			return 3; //y轴绿色显示
		} else if (cp.z < 0.0625 && cp.x < 15.9375) {
			return 4; //x轴红色显示
		} else if (cp.x < 0.0625) {
			return 5; //z轴深蓝色显示
		} else {
			return 2; //其于部分蓝色显示
		};
	} else if (
		( cr.x < 0.09375 || cr.z < 0.09375 ) && (
		( br.x < 0.03125 && br.z < 0.03125 ) || 
		( br.y < 0.03125 && br.x < 0.03125 ) ||
		( br.y < 0.03125 && br.z < 0.03125 ) )) {
		return 1;  //每方块刻度白线
	};
	return 0;
}
*/
/*
	vec3 bp = fract(cp);
	vec3 cr = 8.0 - abs(cp - 8.0);
	vec3 br = 0.5 - abs(bp - 0.5);
	float cn = min(cr.x, cr.z);
	float bn = max(br.x, br.z);
	
	if ( int(cr.x < 0.0625) + int(cr.y < 0.0625) + int(cr.z < 0.0625) >= 2 ) {
		if (cp.x < 0.0625 && cp.z < 0.0625) {
			return 3; //y轴绿色显示
		} else if (cp.z < 0.0625 && cp.x < 15.9375) {
			return 4; //x轴红色显示
		} else if (cp.x < 0.0625) {
			return 5; //z轴深蓝色显示
		} else {
			return 2; //其于部分蓝色显示
		};
	} else if (( cr.x < 0.09375 || cr.z < 0.09375 ) && (
		int(br.x < 0.03125) + int(br.y < 0.03125) + int(br.z < 0.03125) >= 2 )) {
		return 1;  //每方块刻度白线
	};
	return 0;
*/
/*
int runChunkBorder(vec3 cp) {
	vec3 bp = fract(cp);
	if (
		((cp.x < 0.0625 || cp.x > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375)) || 
		((cp.y < 0.0625 || cp.y > 15.9375) && (cp.x < 0.0625 || cp.x > 15.9375)) || 
		((cp.y < 0.0625 || cp.y > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375))) {
		if (cp.x < 0.0625 && cp.z < 0.0625) {
			return 3; //y轴绿色显示
		} else if (cp.z < 0.0625 && cp.x < 15.9375) {
			return 4; //x轴红色显示
		} else if (cp.x < 0.0625) {
			return 5; //z轴深蓝色显示
		} else {
			return 2; //其于部分蓝色显示
		};
	} else if (
		((cp.x < 0.09375 || cp.x > 15.90625) || (cp.z < 0.09375 || cp.z > 15.90625)) && (
		((bp.x < 0.03125 || bp.x > 0.96875) && (bp.y < 0.03125 || bp.y > 0.96875)) || 
		((bp.x < 0.03125 || bp.x > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875)) ||
		((bp.y < 0.03125 || bp.y > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875)))) {
		return 1;  //每方块刻度白线
	};
	return 0;
}
*/
/*
int runChunkBorder(vec3 cp) {
	vec3 bp = fract(cp);
	if (
		((cp.x < 0.125 || cp.x > 15.875) && (cp.z < 0.125 || cp.z > 15.875)) || 
		((cp.y < 0.125 || cp.y > 15.875) && (cp.x < 0.125 || cp.x > 15.875)) || 
		((cp.y < 0.125 || cp.y > 15.875) && (cp.z < 0.125 || cp.z > 15.875))
	) {
		if (
		((cp.x < 0.0625 || cp.x > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375)) || 
		((cp.y < 0.0625 || cp.y > 15.9375) && (cp.x < 0.0625 || cp.x > 15.9375)) || 
		((cp.y < 0.0625 || cp.y > 15.9375) && (cp.z < 0.0625 || cp.z > 15.9375))
		) {
			if (cp.x < 0.125 && cp.z < 0.125) {
				return 3; //y轴绿色显示
			} else if (cp.z < 0.125 && cp.x < 15.875) {
				return 4; //x轴红色显示
			} else if (cp.x < 0.125) {
				return 5; //z轴蓝色显示
			} else {
				return 2; //其于部分蓝色显示
			};
		} else if (
			((bp.x < 0.03125 || bp.x > 0.96875) && (bp.y < 0.03125 || bp.y > 0.96875)) || 
			((bp.x < 0.03125 || bp.x > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875)) ||
			((bp.y < 0.03125 || bp.y > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875))
		) {
			if (cp.x < 0.125 && cp.z < 0.125) {
				return 3; //y轴绿色显示
			} else if (cp.z < 0.125 && cp.x < 15.875) {
				return 4; //x轴红色显示
			} else if (cp.x < 0.125) {
				return 5; //z轴蓝色显示
			} else {
				return 2; //其于部分蓝色显示
			};
		};
	} else if (
		((cp.x < 0.09375 || cp.x > 15.90625) || (cp.z < 0.09375 || cp.z > 15.90625))
	) {
		if (
			((bp.x < 0.03125 || bp.x > 0.96875) && (bp.y < 0.03125 || bp.y > 0.96875)) || 
			((bp.x < 0.03125 || bp.x > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875)) ||
			((bp.y < 0.03125 || bp.y > 0.96875) && (bp.z < 0.03125 || bp.z > 0.96875))
		) {
			return 1;  //每方块刻度白线
		};
	};
	return 0;
}
*/

		//     aaaaaaa
		//    f       b
		//    f       b
		//    f       b
		//     ggggggg
		//    e       c
		//    e       c
		//    e       c
		//     ddddddd
		//					useless

bool runRsOverlay(vec3 color, vec3 cp) {
	vec3 bp = fract(cp);//bPos: 每方块坐标（1x1x1）
	#ifdef INVERT_NUM
	bp = 1.0 - bp;  //是否倒转字体
	#endif
	bp.x = bp.x * 3.0 - 1.1;
	bp.z = bp.z * 3.0 - 1.1;
	if (
		(bp.x <= 0.7 && bp.x >= 0.1 && bp.z <= 0.08 && bp.z >= 0.05) ||
		(bp.x <= 0.7 && bp.x >= 0.1 && bp.z <= 0.2 && bp.z >= 0.17)) {
		return true;  //预绘制两条横线
	} else if (color.r > 0.2930392 && color.r < 0.3030392 && (color.g + color.b) < 0.005) {
		if (
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度0
		};
	} else if (color.r > 0.999 && color.g > 0.1910784 && color.g < 0.2010784 && color.b < 0.005) {
		if (
		(bp.x <= 0.7 && bp.x >= 0.1 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.55 && bp.z >= 0.25) || 
		(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.45) || 
		(bp.x <= 0.3 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度15
		};
	} else if (color.r > 0.4342157 && color.r < 0.4442157 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(1.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.55 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.35) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度1
		};
	} else if (color.r > 0.4734314 && color.r < 0.4834314 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(2.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.45) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.55 && bp.z >= 0.25) || 
		(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度2
		};
	} else if (color.r > 0.5126471 && color.r < 0.5226471 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(3.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度3
		};
	} else if (color.r > 0.5518628 && color.r < 0.5618628 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(4.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45)) {
		return true;  //红石强度4
		};
	} else if (color.r > 0.595 && color.r < 0.605 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(5.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.55 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
		(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度5
		};
	} else if (color.r > 0.6342157 && color.r < 0.6442157 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(6.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.55 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.35) || 
		(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度6
		};
	} else if (color.r > 0.6734314 && color.r < 0.6834314 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(7.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.65 && bp.z >= 0.55)) {
		return true;  //红石强度7
		};
	} else if (color.r > 0.7126471 && color.r < 0.7226471 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(8.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.25) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65) || 
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度8
		};
	} else if (color.r > 0.7518628 && color.r < 0.7618628 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(9.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
		(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.35) || 
		(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
		(bp.x <= 0.55 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) || 
		(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度9
		};
	} else if (color.r > 0.795 && color.r < 0.805 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(10.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) || 
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) ||
		(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.25) ||
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25)) {
		return true;  //红石强度10
		};
	} else if (color.r > 0.8342157 && color.r < 0.8442157 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(11.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.4 && bp.x >= 0.1 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度11
		};
	} else if (color.r > 0.8734314 && color.r < 0.8834314 && (color.g + color.b) < 0.005) {
		if (
		(mod(floor(12.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) ||
		(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.45) ||
		(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.55 && bp.z >= 0.25) ||
		(bp.x <= 0.3 && bp.x >= 0.1 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度12
		};
	} else if (color.r > 0.9126471 && color.r < 0.9226471 && color.g > 0.01852941 && color.g < 0.02852941 && color.b < 0.005) {
		if (
		(mod(floor(13.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) ||
		(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) ||
		(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65)) {
		return true;  //红石强度13
		};
	} else if (color.r > 0.9518628 && color.r < 0.9618627 && color.g > 0.1008824 && color.g < 0.1108824 && color.b < 0.005) {
		if (
		(mod(floor(14.0 / exp2(floor(6.666667 * (bp.x - 0.1)))), 2.0) >= 0.5 && bp.z <= 0.15 && bp.z >= 0.1) ||
		(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) ||
		(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) ||
		(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) ||
		(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) ||
		(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.45) ||
		(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45)) {
		return true;  //红石强度14
		};
	};
	return false;
}

int runLiOverlay(vec2 lightUV, vec3 cp) {
	int set = 0;
	float light = 0.0;
	lightUV += 0.001;  //fix z-fighting
	vec3 bp = fract(cp);
	bp.x = bp.x * 3.0 - 1.1;
	bp.z = bp.z * 3.0 - 1.1;
	bp.x = bp.x - 0.13;
	if (bp.x <= 0.7 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) {
		set = 1;
		light =lightUV.x; //中心区域选择方块光照
	} else {
		bp.x = ((bp.x * 2.0) + 0.55);
		bp.z = ((bp.z * 2.0) - 0.25);
		if (bp.x <= 0.7 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) {
			set = 5;
			light =lightUV.y;  //右下方选择天空光照
		};
	};
	//#ifdef ALPHA_TEST
	if (set != 0) {
		if (light <0.0625) {
			if (
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25)||
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.25)||
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)||
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 2;  //光照等级0
			};
		} else if (light >= 0.9375) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.55 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.3 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 1;  //光照等级15
			};
		} else if (light >= 0.0625 && light < 0.125) {
			if (
			(bp.x <= 0.55 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) ||
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级1
			};
		} else if (light >= 0.125 && light < 0.1875) {
			if (
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.55 && bp.z >= 0.25) || 
			(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级2
			};
		} else if (light >= 0.1875 && light < 0.25) {
			if (
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级3
			};
		} else if (light >= 0.25 && light < 0.3125) {
			if (
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45)) {
			set += 1;  //光照等级4
			};
		} else if (light >= 0.3125 && light < 0.375) {
			if (
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.55 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 1;  //光照等级5
			};
		} else if (light >= 0.375 && light < 0.4375) {
			if (
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.55 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.45 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 1;  //光照等级6
			};
		} else if (light >= 0.4375 && light < 0.5) {
			if (
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.65 && bp.z >= 0.55)) {
			set += 1;  //光照等级7
			};
		} else if (light >= 0.5 && light < 0.5625) {
			if (
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 1;  //光照等级8
			};
		} else if (light >= 0.5625 && light < 0.625) {
			if (
			(bp.x <= 0.45 && bp.x >= 0.35 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.35 && bp.x >= 0.25 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.55 && bp.x >= 0.45 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.55 && bp.x >= 0.25 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.55 && bp.x >= 0.35 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级9
			};
		} else if (light >= 0.625 && light < 0.6875) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25)) {
			set += 1;  //光照等级10
			};
		} else if (light >= 0.6875 && light < 0.75) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.4 && bp.x >= 0.1 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级11
			};
		} else if (light >= 0.75 && light < 0.8125) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.55 && bp.z >= 0.25) || 
			(bp.x <= 0.3 && bp.x >= 0.1 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级12
			};
		} else if (light >= 0.8125 && light < 0.875) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45) || 
			(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.2 && bp.z <= 0.75 && bp.z >= 0.65)) {
			set += 1;  //光照等级13
			};
		} else if (light >= 0.875 && light < 0.9375) {
			if (
			(bp.x <= 0.7 && bp.x >= 0.4 && bp.z <= 0.35 && bp.z >= 0.25) || 
			(bp.x <= 0.6 && bp.x >= 0.5 && bp.z <= 0.75 && bp.z >= 0.35) || 
			(bp.x <= 0.7 && bp.x >= 0.6 && bp.z <= 0.75 && bp.z >= 0.65) || 
			(bp.x <= 0.2 && bp.x >= 0.1 && bp.z <= 0.75 && bp.z >= 0.25) || 
			(bp.x <= 0.4 && bp.x >= 0.3 && bp.z <= 0.75 && bp.z >= 0.45) || 
			(bp.x <= 0.3 && bp.x >= 0.2 && bp.z <= 0.55 && bp.z >= 0.45)) {
			set += 1;  //光照等级14
			};
		};
	};
	//#endif
	if (lightUV.x > 0.0615 && lightUV.x < 0.0625) {
		set = 4;  //边缘线指示
	} else if (lightUV.y > 0.0615 && lightUV.y < 0.0625) {
		set = 8;  //边缘线指示
	};
	if (!(set == 0 || set == 1 || set == 5)) {
		return set;
	};
	return 0;
}


