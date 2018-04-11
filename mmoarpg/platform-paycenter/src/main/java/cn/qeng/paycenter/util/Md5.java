package cn.qeng.paycenter.util;

import java.security.MessageDigest;

public class Md5 {
	/**
	 * 生成md5
	 */
	public static String getMD5(String message) throws Exception {
		String md5str = "";

		// 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
		MessageDigest md = MessageDigest.getInstance("MD5");

		// 2 将消息变成byte数组
		byte[] input = message.getBytes();

		// 3 计算后获得字节数组,这就是那128位了
		byte[] buff = md.digest(input);

		// 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
		md5str = bytesToHex(buff);

		return md5str;
	}

	/**
	 * 二进制转十六进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuffer md5str = new StringBuffer();
		// 把数组每一字节换成16进制连成md5字符串
		int digital;
		for (int i = 0; i < bytes.length; i++) {
			digital = bytes[i];

			if (digital < 0) {
				digital += 256;
			}
			if (digital < 16) {
				md5str.append("0");
			}
			md5str.append(Integer.toHexString(digital));
		}
		return md5str.toString().toUpperCase();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Md5.getMD5("中国"));
	}
}
