package com.wanniu.game.data; 

public class UpLevelEventCO { 

	/** 编号 */
	public int iD;
	/** 事件编号 */
	public int eventID;
	/** 事件名称 */
	public String eventName;
	/** 事件关键字 */
	public String eventKey;
	/** 事件参数 */
	public int eventPar;
	/** 事件达成数量 */
	public int eventCount;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}