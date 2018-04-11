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
package cn.qeng.gm.module.data.domain;

/**
 * 每日数据
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class DailyResult {
	private String today;
	private int loginIdNum;
	private int createIdNum;

	private int maxOnlineNum;
	private int avgOnlineNum;
	private int rechargeRmb;
	private int rechargeNum;
	private int rechargeCount;

	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

	public int getLoginIdNum() {
		return loginIdNum;
	}

	public void setLoginIdNum(int loginIdNum) {
		this.loginIdNum = loginIdNum;
	}

	public int getCreateIdNum() {
		return createIdNum;
	}

	public void setCreateIdNum(int createIdNum) {
		this.createIdNum = createIdNum;
	}

	public int getMaxOnlineNum() {
		return maxOnlineNum;
	}

	public void setMaxOnlineNum(int maxOnlineNum) {
		this.maxOnlineNum = maxOnlineNum;
	}

	public int getAvgOnlineNum() {
		return avgOnlineNum;
	}

	public void setAvgOnlineNum(int avgOnlineNum) {
		this.avgOnlineNum = avgOnlineNum;
	}

	public int getRechargeRmb() {
		return rechargeRmb;
	}

	public void setRechargeRmb(int rechargeRmb) {
		this.rechargeRmb = rechargeRmb;
	}

	public int getRechargeNum() {
		return rechargeNum;
	}

	public void setRechargeNum(int rechargeNum) {
		this.rechargeNum = rechargeNum;
	}

	public int getRechargeCount() {
		return rechargeCount;
	}

	public void setRechargeCount(int rechargeCount) {
		this.rechargeCount = rechargeCount;
	}
}
