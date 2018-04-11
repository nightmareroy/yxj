package com.wanniu.game.data; 

public class DebugParametersCO { 

	/** 参数名称 */
	public String paramName;
	/** 参数类型 */
	public String paramType;
	/** 参数值 */
	public String paramValue;

	/** 主键 */
	public String getKey() {
		return this.paramName; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}