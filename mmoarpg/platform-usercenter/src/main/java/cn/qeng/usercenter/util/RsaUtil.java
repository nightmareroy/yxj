package cn.qeng.usercenter.util;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RsaUtil {
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 用privateKey对content进行SHA1的RSA签名，之后用base64编码返回
	 * 
	 * @param content 需要RSA签名的内容
	 * @param privateKey 签名用的私钥
	 * @return 经过SHA1签名的字符串
	 */
	public static String sign(String content, String privateKey) throws Exception {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey.getBytes(DEFAULT_CHARSET)));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyFactory.generatePrivate(keySpec);
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		signature.update(content.getBytes(DEFAULT_CHARSET));
		byte[] signed = signature.sign();
		return URLEncoder.encode(new String(Base64.getEncoder().encode(signed), DEFAULT_CHARSET), DEFAULT_CHARSET);
	}

	// Bqd%2F5spBsf%2F5E9s9eJCDQ%2Fnvl2TN0%2FNoBt09iHoXkuPiIl4WyWwF%2Fu%2BdDKbXn1du4Gw3%2BP6vAOfbQfpQQf5opihUVCRglgMYQRItEa00WNzidz71btINtaGxibWAWHF5wkMh1X13Vs4p%2FvWRJ8Ir6PXG2W%2BP01m3NZuyxtHTvFE%3D
	// Bqd%2F5spBsf%2F5E9s9eJCDQ%2Fnvl2TN0%2FNoBt09iHoXkuPiIl4WyWwF%2Fu%2BdDKbXn1du4Gw3%2BP6vAOfbQfpQQf5opihUVCRglgMYQRItEa00WNzidz71btINtaGxibWAWHF5wkMh1X13Vs4p%2FvWRJ8Ir6PXG2W%2BP01m3NZuyxtHTvFE%3D
	public static void main(String[] args) throws Exception {
		String x = RsaUtil.sign("11111",
				"MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALYf/VSe4mmXWrDNFONOVEHWXq1lCPEh3IzAnFSRE8PiRLw354nKSeDcp0vuRbowTQT5xXD0tKprJF1Yy0RzIVXXJ57y8Lde4OUUbpc5AGumOfZmdp8MLXJ8CbsIj2VOs3ZccUGh7+qO0VZfMuJsB2f7AU/wNxuCLu4OYxnExU0zAgMBAAECgYBZvEtS/XIresLZqEKpePe09M3ze3u7a+bCh0i/tjfZ5UvevWDdGInsPGxF0mISyie40uSAsmGuMlpZkB2fXWbHknSOM6CX+AqYj/4Xtzn71N17TpM1du2hooL0Kl6tOlrYXWsSeG56x6fQ6Bbyd4NbDFgulBV/3g/WiQTLfpF6iQJBAOj6SZ84GmphF1MCDv0mVooozeCr0VaETnPiSvo4PWvTLlUJPDGzedcBq1D5J2UBRshdAQUgIiz9rDY8texhs0UCQQDIH0OVOyY2u37+vbliYD72Vexvw68ZUE/hRVw1vEeIwEdURZda/2TNIe59t4CcaS81dFRGhYt6sZjq664IH4oXAkEAv8TtfuKWU8Qxh8ElB1lcWIL33Bxxxs5HKXo2jzplzHBbLeHZK2v5OXKFSrSbqbdgRSLQ6SAnIXN5Infco4NUJQJBAKx07MvEjljqWTV0EDcr0/Z682cZTmLVKBFssVsJZuf9Mzr3QcjEgHyaeijSoz2nE2zQU/P2D83rEGmaMn6Dl38CQQDUgXyqi4UIJcHKm9WYsaMRu+QGmAtrry6CdVug2/X2v+vdNrPmoAniVn1IkTsDJGDxTaowoOBI5lkCRhHShX+C");
		System.out.println(x);
	}
}