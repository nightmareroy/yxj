package com.wanniu.game.data; 

public class DailyPayCO { 

	/** 奖励ID */
	public int iD;
	/** 第几天 */
	public int day;
	/** 职业 */
	public String job;
	/** 充值目标 */
	public int target;
	/** 奖励物品1 */
	public String rewardCode1;
	/** 物品数量1 */
	public int rewardNum1;
	/** 奖励物品2 */
	public String rewardCode2;
	/** 物品数量2 */
	public int rewardNum2;
	/** 奖励物品3 */
	public String rewardCode3;
	/** 物品数量3 */
	public int rewardNum3;
	/** 奖励物品4 */
	public String rewardCode4;
	/** 物品数量4 */
	public int rewardNum4;
	/** 宣传图片 */
	public String picture;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}