package com.wanniu.game.data; 

public class CardCO { 

	/** 套餐ID */
	public int iD;
	/** 名称 */
	public String name;
	/** 货币类型 */
	public String payMoneyType;
	/** 货币金额 */
	public int payMoneyAmount;
	/** 购买获得元宝 */
	public int payDiamond;
	/** 每日赠送绑元 */
	public int dailyDW;
	/** 持续时间 */
	public int lastTime;
	/** 解锁绑元商店 */
	public int prv1;
	/** 杀怪经验加成 */
	public int prv2;
	/** 银两收益加成 */
	public int prv3;
	/** 寄卖行摊位增加 */
	public int prv4;
	/** 仓库栏位增加 */
	public int prv5;
	/** 背包栏位增加 */
	public int prv6;
	/** 限购物品的上限增加 */
	public int prv7;
	/** 每日发放免费喇叭 */
	public int prv8;
	/** 特权称号 */
	public String prv9;
	/** 每日免费复活次数 */
	public int prv10;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}