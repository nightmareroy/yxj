package com.wanniu.game.data; 

public class DiceTurnRewardCO { 

	/** 轮次 */
	public int id;
	/** 物品奖励 */
	public String item;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}