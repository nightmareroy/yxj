package com.wanniu.game.data; 

public class SevDayTaskCO { 

	/** 任务ID */
	public int iD;
	/** 任务类型 */
	public int style;
	/** 任务所属日期 */
	public int date;
	/** 任务描述 */
	public String describe;
	/** 目标数量 */
	public int targetNum;
	/** 是否可提前完成 */
	public int advancedDown;
	/** 字符串参数1 */
	public String strParameter1;
	/** 字符串参数2 */
	public String strParameter2;
	/** 字符串参数3 */
	public String strParameter3;
	/** 字符串参数4 */
	public String strParameter4;
	/** 数字参数1 */
	public int numParameter1;
	/** 数字参数2 */
	public int numParameter2;
	/** 数字参数3 */
	public int numParameter3;
	/** 数字参数4 */
	public int numParameter4;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}