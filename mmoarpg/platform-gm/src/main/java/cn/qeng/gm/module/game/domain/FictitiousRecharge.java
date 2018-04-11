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
package cn.qeng.gm.module.game.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 模拟充值实体类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "fictitious_recharge")
public class FictitiousRecharge {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "server_id", nullable = false)
	private int serverId;

	// 玩家信息（可能是Id也可能是名称）
	@Column(name = "type", nullable = false)
	private int type;
	@Column(name = "player", nullable = false, length = 64)
	private String player;

	// 充值的编号
	@Column(name = "productId", nullable = false)
	private int productId;

	// 充值时间
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	// 充值原因
	@Column(name = "recharge_reson", nullable = false, length = 128)
	private String rechargeReson;

	// 申请人的账号和名称
	@Column(name = "username", nullable = false, length = 128)
	private String username;
	@Column(name = "name", nullable = false, length = 256)
	private String name;

	// 审批人
	@Column(name = "audit_username", length = 128)
	private String auditUsername;
	@Column(name = "audit_name", length = 256)
	private String auditName;

	// 发送成功，失败标识
	@Column(name = "singal", nullable = false)
	private int singal;

	@Column(name = "result", nullable = false)
	private int result;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRechargeReson() {
		return rechargeReson;
	}

	public void setRechargeReson(String rechargeReson) {
		this.rechargeReson = rechargeReson;
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

	public String getAuditUsername() {
		return auditUsername;
	}

	public void setAuditUsername(String auditUsername) {
		this.auditUsername = auditUsername;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public int getSingal() {
		return singal;
	}

	public void setSingal(int singal) {
		this.singal = singal;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}