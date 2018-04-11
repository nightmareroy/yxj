package com.wanniu.game.data; 

public class SingleCzCO { 

	/** 编号 */
	public Object iD;
	/** 充值金额 */
	public Object sum;
	/** 奖励道具 */
	public String rewards;

	/** 主键 */
	public Object getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}