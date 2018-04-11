package com.wanniu.game.data; 

public class GTechnologyLevelCO { 

	/** 等级 */
	public int technologyLevel;
	/** 需要资金 */
	public int funds;
	/** 需要金币 */
	public int gold;
	/** 需要公会等级 */
	public int gLevel;
	/** 每日刷新产出道具个数 */
	public int techItemDayCount;
	/** 最大技能等级 */
	public int maxSkill;

	/** 主键 */
	public int getKey() {
		return this.technologyLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}