package com.wanniu.game.data; 

public class TowerBonusCO { 

	/** 编号 */
	public int iD;
	/** 周日期 */
	public String weekDay;
	/** 周日期显示文字 */
	public String weekDayTips;
	/** 奖励物品列表 */
	public String itemList;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}