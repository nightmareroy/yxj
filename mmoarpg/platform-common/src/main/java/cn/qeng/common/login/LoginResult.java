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
 * 用户中心返回的登录结果.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class LoginResult {
	private boolean success = false;
	private String username;// 渠道UID
	private String subchannelUid;// 子渠道UID
	private String desc;

	public LoginResult() {}

	public LoginResult(boolean success, String desc) {
		this.success = success;
		this.desc = desc;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDesc() {
		return desc;
	}

	public String getSubchannelUid() {
		return subchannelUid;
	}

	public void setSubchannelUid(String subchannelUid) {
		this.subchannelUid = subchannelUid;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}