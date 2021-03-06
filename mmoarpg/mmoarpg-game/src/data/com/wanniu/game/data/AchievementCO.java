package com.wanniu.game.data; 

public class AchievementCO { 

	/** 成就ID */
	public int id;
	/** 成就所属章节 */
	public int chapterID;
	/** 成就类型 */
	public int conditionType;
	/** 成就名称 */
	public String name;
	/** 成就描述 */
	public String des;
	/** 目标ID */
	public String targetID;
	/** 目标数量 */
	public int targetNum;
	/** 是否显示进度 */
	public int schedule;
	/** 是否广播 */
	public int broadCast;
	/** 成就图标 */
	public String icon;
	/** 奖励key */
	public String awardKey;
	/** 奖励值 */
	public int awardValue;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}