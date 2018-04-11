package com.wanniu.game.data; 

public class FirstPayCO { 

	/** 奖励ID */
	public int iD;
	/** 职业 */
	public String job;
	/** 武器代码 */
	public String weaponCode;
	/** 强化等级 */
	public int enchantLv;
	/** 武器动画 */
	public String weaponAssetBuddles;
	/** 武器动画倍数 */
	public float assetBuddlesPercent;
	/** 武器动画高度 */
	public float assetBuddlesY;
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

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}