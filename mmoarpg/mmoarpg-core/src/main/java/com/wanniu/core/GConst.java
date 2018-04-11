package com.wanniu.core;

import java.nio.charset.Charset;


/**
 * 功能描述：游戏中的常量定义
 * @author agui
 */
interface GConst {

	/** 秒 */
	int 	TIME_SECOND						= 1000;
	
	/** 分 */
	int 	TIME_MINUTE_SECOND 				= 60;
	int 	TIME_MINUTE						= TIME_MINUTE_SECOND * TIME_SECOND;
	
	/** 时 */
	int 	TIME_HOUR_SECOND 				= TIME_MINUTE_SECOND 	* 60;
	int 	TIME_HOUR						= TIME_MINUTE 			* 60;
	
	/** 天 */
	int 	TIME_DAY_SECOND 				= TIME_HOUR_SECOND 	* 24;
	int 	TIME_DAY 						= TIME_HOUR 		* 24;
	
	/** 周 */
	int 	TIME_WEEK_SECOND 				= TIME_DAY_SECOND 	* 7;
	int 	TIME_WEEK 						= TIME_DAY 			* 7;
	
	/** 月 */
	int 	TIME_MONTH_SECOND 				= TIME_DAY_SECOND 	* 30;
	int 	TIME_MONTH 						= TIME_DAY 			* 30;

	/** 年*/
	int 	TIME_YEAR_SECOND 				= TIME_DAY_SECOND 	* 365;
	long 	TIME_YEAR 						= TIME_DAY 			* 365L;
	

	Charset UTF_8 							= Charset.forName("UTF-8");

}
