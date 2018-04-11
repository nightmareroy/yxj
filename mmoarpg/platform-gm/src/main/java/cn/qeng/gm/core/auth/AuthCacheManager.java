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
package cn.qeng.gm.core.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.module.backstage.domain.AuthGroup;

/**
 * 权限缓存管理类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Component
public class AuthCacheManager {
	private static final Map<Integer, AuthCache> caches = new ConcurrentHashMap<>();

	public synchronized void update(AuthGroup group) {
		AuthCache cache = caches.computeIfAbsent(group.getId(), key -> new AuthCache());
		cache.setName(group.getName());
		cache.setSuperman(group.isSuperman());

		if (group.isSuperman()) {
			cache.setAuths(Arrays.stream(AuthResource.values()).collect(Collectors.toMap(AuthResource::name, key -> Boolean.TRUE)));
		} else {
			List<String> auths = JSON.parseArray(group.getAuth(), String.class);
			if (auths != null && !auths.isEmpty()) {
				cache.setAuths(auths.stream().collect(Collectors.toMap(Function.identity(), key -> Boolean.TRUE)));
			}
		}
	}

	public AuthCache getAuthCache(Integer authGroupId) {
		return caches.get(authGroupId);
	}
}