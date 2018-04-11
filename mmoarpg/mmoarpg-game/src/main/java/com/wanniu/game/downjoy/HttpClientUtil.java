/**
 * $Id: HttpClientUtil.java,v 1.9 2013/04/19 10:56:14 liuliulong Exp $
 */
package com.wanniu.game.downjoy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.wanniu.core.logfs.Out;

/**
 * http 请求工具类
 */
public class HttpClientUtil {

	public static final String DEFAULT_URL_CONTENT_TYPE = "application/x-www-form-urlencoded";
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final int CONNECT_TIME_OUT = 5 * 1000; // 连接超时时间5秒（单位：毫秒）
	public static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * http post请求
	 * 
	 * @param postURL
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String postURL, String requestBody) throws Exception {
		return doPost(postURL, requestBody, DEFAULT_URL_CONTENT_TYPE);
	}

	public static String doPost(String postURL, String requestBody, String contentType) throws Exception {
		Out.info("HTTP POST URL=", postURL, ", requestBody=", requestBody);
		// Post请求的url，与get不同的是不需要带参数
		HttpURLConnection httpConn = null;
		try {

			URL postUrl = new URL(postURL);
			// 打开连接
			httpConn = (HttpURLConnection) postUrl.openConnection();

			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			httpConn.setDoOutput(true);
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpConn.setDoInput(true);
			// 设定请求的方法为"POST"，默认是GET
			httpConn.setRequestMethod("POST");
			// Post 请求不能使用缓存
			httpConn.setUseCaches(false);
			// 进行跳转
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestProperty("Content-Type", contentType + "; charset=UTF-8;");

			byte[] bytes = requestBody.getBytes("UTF-8");
			httpConn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			// httpUrlConnection.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");
			// 连接主机的超时时间（单位：毫秒）
			httpConn.setConnectTimeout(CONNECT_TIME_OUT);
			// 从主机读取数据的超时时间（单位：毫秒）
			httpConn.setReadTimeout(CONNECT_TIME_OUT);
			// 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行 connect。
			httpConn.connect();
			DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
			out.write(bytes);
			out.flush();
			out.close();
			int status = httpConn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				System.out.println("发送请求失败，状态码：[" + status + "] 返回信息：" + httpConn.getResponseMessage());
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			StringBuffer responseSb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseSb.append(line.trim());
			}
			reader.close();
			return responseSb.toString().trim();
		} finally {
			httpConn.disconnect();
		}
	}

}
