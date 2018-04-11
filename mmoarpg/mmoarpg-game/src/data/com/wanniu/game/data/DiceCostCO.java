package com.wanniu.game.data; 

public class DiceCostCO { 

	/** 轮次 */
	public int turn;
	/** 消耗绑元 */
	public int cost;

	/** 主键 */
	public int getKey() {
		return this.turn; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}