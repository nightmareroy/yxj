package com.wanniu.game.data; 

public class ScheduleCO { 

	/** 任务ID */
	public int schID;
	/** 任务名称 */
	public String schName;
	/** 排序 */
	public int sort;
	/** 是否有效 */
	public int isValid;
	/** 活跃度次数上限 */
	public int maxCount;
	/** 可参与次数上限 */
	public int maxAttend;
	/** 事件类型 */
	public int type;
	/** 目标 */
	public String target;
	/** 时间描述 */
	public String timeDesc;
	/** 开放时段 */
	public String openPeriod;
	/** 开放周期 */
	public String openday;
	/** 周历内展示时间 */
	public String periodInCalendar;
	/** 是否在周历内展示 */
	public int isShowInCalendar;
	/** 奖励活跃度 */
	public int vitBonus;
	/** 副本ID */
	public String mapID;
	/** 事件类型 */
	public int funType;
	/** 快捷入口 */
	public String funID;
	/** 任务图标 */
	public String icon;
	/** 任务角标 */
	public int script;
	/** 参与等级 */
	public int lvLimit;
	/** 活动形式 */
	public String form;
	/** 活动描述 */
	public String activDesc;
	/** 奖励预览 */
	public String rewardPre;
	/** 跟随状态下是否可以前往 */
	public int goForInFollowingState;

	/** 主键 */
	public int getKey() {
		return this.schID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}