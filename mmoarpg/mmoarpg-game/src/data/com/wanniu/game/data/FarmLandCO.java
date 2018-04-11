package com.wanniu.game.data; 

public class FarmLandCO { 

	/** 编号 */
	public int iD;
	/** 需要等级 */
	public int level;
	/** 需要元宝 */
	public int diamond;
	/** 需要月卡 */
	public int monthCard;
	/** 需要尊享卡卡 */
	public int vmCard;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}