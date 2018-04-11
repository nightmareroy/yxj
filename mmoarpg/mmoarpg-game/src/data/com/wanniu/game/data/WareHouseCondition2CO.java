package com.wanniu.game.data; 

public class WareHouseCondition2CO { 

	/** 序号 */
	public int order;
	/** 过滤名称 */
	public String conditionName;
	/** 过滤类型 */
	public int conditionType;
	/** 过滤代码 */
	public int conditionCode;

	/** 主键 */
	public int getKey() {
		return this.order; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}