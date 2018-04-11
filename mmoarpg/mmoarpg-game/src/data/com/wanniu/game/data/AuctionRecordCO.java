package com.wanniu.game.data; 

public class AuctionRecordCO { 

	/** 编号 */
	public int recordID;
	/** 内容 */
	public String recordMsg;

	/** 主键 */
	public int getKey() {
		return this.recordID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}