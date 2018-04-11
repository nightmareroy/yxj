package com.wanniu.game.data; 

public class ConsumeCO { 

	/** 货币ID */
	public int iD;
	/** 红包消耗货币种类 */
	public String consumeType;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}