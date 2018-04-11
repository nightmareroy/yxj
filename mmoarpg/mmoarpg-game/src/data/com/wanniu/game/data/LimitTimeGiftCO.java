package com.wanniu.game.data; 

public class LimitTimeGiftCO { 

	/** 序号 */
	public int id;
	/** 礼包名称 */
	public String name;
	/** 触发条件 */
	public int condition;
	/** 参数 */
	public int value;
	/** 角色最低等级 */
	public int minLevel;
	/** 角色最高等级 */
	public int maxLevel;
	/** 触发概率 */
	public int pushPro;
	/** 当天只推送一次 */
	public int onlyPushOne;
	/** 物品奖励 */
	public String rewardItem;
	/** 时效 */
	public int limitTime;
	/** 价格 */
	public int price;
	/** 图集路径 */
	public String atlasRoute;
	/** 编号 */
	public String atlasID;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}