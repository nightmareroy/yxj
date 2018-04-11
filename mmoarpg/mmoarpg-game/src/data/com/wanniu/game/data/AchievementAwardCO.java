package com.wanniu.game.data; 

public class AchievementAwardCO { 

	/** ID */
	public int id;
	/** 成就类型ID */
	public int typeId;
	/** 职业ID */
	public String pro;
	/** 需求成就点数 */
	public int ponitCondition;
	/** 领取等级 */
	public int levelCondition;
	/** 需要人物阶级 */
	public int upOrder;
	/** 奖励类型1 */
	public int awardType;
	/** 奖励key1 */
	public String awardKey1;
	/** 奖励值1 */
	public int awardValue1;
	/** 奖励key2 */
	public String awardKey2;
	/** 奖励值2 */
	public int awardValue2;
	/** 奖励key3 */
	public String awardKey3;
	/** 奖励值3 */
	public int awardValue3;
	/** 奖励key4 */
	public String awardKey4;
	/** 奖励值4 */
	public int awardValue4;
	/** 奖励key5 */
	public String awardKey5;
	/** 奖励值5 */
	public int awardValue5;
	/** 消耗金币 */
	public int goldCost;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}