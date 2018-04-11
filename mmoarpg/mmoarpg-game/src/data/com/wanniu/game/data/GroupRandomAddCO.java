package com.wanniu.game.data; 

public class GroupRandomAddCO { 

	/** 物品分组 */
	public int groupID;
	/** 组权重 */
	public int groupProb;

	/** 主键 */
	public int getKey() {
		return this.groupID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}