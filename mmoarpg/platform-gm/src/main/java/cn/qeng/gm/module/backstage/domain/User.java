/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
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
package cn.qeng.gm.module.backstage.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户实体类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "user")
public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "username", nullable = false, length = 128, unique = true)
	private String username;
	@Column(name = "password", nullable = false, length = 36)
	private String password;

	// 名称
	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;

	// 权限组ID
	@Column(name = "auth_group_id", nullable = false)
	private int authGroupId;

	// 登录方式ID
	@Column(name = "loginPlatformId", nullable = false, length = 128)
	private String loginPlatformId;

	// 账号状态
	@Column(name = "status", nullable = false)
	private int status;

	// 登录累计错误次数
	@Column(name = "error_count", nullable = false)
	private int errorCount;

	// 上次登录时间
	@Column(name = "last_login_time")
	private Date lastLoginTime;
	// 上次登录信息
	@Column(name = "last_login_ip", length = 16)
	private String lastLoginIp;
	// 上次登录城市
	@Column(name = "last_login_city", length = 128)
	private String lastLoginCity;

	// 本次登录时间
	@Column(name = "login_time")
	private Date loginTime;
	// 本次登录信息
	@Column(name = "login_ip", length = 16)
	private String loginIp;
	// 本次登录城市
	@Column(name = "login_city", length = 128)
	private String loginCity;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public int getAuthGroupId() {
		return authGroupId;
	}

	public void setAuthGroupId(int authGroupId) {
		this.authGroupId = authGroupId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
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

	public String getLoginPlatformId() {
		return loginPlatformId;
	}

	public void setLoginPlatformId(String loginPlatformId) {
		this.loginPlatformId = loginPlatformId;
	}

}