package com.wanniu.game.data; 

public class NoticeSendCO { 

	/** 任务ID */
	public int schID;
	/** 任务名称 */
	public String schName;
	/** 是否有效 */
	public int isValid;
	/** 时间描述 */
	public String timeDesc;
	/** 开启日期 */
	public String startDay;
	/** 开始时间 */
	public String startTime;
	/** 结束时间 */
	public String endTime;
	/** 开始前第一次发布 */
	public int firstTime;
	/** 开始前第二次发布 */
	public int secondTime;
	/** 提前公告内容 */
	public String showNotice;
	/** 间隔时间2 */
	public int spaceTime2;
	/** 活动公告内容 */
	public String showNotice2;
	/** 结束前第一次发布 */
	public int firstTime2;
	/** 结束前第二次发布 */
	public int secondTime2;
	/** 公告内容 */
	public String showNotice3;

	/** 主键 */
	public int getKey() {
		return this.schID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}