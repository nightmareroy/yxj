package com.wanniu.game.data; 

public class PayRewardCO { 

	/** 套餐ID */
	public int iD;
	/** 奖励类型 */
	public int payRewardType;
	/** 奖励描述 */
	public String payRewardDesc;
	/** 累充金额 */
	public int payTotalPay;
	/** 单笔充值金额 */
	public int singlePay;
	/** 奖励道具 */
	public String payReward;
	/** 刷新时间 */
	public String payRefreshDay;
	/** 邮件模板 */
	public int mailID;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}