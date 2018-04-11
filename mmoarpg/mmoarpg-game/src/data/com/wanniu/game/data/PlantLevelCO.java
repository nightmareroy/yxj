package com.wanniu.game.data; 

public class PlantLevelCO { 

	/** 种植等级 */
	public int level;
	/** 需要经验 */
	public int exp;
	/** 产量加成 */
	public int harvestAdd;

	/** 主键 */
	public int getKey() {
		return this.level; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}