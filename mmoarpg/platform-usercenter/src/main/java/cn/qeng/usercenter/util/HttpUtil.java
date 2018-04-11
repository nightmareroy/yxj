/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.usercenter.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP工具类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class HttpUtil {
	protected final static Logger logger = LogManager.getLogger(HttpUtil.class);

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) throws Exception {
		logger.info("HTTP GET url={}", url);
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
		String htmlResult = result.toString();
		logger.info("HTTP GET result={}", htmlResult);
		return htmlResult;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&amp;name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) throws Exception {
		logger.info("HTTP POST url={},param={}", url, param);
		// 打开和URL之间的连接
		URLConnection conn = new URL(url).openConnection();
		// 设置通用的请求属性
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setReadTimeout(3000);
		conn.setConnectTimeout(3000);
		if (StringUtils.isNotEmpty(param)) {
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
		String htmlResult = result.toString();
		logger.info("HTTP POST result={}", htmlResult);
		return htmlResult;
	}

	/**
	 * 获取请求IP地址
	 */
	public static String requestIp(HttpServletRequest request) {
		StringBuffer ipLog = new StringBuffer();
		String ip = request.getParameter("ip");
		if (ip == null || ip.length() == 0) {
			ip = request.getHeader("x-forwarded-for");
		}
		ipLog.append("[x-forwarded-for:" + ip + "]");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			ipLog.append("[Proxy-Client-IP:" + ip + "]");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			ipLog.append("[WL-Proxy-Client-IP:" + ip + "]");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			ipLog.append("[request_getRemoteAddr:" + ip + "]");
		}
		if (ip.equals("127.0.0.1")) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
				ipLog.append("[InetAddress.getLocalHost().getHostAddress():" + ip + "]");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return ip;
			}
		}
		String result = "";
		if (ip.indexOf(",") > 0) {
			String[] ips = ip.split(",");
			for (int i = ips.length - 1; i >= 0; i--) {
				if (!ips[i].trim().startsWith("127.") && !ips[i].trim().startsWith("10.") && !ips[i].trim().startsWith("192.168.")) {
					result = ips[i].trim();
				}
			}
		} else {
			result = ip;
		}
		return result;
	}
}
