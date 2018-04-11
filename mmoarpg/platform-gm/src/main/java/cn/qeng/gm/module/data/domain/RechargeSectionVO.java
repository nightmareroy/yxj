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
 * 充值区间VO.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class RechargeSectionVO {
	/**
	 * 数据时间
	 */
	private String date;
	/**
	 * 划分区间，{ 1, 6, 30, 98, 198, 328, 648, 3240, 5000 };
	 */
	private int v1;
	private int v6;
	private int v30;
	private int v98;
	private int v198;
	private int v328;
	private int v648;
	private int v3240;
	private int v5000;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getV1() {
		return v1;
	}

	public void setV1(int v1) {
		this.v1 = v1;
	}

	public int getV6() {
		return v6;
	}

	public void setV6(int v6) {
		this.v6 = v6;
	}

	public int getV30() {
		return v30;
	}

	public void setV30(int v30) {
		this.v30 = v30;
	}

	public int getV98() {
		return v98;
	}

	public void setV98(int v98) {
		this.v98 = v98;
	}

	public int getV198() {
		return v198;
	}

	public void setV198(int v198) {
		this.v198 = v198;
	}

	public int getV328() {
		return v328;
	}

	public void setV328(int v328) {
		this.v328 = v328;
	}

	public int getV648() {
		return v648;
	}

	public void setV648(int v648) {
		this.v648 = v648;
	}

	public int getV3240() {
		return v3240;
	}

	public void setV3240(int v3240) {
		this.v3240 = v3240;
	}

	public int getV5000() {
		return v5000;
	}

	public void setV5000(int v5000) {
		this.v5000 = v5000;
	}
}