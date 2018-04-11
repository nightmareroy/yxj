/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
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
package com.wanniu.core.util;

/**
 * IP相关操作辅助类.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
public class IpUtils {

	/**
	 * 私有IP：
	 * <p>
	 * A类 10.0.0.0-10.255.255.255 <br>
	 * B类 172.16.0.0-172.31.255.255 <br>
	 * C类 192.168.0.0-192.168.255.255<br>
	 **/
	private static long aBegin = calIpNum("10.0.0.0");
	private static long aEnd = calIpNum("10.255.255.255");
	private static long bBegin = calIpNum("172.16.0.0");
	private static long bEnd = calIpNum("172.31.255.255");
	private static long cBegin = calIpNum("192.168.0.0");
	private static long cEnd = calIpNum("192.168.255.255");

	/**
	 * 判定一个IP是否为内网IP.
	 */
	public static boolean isInnerIP(String ipAddress) {
		final long ipNum = calIpNum(ipAddress);
		return isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals("127.0.0.1");
	}

	public static long calIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);
		return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}
}