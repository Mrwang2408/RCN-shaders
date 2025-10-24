//bilibili 王2408
//QQ：3308116191

//	#define NO_FOG
	
//	#define NIGHT_VISION
	
//	#define REDSTONE_OVERLAY
	
//	#define CHUNK_BORDERS
	
//	#define LIGHT_OVERLAY
	
//	#define ORE_TEST
	
//	#define CHUNK_ANIM 20.0
	
//	#define INVERT_NUM
	
//	#define DEBUG_UV


//RCN_overlay2
#define SUBPACKS 3
	
	
	
			
//RCN Config Defines
#if !defined(DEPTH_ONLY) && !defined(DEPTH_ONLY_OPAQUE)
	#if !defined(INSTANCING) && !defined(RENDER_AS_BILLBOARDS)
		#if defined(SEASONS)
			#if defined(ALPHA_TEST) //Seasons
				#define CHUNK_BORDERS //区块显示
				#define LIGHT_OVERLAY //亮度显示
			#endif
		#else //SEASONS
			#if defined(ALPHA_TEST) //AlphaTest
				#define REDSTONE_OVERLAY //红石显示
				#define ORE_TEST //植物发光
				#define CHUNK_BORDERS //区块显示
				#define LIGHT_OVERLAY //亮度显示
			#endif
			#if defined(OPAQUE) //Opaque
				#define ORE_TEST //矿物发光
				#define CHUNK_BORDERS //区块显示
				//#define LIGHT_OVERLAY //亮度显示
			#endif
			#if defined(TRANSPARENT) //Transparrent
				#define CHUNK_BORDERS //区块显示
			#endif
		#endif //SEASONS
	#endif //INSTANCING
#endif //DEPTH_ONLY
			
//RCN Auto Subpacks
#ifdef SUBPACKS
	#if SUBPACKS < 3 && defined(LIGHT_OVERLAY)
		#undef LIGHT_OVERLAY
	#endif
	#if SUBPACKS < 2 && defined(CHUNK_BORDERS)
		#undef CHUNK_BORDERS
	#endif
	#if SUBPACKS < 1 && defined(REDSTONE_OVERLAY)
		#undef REDSTONE_OVERLAY
	#endif
#endif
	
	
	
/*
//RCN Subpacks Config
#ifdef SUBPACKS
#if SUBPACKS >= 1
#define REDSTONE_OVERLAY
#if SUBPACKS >= 2
#define CHUNK_BORDERS
#if SUBPACKS >= 3
#define LIGHT_OVERLAY
#endif
#endif
#endif
#endif
			
//RCN Auto Undef
#if defined(ALPHA_TEST) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))
#else
	#ifdef REDSTONE_OVERLAY
		#undef REDSTONE_OVERLAY
	#endif
#endif
#if ((defined(OPAQUE) || defined(TRANSPARENT)) && !(defined(SEASONS) || defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS))) || (defined(ALPHA_TEST) && !(defined(INSTANCING) || defined(RENDER_AS_BILLBOARDS)))
	#define MAIN_FLAG
#else
	#ifdef ORE_TEST
		#undef ORE_TEST
	#endif
	#ifdef CHUNK_BORDERS
		#undef CHUNK_BORDERS
	#endif
	#ifdef LIGHT_OVERLAY
		#undef LIGHT_OVERLAY
	#endif
#endif
*/
	
	
//Modify by Mrwang2408