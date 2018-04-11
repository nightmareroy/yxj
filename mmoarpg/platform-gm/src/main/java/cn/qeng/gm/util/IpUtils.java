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
package cn.qeng.gm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * IP相关操作辅助类.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
public class IpUtils {
	public static String getRemoteHost(javax.servlet.http.HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	private static final String default_key = "X2CBZ-KS7KS-XE7O5-636O2-7TEYE-5CBE5";

	// http://lbs.qq.com/webservice_v1/guide-ip.html
	public static String getCity(String ip) {
		String json = HttpUtils.sendGet("http://apis.map.qq.com/ws/location/v1/ip?key=" + default_key + "&ip=" + ip);
		JSONObject result = JSON.parseObject(json);
		JSONObject info = (JSONObject) result.get("result");
		if (info == null) {
			return result.getString("message");
		}
		return ((JSONObject) info.get("ad_info")).getString("city");
	}

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

	// IP的正则
	private static Pattern pattern = Pattern.compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");

	/**
	 * getAvaliIpList:(根据IP白名单设置获取可用的IP列表).
	 */
	public static Set<String> getAvaliIpList(String allowIp) {
		Set<String> ipList = new HashSet<String>();
		for (String allow : allowIp.replaceAll("\\s", "").split(";")) {
			if (allow.indexOf("*") > -1) {
				String[] ips = allow.split("\\.");
				String[] from = new String[] { "0", "0", "0", "0" };
				String[] end = new String[] { "255", "255", "255", "255" };
				List<String> tem = new ArrayList<String>();
				for (int i = 0; i < ips.length; i++)
					if (ips[i].indexOf("*") > -1) {
						tem = complete(ips[i]);
						from[i] = null;
						end[i] = null;
					} else {
						from[i] = ips[i];
						end[i] = ips[i];
					}

				StringBuffer fromIP = new StringBuffer();
				StringBuffer endIP = new StringBuffer();
				for (int i = 0; i < 4; i++)
					if (from[i] != null) {
						fromIP.append(from[i]).append(".");
						endIP.append(end[i]).append(".");
					} else {
						fromIP.append("[*].");
						endIP.append("[*].");
					}
				fromIP.deleteCharAt(fromIP.length() - 1);
				endIP.deleteCharAt(endIP.length() - 1);

				for (String s : tem) {
					String ip = fromIP.toString().replace("[*]", s.split(";")[0]) + "-" + endIP.toString().replace("[*]", s.split(";")[1]);
					if (validate(ip)) {
						ipList.add(ip);
					}
				}
			} else {
				if (validate(allow)) {
					ipList.add(allow);
				}
			}
		}
		return ipList;
	}

	/**
	 * 对单个IP节点进行范围限定
	 * 
	 * @param arg
	 * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
	 */
	private static List<String> complete(String arg) {
		List<String> com = new ArrayList<String>();
		if (arg.length() == 1) {
			com.add("0;255");
		} else if (arg.length() == 2) {
			String s1 = complete(arg, 1);
			if (s1 != null)
				com.add(s1);
			String s2 = complete(arg, 2);
			if (s2 != null)
				com.add(s2);
		} else {
			String s1 = complete(arg, 1);
			if (s1 != null)
				com.add(s1);
		}
		return com;
	}

	private static String complete(String arg, int length) {
		String from = "";
		String end = "";
		if (length == 1) {
			from = arg.replace("*", "0");
			end = arg.replace("*", "9");
		} else {
			from = arg.replace("*", "00");
			end = arg.replace("*", "99");
		}
		if (Integer.valueOf(from) > 255)
			return null;
		if (Integer.valueOf(end) > 255)
			end = "255";
		return from + ";" + end;
	}

	/**
	 * 在添加至白名单时进行格式校验
	 */
	private static boolean validate(String ip) {
		for (String s : ip.split("-"))
			if (!pattern.matcher(s).matches()) {
				return false;
			}
		return true;
	}

	/**
	 * checkLoginIP:(根据IP,及可用Ip列表来判断ip是否包含在白名单之中).
	 */
	public static boolean checkLoginIP(String ip, Set<String> ipList) {
		if (ipList.isEmpty() || ipList.contains(ip))
			return true;
		else {
			for (String allow : ipList) {
				if (allow.indexOf("-") > -1) {
					String[] from = allow.split("-")[0].split("\\.");
					String[] end = allow.split("-")[1].split("\\.");
					String[] tag = ip.split("\\.");

					// 对IP从左到右进行逐段匹配
					boolean check = true;
					for (int i = 0; i < 4; i++) {
						int s = Integer.valueOf(from[i]);
						int t = Integer.valueOf(tag[i]);
						int e = Integer.valueOf(end[i]);
						if (!(s <= t && t <= e)) {
							check = false;
							break;
						}
					}
					if (check) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * checkLoginIP:(根据IP地址，及IP白名单设置规则判断IP是否包含在白名单).
	 */
	private static boolean checkLoginIP(String ip, String ipWhiteConfig) {
		Set<String> ipList = getAvaliIpList(ipWhiteConfig);
		return checkLoginIP(ip, ipList);
	}

	public static void main(String[] args) {
		String ipWhilte = "192.168.1.256;" + // 设置单个IP的白名单
				"192.*.1.*;" + // 设置ip通配符,对一个ip段进行匹配
				"192.168.3.17-192.168.3.38"; // 设置一个IP范围
		boolean flag = IpUtils.checkLoginIP("192.168.2.2", ipWhilte);
		boolean flag2 = IpUtils.checkLoginIP("192.168.1.2", ipWhilte);
		boolean flag3 = IpUtils.checkLoginIP("192.168.3.16", ipWhilte);
		boolean flag4 = IpUtils.checkLoginIP("192.168.3.17", ipWhilte);
		System.out.println(flag); // true
		System.out.println(flag2); // false
		System.out.println(flag3); // false
		System.out.println(flag4); // true
	}
}