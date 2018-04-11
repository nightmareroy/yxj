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
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTPS访问工具类.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class HttpsUtils {
	protected final static Logger logger = LogManager.getLogger(HttpsUtils.class);

	public static String get(String httpsURL) throws Exception {
		logger.info("HTTPS GET url={}", httpsURL);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
		URL realUrl = new URL(httpsURL);
		// 打开和URL之间的连接
		HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
		// 设置https相关属性
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setDoOutput(true);
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		// 建立实际的连接
		connection.connect();

		// 定义 BufferedReader输入流来读取URL的响应
		StringBuilder result = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		}
		String htmlResult = result.toString();
		logger.info("HTTPS GET result={}", htmlResult);
		return htmlResult;
	}

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}
}