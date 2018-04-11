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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 权限组实体类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "auth_group")
public class AuthGroup {
	// id
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	// name
	@Column(name = "name", nullable = false, length = 256)
	private String name;
	// 权限字符串
	@Lob
	@Column(name = "auth", nullable = false, length = 65535)
	private String auth;

	// 超级管理员的标识（拥有全部权限，且不能修改）
	@Column(name = "superman", nullable = false)
	private boolean superman;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;
	// 平台
	@Lob
	@Column(name = "platforms", nullable = false)
	private String platforms;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public boolean isSuperman() {
		return superman;
	}

	public void setSuperman(boolean superman) {
		this.superman = superman;
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

	public String getPlatforms() {
		return platforms;
	}

	public void setPlatforms(String platforms) {
		this.platforms = platforms;
	}

}