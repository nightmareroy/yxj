package com.wanniu.game.data; 

public class TaskChestCO { 

	/** 主键 */
	public int iD;
	/** 任务类型 */
	public int kind;
	/** 人物等级 */
	public int charLevel;
	/** 奖励宝箱代码 */
	public String chest;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}