package com.wanniu.game.data; 

public class ZillionaireFreeCO { 

	/** 排序 */
	public int sort;
	/** 目标名称 */
	public String taskName;
	/** 任务ID */
	public int taskID;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}