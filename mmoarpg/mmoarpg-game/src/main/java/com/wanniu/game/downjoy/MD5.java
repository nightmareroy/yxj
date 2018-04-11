package com.wanniu.game.downjoy;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 *
 */
public class MD5 {
	public static String getDigest(String msg) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] byteArray = null;
			byteArray = msg.getBytes("UTF-8");
			byte[] md5Bytes = md5.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {

		}
		return null;
	}

	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);

		for (int i = 0; i < bArray.length; i++) {
			String sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * source.getBytes("UTF-8")
	 * 
	 * @param source
	 * @return
	 */
	public static String encrypt(String source) {
		MessageDigest md = null;
		byte[] bt = source.getBytes();
		try {
			bt = source.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			md.update(bt);
			return bytesToHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		System.out.println(getDigest("order=ok123456&money=5.21&umid=123456&time=20141212105433&result=1&transno=&key=66666dfPe05f"));
		System.out.println(getDigest("195|j5VEvxhc|4C18A0AEAB1B4C9BBFD49E21E202025C|36223535814"));
	}
}