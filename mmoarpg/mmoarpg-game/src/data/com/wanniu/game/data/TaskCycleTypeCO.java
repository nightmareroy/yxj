package com.wanniu.game.data; 

public class TaskCycleTypeCO { 

	/** Task分类 */
	public int taskCycle;
	/** 分类名称 */
	public String typeName;
	/** 是否有效 */
	public int isValid;
	/** 追回单价 */
	public int perPrice;

	/** 主键 */
	public int getKey() {
		return this.taskCycle; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}