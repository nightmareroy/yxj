package com.wanniu.game.data; 

public class FateCO { 

	/** 编号 */
	public int iD;
	/** 事件名称 */
	public String event;
	/** 事件类型 */
	public int eventType;
	/** 单次获取仙缘值 */
	public int singleNum;
	/** 每日获取上限 */
	public int numLimit;
	/** 简略描述 */
	public String briefDesc;
	/** 详尽描述 */
	public String detailDesc;
	/** 任务图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}