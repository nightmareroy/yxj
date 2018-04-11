package com.wanniu.game.data; 

public class ShopMallItemsCO { 

	/** 排序 */
	public int sort;
	/** 编号 */
	public String iD;
	/** 道具编号 */
	public String itemCode;
	/** 道具名称 */
	public String name;
	/** 是否有效 */
	public int isShow;
	/** 购买后是否绑定 */
	public int isBind;
	/** 数量 */
	public int num;
	/** 页签类型 */
	public int itemType;
	/** 消耗类型 */
	public int consumeType;
	/** 原价 */
	public int price;
	/** 积分 */
	public int points;
	/** 是否可试穿 */
	public int isTst;
	/** 限时起点 */
	public String periodStart;
	/** 限时终点 */
	public String periodEnd;
	/** 限时折扣价 */
	public int price2;
	/** 每天可购买次数 */
	public int buyTimes;
	/** 每周可购买次数 */
	public int weekBuyTimes;
	/** 购买次数用完是否消失 */
	public int isUseOut;
	/** 购买等级 */
	public int reqLvl;
	/** VIP等级 */
	public int vIP;
	/** 商品角标 */
	public int series;
	/** 替换图片 */
	public String icon;
	/** 是否显示倒计时 */
	public int countDown;
	/** 是否全服限购 */
	public int serveLimit;
	/** 全服限购数量 */
	public int serveBuyTimes;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}