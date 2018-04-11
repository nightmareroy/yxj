package com.wanniu.game.data; 

public class ExchangeMallCO { 

	/** 排序 */
	public int sort;
	/** 编号 */
	public String iD;
	/** 道具编号 */
	public String itemCode;
	/** 道具名称 */
	public String name;
	/** 数量 */
	public int num;
	/** 兑换需求 */
	public String exchangeNeed;
	/** 是否有效 */
	public int isShow;
	/** 兑换后是否绑定 */
	public int isBind;
	/** 页签类型 */
	public int itemType;
	/** 是否显示倒计时 */
	public int countDown;
	/** 限时起点 */
	public String periodStart;
	/** 限时终点 */
	public String periodEnd;
	/** 终身购买次数 */
	public int totalTimes;
	/** 每天可兑换次数 */
	public int exchangeTimes;
	/** 每周可兑换次数 */
	public int weekExchangeTimes;
	/** 兑换次数用完是否消失 */
	public int isUseOut;
	/** 兑换等级 */
	public int reqLvl;
	/** VIP等级 */
	public int vIP;
	/** 商品角标 */
	public int series;
	/** 替换图片 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}