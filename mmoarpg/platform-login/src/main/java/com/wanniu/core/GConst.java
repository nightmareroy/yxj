package com.wanniu.core;

import java.nio.charset.Charset;


/**
 * 功能描述：游戏中的常量定义
 * @author agui
 */
interface GConst {

	/** 分 */
	int 		TIME_MINUTE_SECOND 		= 60;
	long 	TIME_MINUTE						= TIME_MINUTE_SECOND * 1000L;
	
	/** 时 */
	int 		TIME_HOUR_SECOND 			= TIME_MINUTE_SECOND * 60;
	long 	TIME_HOUR							= TIME_HOUR_SECOND * 1000L;
	
	/** 天 */
	int 		TIME_DAY_SECOND 			= TIME_HOUR_SECOND * 24;
	long 	TIME_DAY 							= TIME_DAY_SECOND * 1000L;
	
	/** 周 */
	int 		TIME_WEEK_SECOND 			= TIME_DAY_SECOND * 7;
	long 	TIME_WEEK 							= TIME_WEEK_SECOND * 1000L;
	
	/** 月 */
	int 		TIME_MONTH_SECOND 		= TIME_DAY_SECOND * 30;
	long 	TIME_MONTH 						= TIME_MONTH_SECOND * 1000L;

	/** 年*/
	int 		TIME_YEAR_SECOND 			= TIME_DAY_SECOND * 365;
	long 	TIME_YEAR 							= TIME_YEAR_SECOND * 1000L;
	

	Charset UTF_8 									= Charset.forName("UTF-8");

}
