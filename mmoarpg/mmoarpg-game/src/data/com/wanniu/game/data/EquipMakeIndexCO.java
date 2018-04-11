package com.wanniu.game.data; 

public class EquipMakeIndexCO { 

	/** 编号 */
	public int iD;
	/** 装备标签 */
	public String equipLable;
	/** 装备等级 */
	public int levelIndex;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}