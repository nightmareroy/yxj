package com.wanniu.game.data; 

public class MonsterConfigCO { 

	/** 参数名称 */
	public String param;
	/** 参数值 */
	public float paramValue;

	/** 主键 */
	public String getKey() {
		return this.param; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}