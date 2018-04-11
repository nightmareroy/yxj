package com.wanniu.game.data; 

public class EventCO { 

	/** 事件ID */
	public String eventID;
	/** 事件类型 */
	public int eventType;
	/** 事件参数1 */
	public String eventData1;
	/** 事件参数2 */
	public String eventData2;
	/** 事件参数3 */
	public String eventData3;

	/** 主键 */
	public String getKey() {
		return this.eventID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}