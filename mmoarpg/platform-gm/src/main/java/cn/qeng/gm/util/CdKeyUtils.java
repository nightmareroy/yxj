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
package cn.qeng.gm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * CDKEY生成工具类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class CdKeyUtils {
	private static final Random r = new Random();
	// 去除字符O，I,数字0,1,--顺序有意义
	private static final char[] CHARS = { 'S', 'A', '2', 'B', 'T', 'C', '3', 'D', 'U', 'E', '4', 'F', 'V', 'G', '5', 'H', 'W', 'J', '6', 'K', 'X', 'L', '7', 'M', 'Y', 'N', '8', 'P', 'Z', '9', 'Q', 'R' };
	// 最大值就是字母Z的ASCII值.
	private static final int[] NUMS = new int['Z' + 1];
	static {
		for (int i = 0, len = CHARS.length; i < len; i++) {
			NUMS[CHARS[i]] = i;
		}
	}

	/**
	 * @param pid 最大值为10000
	 * @param bid 最大值为 4095
	 * @param max 最大值 10W
	 * @return 返回生成的兑换码
	 */
	public static List<String> gen(int pid, int bid, int max) {
		return new ArrayList<>(build(pid, bid, max));
	}

	private static Set<String> build(long pid, long bid, int max) {
		Set<String> result = new HashSet<>();
		int step = 0xFFFFFF / max;
		long randomNum = 0;
		while (max-- > 0) {
			randomNum += (r.nextInt(step) + 1);
			long cdkey = pid << 36 | randomNum << 12 | bid;
			result.add(cdkey2Code(cdkey));
		}
		return result;
	}

	private static String cdkey2Code(long cdkey) {
		final char[] c = new char[11];
		int sum = 0;
		for (int i = 0; i < 10; i++) {
			c[9 - i] = CHARS[(int) (cdkey % 32)];
			cdkey >>>= 5;
			sum += c[9 - i] * (9 - i + 1);
		}
		if (cdkey > 0) {
			throw new RuntimeException("CDKEY生成异常...");
		}
		// 添加校验位...
		c[10] = CHARS[sum % 32];
		return new String(c);
	}

	public static Cdkey codeToCdkey(String code) {
		if (StringUtils.isEmpty(code) || code.length() != 11) {
			return new Cdkey(false);
		}
		long id = NUMS[code.charAt(0)];
		int sum = code.charAt(0);
		for (int i = 1; i < 10; i++) {
			id <<= 5;
			char n = code.charAt(i);
			sum += n * (i + 1);
			id |= NUMS[n];
		}
		if (CHARS[sum % 32] == code.charAt(10)) {
			Cdkey result = new Cdkey(true);
			result.setBid((int) (id & 0xFFF));
			result.setPid((int) (id >> 36));
			return result;
		}
		return new Cdkey(false);
	}

	public static class Cdkey {
		private boolean success;
		private int pid;// 礼包ID
		private int bid;// 批次ID

		public Cdkey(boolean success) {
			this.success = success;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public int getPid() {
			return pid;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public int getBid() {
			return bid;
		}

		public void setBid(int bid) {
			this.bid = bid;
		}
	}

	public static void main(String[] args) {
		// gen(100000, 3, 10_000);
		List<String> result = gen(3, 12, 100);
		System.out.println(result);
		System.out.println(codeToCdkey("SS36X73XSVY"));

	}
}