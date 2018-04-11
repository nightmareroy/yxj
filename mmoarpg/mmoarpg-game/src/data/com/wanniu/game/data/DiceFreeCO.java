package com.wanniu.game.data; 

public class DiceFreeCO { 

	/** 序号 */
	public int id;
	/** 完成条件 */
	public int condition;
	/** 奖励次数 */
	public int rewardNum;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}