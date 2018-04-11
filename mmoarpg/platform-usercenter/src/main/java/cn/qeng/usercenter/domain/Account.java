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
package cn.qeng.usercenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 保存一下走我们这里登录过的账号信息.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Entity
@Table(name = "account")
public class Account {
	// 游戏服务器最终用的UID
	@Id
	@Column(name = "username", length = 64)
	private String username;

	// 大渠道编号
	@Column(name = "channel", nullable = false, length = 64)
	private String channel;
	// 子渠道名称
	@Column(name = "subchannel", length = 64)
	private String subchannel;
	// 子渠道的UID
	@Column(name = "subchannel_uid", length = 256)
	private String subchannelUid;

	@Column(name = "sdk_uid", length = 256)
	private String sdkUid;

	@Column(name = "app_id", nullable = false, length = 64)
	private String appId;

	@Column(name = "ip", length = 32)
	private String ip;

	@Column(name = "mac", length = 256)
	private String mac;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "os", length = 256)
	private String os;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSubchannelUid() {
		return subchannelUid;
	}

	public void setSubchannelUid(String subchannelUid) {
		this.subchannelUid = subchannelUid;
	}

	public String getSubchannel() {
		return subchannel;
	}

	public void setSubchannel(String subchannel) {
		this.subchannel = subchannel;
	}

	public String getSdkUid() {
		return sdkUid;
	}

	public void setSdkUid(String sdkUid) {
		this.sdkUid = sdkUid;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
}