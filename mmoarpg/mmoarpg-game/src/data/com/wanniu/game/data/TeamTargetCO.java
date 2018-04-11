package com.wanniu.game.data; 

public class TeamTargetCO { 

	/** ID */
	public int iD;
	/** 列表页签 */
	public int tabType;
	/** 标签序列 */
	public int tabIndex;
	/** 目标名称 */
	public String targetName;
	/** 多人场景ID */
	public int normalMapID;
	/** 是否可切换难度 */
	public int hardChange;
	/** 开启等级 */
	public int openLv;
	/** 是否允许加入机器人 */
	public int isRobot;
	/** 攻击 */
	public String prop1;
	/** 最小值1 */
	public int min1;
	/** 最大值1 */
	public int max1;
	/** 防御 */
	public String prop2;
	/** 最小值2 */
	public int min2;
	/** 最大值2 */
	public int max2;
	/** 生命 */
	public String prop3;
	/** 最小值3 */
	public int min3;
	/** 最大值3 */
	public int max3;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}