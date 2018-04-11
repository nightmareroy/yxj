package com.wanniu.game.data; 

public class BlesslibaoCO { 

	/** 等级 */
	public int blessLevel;
	/** 宝箱奖励条目数 */
	public int blessBuffNum;
	/** 30%礼包奖励 */
	public String blessAward30;
	/** 60%礼包奖励 */
	public String blessAward60;
	/** 100%礼包奖励 */
	public String blessAward100;

	/** 主键 */
	public int getKey() {
		return this.blessLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}