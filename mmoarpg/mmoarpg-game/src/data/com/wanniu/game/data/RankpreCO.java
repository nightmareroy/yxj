package com.wanniu.game.data; 

public class RankpreCO { 

	/** 编号ID */
	public int iD;
	/** 起始名次 */
	public int startRank;
	/** 结束名次 */
	public int stopRank;
	/** 奖励物品 */
	public String rankReward;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}