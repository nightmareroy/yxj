package com.wanniu.game.data; 

public class BlessBuffCO { 

	/** BUFFID */
	public int blessBuffID;
	/** BUFF名称 */
	public String blessBuff;
	/** 增加属性1 */
	public String buffAttribute1;
	/** 属性数值1 */
	public int buffValue1;
	/** 属性类型 */
	public int buffValueType;
	/** BUFF持续时间 */
	public int bufftime;

	/** 主键 */
	public int getKey() {
		return this.blessBuffID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}