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

import java.util.HashMap;
import java.util.Map;

/**
 * 权限缓存，用于Session共享.
 * <p>
 * 并发问题，不要对权限Map做添加移除操作，整换引用...
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public final class AuthCache {
	private String name;
	private boolean superman;
	private Map<String, Boolean> auths = new HashMap<>();

	public boolean isSuperman() {
		return superman;
	}

	public void setSuperman(boolean superman) {
		this.superman = superman;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuths(Map<String, Boolean> auths) {
		this.auths = auths;
	}

	public boolean isEmpty() {
		return auths.isEmpty();
	}

	public boolean contains(String name) {
		return auths.containsKey(name);
	}

	// 这个名字不要乱改，有页面索引此方法名
	public Map<String, Boolean> getHave() {
		return auths;
	}
}