package com.wanniu.game.data; 

public class DropListCO { 

	/** 编号 */
	public int iD;
	/** 周日期 */
	public String weekDay;
	/** 层数 */
	public int floorNo;
	/** 推荐战力 */
	public int fcValue;
	/** 首次通关奖励 */
	public String firstReward;
	/** 扫荡奖励 */
	public String itemView;
	/** 每周奖励 */
	public String weekReward;
	/** 场景ID */
	public int mapId;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}