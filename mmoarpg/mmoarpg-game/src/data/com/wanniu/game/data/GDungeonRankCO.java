package com.wanniu.game.data; 

public class GDungeonRankCO { 

	/** 排行ID */
	public int rankID;
	/** 排行类型 */
	public int rankType;
	/** 名次 */
	public int openTime;
	/** 积分奖励 */
	public int gpoints;

	/** 主键 */
	public int getKey() {
		return this.rankID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}