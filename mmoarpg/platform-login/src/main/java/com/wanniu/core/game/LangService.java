package com.wanniu.core.game;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.FileUtil;

/**
 * 语言包服务
 * @author agui
 */
public final class LangService {

	private LangService() { }
	
	private static final Map<String, String> __LANGS = new HashMap<String, String>();
	
	/**
	 * 通过类名 + ".txt" 构建
	 */
	public static Map<String, String> build(Class<?> clz) {
		return build(clz.getSimpleName() + ".txt");
	}
	
	/**
	 * 通过文件名(包括后缀)构建
	 */
	public static Map<String, String> build(String file) {
		return build(new File(GGlobal.DIR_LANGUAGE + file));
	}
	
	/**
	 * 通过指定文件构建
	 */
	public static Map<String, String> build(File file) {
		Map<String, String> maps = new HashMap<String, String>();
		List<String> langs = FileUtil.readLines(file);
		for (String lang : langs) {
			String text = lang.trim();
			if(text.startsWith("#") || text.length()==0) continue;
			int index = text.indexOf("=");
			if(index > 0) {
				String key = text.substring(0, index);
				String value = text.substring(index + 1);
				if(value.length() > 0) {
					maps.put(key, value);
				}
			}
		}
		return maps;
	}

	private static void init(boolean reinit) {
		File language = new File(GGlobal.DIR_LANGUAGE);
		if(language.exists() && language.isDirectory()) {
			File[] langs = language.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					if(file.isFile()) return true;
					return false;
				}
			});
			Map<String, String> msgs = null;
			for (File file : langs) {
				msgs = build(file);
				for (Map.Entry<String, String> entry : msgs.entrySet()) {
					if(__LANGS.containsKey(entry.getKey()) && !reinit) {
						Out.error( String.format("当前包 (%s) ： [%s]  -> %s，已经存在值：%s", file.getName(), entry.getKey(), entry.getValue(), __LANGS.get(entry.getKey())));
					}
					__LANGS.put(entry.getKey(), entry.getValue());
				}
				Out.info(String.format("加载语言包文件 -> %s\t\t[%d / %d]", file.getName(), msgs.size(), __LANGS.size()));
			}
		}
	}

	/** 初始化游戏中的语言包 */
	public static void init() {
		init(false);
	}

	/** 重新加载游戏中的语言包 */
	public static void reinit() {
		init(true);
	}

	/**
	 * 设置语言对
	 */
	public static String put(String key, String value) {
		return __LANGS.put(key, value);
	}

	/**
	 * 获取相应的字符串
	 */
	public static String getValue(String key) {
		return __LANGS.get(key);
	}
	
}
