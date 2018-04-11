package com.wanniu.game.data; 

public class AddRechargeLimitCO { 

	/** 排序 */
	public int iD;
	/** 累计连续充值天数 */
	public int addTime;
	/** 每日指定充值额度 */
	public int rechargeLimit;
	/** 每日充值奖励 */
	public String rechargeFReward;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}