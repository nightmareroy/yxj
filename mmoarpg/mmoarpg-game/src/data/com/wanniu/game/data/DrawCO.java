package com.wanniu.game.data; 

public class DrawCO { 

	/** 编号 */
	public int iD;
	/** 类型 */
	public String type;
	/** 道具数 */
	public int itemNumber;
	/** 进入奖池轮数 */
	public int round;
	/** 道具库 */
	public String propLibrary;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}