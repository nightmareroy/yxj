package com.wanniu.game.data; 

public class DailyRewardCO { 

	/** 玩家等级 */
	public int lv;
	/** 经验奖励 */
	public int exp;
	/** 修为奖励 */
	public int cul;
	/** 银两奖励 */
	public int gold;

	/** 主键 */
	public int getKey() {
		return this.lv; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}