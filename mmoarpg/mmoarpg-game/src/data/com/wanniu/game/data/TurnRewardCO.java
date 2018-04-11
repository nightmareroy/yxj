package com.wanniu.game.data; 

public class TurnRewardCO { 

	/** 排序 */
	public int sort;
	/** 获得奖励 */
	public String getReward;
	/** 是否绑定 */
	public int isBind;
	/** 是否可卖给商店 */
	public int saleShop;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}