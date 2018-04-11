package com.wanniu.game.data; 

public class MonDataConfigCO { 

	/** 参数名 */
	public String paramName;
	/** 参数值 */
	public float paramValue;

	/** 主键 */
	public String getKey() {
		return this.paramName; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}