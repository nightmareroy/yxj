package com.wanniu.game.data; 

public class SoloTimesCostCO { 

	/** 报名参战的购买次数 */
	public int times;
	/** 钻石消耗 */
	public int costDiamond;

	/** 主键 */
	public int getKey() {
		return this.times; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}