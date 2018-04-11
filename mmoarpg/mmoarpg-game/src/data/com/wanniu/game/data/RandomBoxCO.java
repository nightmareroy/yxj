package com.wanniu.game.data; 

public class RandomBoxCO { 

	/** 宝箱ID */
	public int iD;
	/** 宝箱名称 */
	public String name;
	/** 宝箱数量 */
	public int quantity;
	/** 常规TC */
	public String tc;
	/** 队伍模式TC */
	public String teamTc;
	/** 个人TC */
	public String personTc;
	/** 经验万分比 */
	public int expRatio;
	/** 修为万分比 */
	public int upExpRatio;
	/** 金币奖励 */
	public int goldPerMonLv;
	/** 刷新场景 */
	public int startScene;
	/** 刷新点 */
	public String startPoint;
	/** 刷新间隔 */
	public int refreshTime;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}