package com.wanniu.util;

import java.security.MessageDigest;

public class KeyUtil {
	
	// MD5加密（32位）
	public static String md5(String input) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return "";
		}
		char[] charArray = input.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

	/**
	 * 可逆的加密算法
	 */
	public static String encode(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'y');
		}
		String s = new String(a);
		return s;
	}

	/**
	 * 加密后解密
	 */
	public static String decode(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'y');
		}
		String k = new String(a);
		return k;
	}

	// 测试主函数
	public static void main(String args[]) {
		String s = new String("admin!");
		System.out.println("原始：" + s);
		System.out.println("MD5后：" + md5(s));
		System.out.println("MD5后再加密：" + encode(md5(s)));
		System.out.println("解密为MD5后的：" + decode(encode(md5(s))));
	}
}
