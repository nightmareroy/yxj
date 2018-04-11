package com.wanniu.game.data; 

public class PpresentCO { 

	/** 物品编号 */
	public int iD;
	/** 个人积分 */
	public int pERSONAL;
	/** 奖励物品 */
	public String rankReward;
	/** 对应图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}