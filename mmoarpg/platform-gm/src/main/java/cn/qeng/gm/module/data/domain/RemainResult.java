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
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class RemainResult {
	/**
	 * 数据时间
	 */
	private String date;

	/**
	 * 基数
	 */
	private int createNum;

	/**
	 * 2日留存
	 */
	private int remain2;
	private int remain3;
	private int remain4;
	private int remain5;
	private int remain6;
	private int remain7;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getRemain2() {
		return remain2;
	}

	public void setRemain2(int remain2) {
		this.remain2 = remain2;
	}

	public int getRemain3() {
		return remain3;
	}

	public void setRemain3(int remain3) {
		this.remain3 = remain3;
	}

	public int getRemain4() {
		return remain4;
	}

	public void setRemain4(int remain4) {
		this.remain4 = remain4;
	}

	public int getRemain5() {
		return remain5;
	}

	public void setRemain5(int remain5) {
		this.remain5 = remain5;
	}

	public int getRemain6() {
		return remain6;
	}

	public void setRemain6(int remain6) {
		this.remain6 = remain6;
	}

	public int getRemain7() {
		return remain7;
	}

	public void setRemain7(int remain7) {
		this.remain7 = remain7;
	}

	public int getCreateNum() {
		return createNum;
	}

	public void setCreateNum(int createNum) {
		this.createNum = createNum;
	}
}