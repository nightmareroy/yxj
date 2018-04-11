package com.wanniu.game.data; 

public class DungeonMapCostCO { 

	/** 副本ID */
	public int mapID;
	/** 玩法类型 */
	public int playType;
	/** 购买次数消耗元宝 */
	public int costDiamond;
	/** 购买是否需要VIP */
	public int reqVip;
	/** 是否可双倍领奖 */
	public int isDoubleBonus;
	/** 双倍领奖花费元宝 */
	public int bounsCostDiamond;
	/** 进入等级 */
	public int enterLevel;
	/** 奖励展示 */
	public String showReward;

	/** 主键 */
	public int getKey() {
		return this.mapID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}