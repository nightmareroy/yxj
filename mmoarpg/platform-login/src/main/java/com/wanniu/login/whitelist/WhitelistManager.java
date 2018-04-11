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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.core.util.IpUtils;

/**
 * 白名单管理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class WhitelistManager {
	private static final WhitelistManager instance = new WhitelistManager();
	// 缓存
	private static final EnumMap<WhitelistType, Set<WhitelistCheck>> caches = new EnumMap<>(WhitelistType.class);

	public static WhitelistManager getInstance() {
		return instance;
	}

	public void clearAll() {
		Out.info("清空所有白名单与黑名单");
		caches.clear();
	}

	/**
	 * 判定当前的IP黑名单.
	 */
	public boolean isBlackListByIP(String ip) {
		return in(WhitelistType.BLACKLIST_IP, ip);
	}

	/**
	 * 判定当前是否在UID黑名单.
	 */
	public boolean isBlackListByUID(String uid) {
		return in(WhitelistType.BLACKLIST_UID, uid);
	}

	/**
	 * 判定当前是否在白名单.
	 */
	public boolean isWhiteList(String ip, String uid) {
		// 局域网IP
		if (IpUtils.isInnerIP(ip)) {
			return true;
		}

		// 白名单IP
		if (in(WhitelistType.WHITELIST_IP, ip)) {
			return true;
		}

		// 白名单UID
		if (in(WhitelistType.WHITELIST_UID, uid)) {
			return true;
		}

		return false;
	}

	private boolean in(WhitelistType type, String value) {
		try {
			Set<WhitelistCheck> iplist = caches.computeIfAbsent(type, key -> loadData(key));
			return iplist.stream().filter(v -> v.check(value)).findFirst().isPresent();
		} catch (Exception e) {
			Out.warn("白名单判定异常", e);
			return false;
		}
	}

	private Set<WhitelistCheck> loadData(WhitelistType type) {
		Map<String, String> x = GCache.hgetAll(type.getRediskey());
		Set<WhitelistCheck> result = new HashSet<>(x.size());
		try {
			x.forEach((k, v) -> result.add(type.isIp() ? new IpCheck(k, Long.parseLong(v)) : new UidCheck(k, Long.parseLong(v))));
		} catch (Exception e) {
			Out.warn("白名单配置格式异常，ip=", x);
		}
		return result;
	}
}