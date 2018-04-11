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
package cn.qeng.common.login;

/**
 * 登录成功后写入Redis给游戏服的信息.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class TokenInfo {

	// 游戏的用户ID（uid=channel-channelUid）
	private String uid;

	// 渠道
	private String channel;
	// 渠道的用户ID
	private String channelUid;

	// 子渠道
	private String subchannel;
	// 子渠道的用户ID
	private String subchannelUid;

	// MAC地址
	private String mac;
	// 系统类型(ios是5，安卓是6)
	private String os;
	// 登录Token
	private String accessToken;

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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getChannelUid() {
		return channelUid;
	}

	public void setChannelUid(String channelUid) {
		this.channelUid = channelUid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSubchannelUid() {
		return subchannelUid;
	}

	public void setSubchannelUid(String subchannelUid) {
		this.subchannelUid = subchannelUid;
	}
}