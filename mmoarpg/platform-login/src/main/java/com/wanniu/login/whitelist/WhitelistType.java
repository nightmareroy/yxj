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

import cn.qeng.common.gm.RedisKeyConst;

/**
 * 白名单类型.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public enum WhitelistType {
	/**
	 * 白名单IP
	 */
	WHITELIST_IP(RedisKeyConst.REDIS_KEY_WHITELIST_IP, true),
	/**
	 * 白名单UID
	 */
	WHITELIST_UID(RedisKeyConst.REDIS_KEY_WHITELIST_UID, false),
	/**
	 * 黑名单IP
	 */
	BLACKLIST_IP(RedisKeyConst.REDIS_KEY_BLACKLIST_IP, true),
	/**
	 * 黑名单UID
	 */
	BLACKLIST_UID(RedisKeyConst.REDIS_KEY_BLACKLIST_UID, false);

	private final String rediskey;
	private final boolean ip;

	private WhitelistType(String rediskey, boolean ip) {
		this.ip = ip;
		this.rediskey = rediskey;
	}

	public String getRediskey() {
		return rediskey;
	}

	public boolean isIp() {
		return ip;
	}
}