package com.wanniu.util;

import com.wanniu.GConfig;


/**
 * 调试输出类
 * @author agui
 */
public final class Out {
	
	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}
	
	private static Level __level__ =  Level.valueOf(GConfig.getInstance().get("out.level", "DEBUG").toUpperCase());
	
	public static boolean enable(Level level) {
		return __level__.compareTo(level) <= 0;
	}

	public static void debug(Object... args){
		if(enable(Level.DEBUG)) {
			StringBuilder builder = new StringBuilder();
			builder.append(DateUtil.getTime(DateUtil.F_FULL)).append(": ");
			for (Object object : args) {
				builder.append(object);
			}
			System.out.println(builder.toString());
		}
	}
	
	public static void info(Object... args){
		if(enable(Level.INFO)) {
			StringBuilder builder = new StringBuilder();
			builder.append(DateUtil.getTime(DateUtil.F_FULL)).append(": ");
			for (Object object : args) {
				builder.append(object);
			}
			System.out.println(builder.toString());
		}
	}
	
	public static void warn(Object... args){
		if(enable(Level.WARN)) {
			StringBuilder builder = new StringBuilder();
			builder.append(DateUtil.getTime(DateUtil.F_FULL)).append(": ");
			for (Object object : args) {
				builder.append(object);
			}
			System.err.println(builder.toString());
		}
	}
	
	public static void error(Object... args){
		StringBuilder builder = new StringBuilder();
		builder.append(DateUtil.getTime(DateUtil.F_FULL)).append(": ");
		for (Object object : args) {
			builder.append(object);
		}
		System.err.println(builder.toString());
	}
	/**
	 * 红字输出
	 */
	public static void red(Object... args){
		error(args);
	}
	
}
