package com.wanniu.game.data; 

public class MasteryPropCO { 

	/** 编号ID */
	public int iD;
	/** 专精部位 */
	public int pos;
	/** 等级 */
	public int level;
	/** 属性名1 */
	public String prop;
	/** 属性值1 */
	public int value;
	/** 奖励魔界威望 */
	public int bonusRingPoint;
	/** 激活消耗虚拟道具 */
	public String costItem;
	/** 道具数量 */
	public int itemCount;
	/** 目标怪物ID */
	public int monsterID;
	/** 怪物强度 */
	public String monsterHard;
	/** 所在场景ID */
	public int mapID;
	/** 坐标 */
	public String coord;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}