package com.wanniu.game.data; 

public class WelfareCO { 

	/** 活动编号 */
	public int id;
	/** 控件名称 */
	public String controlName;
	/** 活动名称 */
	public String btnText;
	/** xml名称 */
	public String xmlPath;
	/** 控件背景图片 */
	public String pic2d;
	/** 逻辑脚本 */
	public String logicScript;
	/** 活动索引ID */
	public int activityID;
	/** 红点 */
	public int flagStatus;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}