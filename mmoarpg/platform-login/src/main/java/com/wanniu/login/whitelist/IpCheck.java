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
package com.wanniu.login.whitelist;

import java.util.Arrays;

/**
 * IP偏移.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class IpCheck extends WhitelistCheck {
	private final String config;
	private final int[] start = new int[4];
	private final int[] end = new int[4];
	private final long lifetime;

	public IpCheck(String config, long lifetime) {
		this.config = config;
		this.lifetime = lifetime;

		String[] ips = config.split("\\.");
		for (int i = 0; i < ips.length; i++) {
			// 星号0-255
			if ("*".equals(ips[i])) {
				start[i] = 0;
				end[i] = 255;
			}
			// 有链接号的区间写法
			else if (ips[i].indexOf("-") > -1) {
				String[] is = ips[i].split("-", 2);
				start[i] = Integer.parseInt(is[0]);
				end[i] = Integer.parseInt(is[1]);
			}
			//
			else {
				start[i] = end[i] = Integer.parseInt(ips[i]);
			}
		}
	}

	@Override
	public boolean check(String ip) {
		// 检测时间
		if (lifetime > 0 && System.currentTimeMillis() > lifetime) {
			return false;
		}

		// 检测IP段
		String[] ips = ip.split("\\.");
		for (int i = 0; i < ips.length; i++) {
			int v = Integer.parseInt(ips[i]);
			if (start[i] > v || v > end[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "IpOffset [config=" + config + ", start=" + Arrays.toString(start) + ", end=" + Arrays.toString(end) + ", lifetime=" + lifetime + "]";
	}
}