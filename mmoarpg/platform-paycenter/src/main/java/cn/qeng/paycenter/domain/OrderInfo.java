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
package cn.qeng.paycenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * 订单信息.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Entity
@Table(name = "order_info")
public class OrderInfo {
	@Id
	@Column(name = "id")
	private String id;// 订单号

	// 乐观锁
	@Version
	@Column(name = "version", nullable = false)
	private int version;

	// 应用编号
	@Column(name = "app_id", nullable = false)
	private int appId;

	// 渠道
	@Column(name = "channel", nullable = false, length = 32)
	private String channel;

	// 子渠道
	@Column(name = "subchannel", length = 32)
	private String subchannel;

	// 服务器编号
	@Column(name = "server_id", nullable = false)
	private int serverId;

	// 用户名（UID）
	@Column(name = "username", nullable = false, length = 64)
	private String username;

	// 角色ID
	@Column(name = "role_id", nullable = false, length = 64)
	private String roleId;

	// 充值金额
	@Column(name = "money", nullable = false)
	private int money;

	// 下单时间...
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;

	// 订单状态
	@Column(name = "state", nullable = false)
	private int state;

	// 发货通知时间
	@Column(name = "notify_time")
	private Date notifyTime;
	// 发货通知参数备案
	@Column(name = "notify_params", length = 512)
	private String notifyParams;
	// 发货通知者IP
	@Column(name = "notify_ip", length = 32)
	private String notifyIp;
	// 第三方交易流水号
	@Column(name = "notify_trans_id", length = 64)
	private String notifyTransId;

	// 对游戏发货相关记录
	@Column(name = "try_time")
	private Date tryTime;// 尝试发货时间
	@Column(name = "try_count")
	private int tryCount;// 尝试发货次数

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSubchannel() {
		return subchannel;
	}

	public void setSubchannel(String subchannel) {
		this.subchannel = subchannel;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getNotifyParams() {
		return notifyParams;
	}

	public void setNotifyParams(String notifyParams) {
		this.notifyParams = notifyParams;
	}

	public String getNotifyIp() {
		return notifyIp;
	}

	public void setNotifyIp(String notifyIp) {
		this.notifyIp = notifyIp;
	}

	public Date getTryTime() {
		return tryTime;
	}

	public void setTryTime(Date tryTime) {
		this.tryTime = tryTime;
	}

	public int getTryCount() {
		return tryCount;
	}

	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	public String getNotifyTransId() {
		return notifyTransId;
	}

	public void setNotifyTransId(String notifyTransId) {
		this.notifyTransId = notifyTransId;
	}
}