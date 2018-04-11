package com.wanniu.game.data; 

public class FarmRecordCO { 

	/** 动态类型 */
	public int recordType;
	/** 内容 */
	public String recordMsg;

	/** 主键 */
	public int getKey() {
		return this.recordType; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}