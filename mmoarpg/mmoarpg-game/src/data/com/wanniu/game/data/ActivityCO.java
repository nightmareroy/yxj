package com.wanniu.game.data; 

public class ActivityCO { 

	/** 活动编号 */
	public int activityID;
	/** 标签页ID */
	public int tabID;
	/** 活动类型 */
	public int activityTab;
	/** 活动 */
	public String activity;
	/** 是否开放 */
	public int isOpen;
	/** 活动描述 */
	public String activityDesc;
	/** 活动键值 */
	public String activityKey;
	/** 活动页签图片 */
	public String icon;
	/** 角标路径 */
	public String corner;
	/** 界面ID */
	public int uIID;
	/** 开始时间 */
	public String openTime;
	/** 结束时间 */
	public String closeTime;
	/** 开服后几天 */
	public int days;
	/** 活动规则 */
	public String activityRule;
	/** 跳转标示 */
	public String goTo;
	/** 历史信息 */
	public int history;
	/** 渠道显示 */
	public int channelID;
	/** 活动初始参与人数 */
	public int joinNum;

	/** 主键 */
	public int getKey() {
		return this.activityID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}