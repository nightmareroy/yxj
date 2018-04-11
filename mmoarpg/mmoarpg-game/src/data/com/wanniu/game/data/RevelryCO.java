package com.wanniu.game.data; 

public class RevelryCO { 

	/** 页签ID */
	public String tabID;
	/** 标签页名称 */
	public String tabName;
	/** 活动类型 */
	public String activityTab;
	/** 活动名称1 */
	public String activityName1;
	/** 活动编号 */
	public String activityID;
	/** 活动名称2 */
	public String activityName2;
	/** 是否开放 */
	public int isOpen;
	/** 活动描述 */
	public String activityDesc;
	/** 活动键值 */
	public int activityKey;
	/** 排名显示值 */
	public String activityKey2;
	/** 活动banner图片 */
	public String banner;
	/** 活动称号图片 */
	public String icon;
	/** 开服后几天结束倒计时 */
	public int endDays1;
	/** 活动入口关闭 */
	public int endDays2;
	/** 活动规则 */
	public String activityRule;
	/** 查看榜单 */
	public String goTo1;
	/** 我要变强 */
	public String goTo2;

	/** 主键 */
	public String getKey() {
		return this.tabID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}