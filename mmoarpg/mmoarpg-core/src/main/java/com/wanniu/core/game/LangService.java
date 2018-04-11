package com.wanniu.core.game;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;

/**
 * 语言包服务
 * 
 * @author agui
 */
public final class LangService {

	private LangService() {}

	private static final Map<String, String> __LANGS = new HashMap<String, String>();

	/**
	 * 设置语言对
	 */
	public static String put(String key, String value) {
		if (__LANGS.containsKey(key)) {
			Out.warn("语言包重复配置:", key);
		}
		return __LANGS.put(key, value);
	}

	/**
	 * 获取相应的字符串
	 */
	public static String getValue(String key) {
		return __LANGS.getOrDefault(key, key);
	}

	/**
	 * 获取相应的字符串
	 */
	public static String format(String key, Object... o) {
		return String.format(getValue(key), o);
	}
}