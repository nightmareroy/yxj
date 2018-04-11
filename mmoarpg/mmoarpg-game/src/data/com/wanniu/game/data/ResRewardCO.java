package com.wanniu.game.data; 

public class ResRewardCO { 

	/** 玩家等级 */
	public int lv;
	/** 极限挑战扫荡奖励（简单） */
	public String saodangReward11;
	/** 极限挑战扫荡奖励（困难） */
	public String saodangReward12;
	/** 极限挑战扫荡奖励（炼狱） */
	public String saodangReward13;
	/** 守护神宠扫荡奖励（简单） */
	public String saodangReward21;
	/** 守护神宠扫荡奖励（困难） */
	public String saodangReward22;
	/** 守护神宠扫荡奖励（炼狱） */
	public String saodangReward23;
	/** 幻妖农场扫荡奖励（简单） */
	public String saodangReward31;
	/** 幻妖农场扫荡奖励（困难） */
	public String saodangReward32;
	/** 幻妖农场扫荡奖励（炼狱） */
	public String saodangReward33;

	/** 主键 */
	public int getKey() {
		return this.lv; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}