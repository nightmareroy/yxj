package com.wanniu.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author agui
 */
public final class PropertiesUtil {

	// 读取properties的全部信息
	public static Map<String, String> loadProperties(File file) {
		Map<String, String> ret = new HashMap<String, String>();
		Properties props = new Properties();
		try {
			InputStream in = new FileInputStream(file);
			props.load(in);
			Enumeration<?> en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = props.getProperty(key);
				ret.put(key, value);
			}
			props.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// 写入properties信息
	public static void writeProperties(File file, String name, String value) {
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(file);
			// 从输入流中读取属性列表（键和元素对）
			prop.load(fis);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream fos = new FileOutputStream(file);
			prop.setProperty(name, value);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			prop.store(fos, "Update '" + name + "' value");
		} catch (IOException e) {
			System.err.println("Visit " + file.getPath() + " for updating " + name + " value error");

		}
	}

}
