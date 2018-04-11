package com.wanniu.game.data; 

public class RechargeCostCO { 

	/** 购买次数 */
	public int rechargeTimes;
	/** 货币数量 */
	public int costNum;

	/** 主键 */
	public int getKey() {
		return this.rechargeTimes; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}