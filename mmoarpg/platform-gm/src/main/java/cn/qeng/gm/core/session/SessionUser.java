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
package cn.qeng.gm.core.session;

import java.util.Date;

import cn.qeng.gm.core.auth.AuthCache;
import cn.qeng.gm.module.backstage.domain.User;

/**
 * Session用户对象.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class SessionUser {
	private String username;// 账号
	private String name;// 名称
	private String remarks = "目标：做一款赚钱的游戏...";// 备注
	private int head = 1;// 头像编号
	private AuthCache auth;// 权限

	// 上次登录时间
	private Date lastLoginTime;
	// 上次登录信息
	private String lastLoginIp;
	// 上次登录城市
	private String lastLoginCity;

	// 本次登录时间
	private Date loginTime;
	// 本次登录信息
	private String loginIp;
	// 本次登录城市
	private String loginCity;

	public SessionUser(User user) {
		this.username = user.getUsername();
		this.name = user.getName();
		this.head = (user.getName().hashCode() % 3) * 2 + 1;

		this.lastLoginCity = user.getLastLoginCity();
		this.lastLoginIp = user.getLastLoginIp();
		this.lastLoginTime = user.getLastLoginTime();

		this.loginCity = user.getLoginCity();
		this.loginIp = user.getLoginIp();
		this.loginTime = user.getLoginTime();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public AuthCache getAuth() {
		return auth;
	}

	public void setAuth(AuthCache auth) {
		this.auth = auth;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getLastLoginCity() {
		return lastLoginCity;
	}

	public void setLastLoginCity(String lastLoginCity) {
		this.lastLoginCity = lastLoginCity;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getLoginCity() {
		return loginCity;
	}

	public void setLoginCity(String loginCity) {
		this.loginCity = loginCity;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}
}