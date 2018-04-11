/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * HTTP工具类.
 *
 * @since 2.3
 * @author 小流氓(176543888@qq.com)
 */
@Component
public class HttpUtils {
	private final static Logger logger = LogManager.getLogger(HttpUtils.class);

	private static String key = "";

	@Value("${game.http.api.key}")
	public void setKey(String key) {
		HttpUtils.key = key;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		try {
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			// 建立实际的连接
			connection.connect();

			StringBuilder result = new StringBuilder();
			// 定义 BufferedReader输入流来读取URL的响应
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		logger.info("POST: url={}, param={}", url, param);
		try {
			// 打开和URL之间的连接
			URLConnection conn = new URL(url).openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			if (!StringUtils.isEmpty(param)) {
				// 获取URLConnection对象对应的输出流
				try (PrintWriter out = new PrintWriter(conn.getOutputStream())) {
					// 发送请求参数
					out.print(param);
					// flush输出流的缓冲
					out.flush();
				}
			}

			StringBuilder result = new StringBuilder();
			// 定义BufferedReader输入流来读取URL的响应
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}
			logger.info(result.toString());
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}