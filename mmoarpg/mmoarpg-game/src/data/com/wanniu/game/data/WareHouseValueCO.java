package com.wanniu.game.data; 

public class WareHouseValueCO { 

	/** 序号 */
	public int order;
	/** 等级 */
	public int equipLv;
	/** 进阶 */
	public int equipUp;
	/** 品质 */
	public int equipColor;
	/** 公会存入资金 */
	public int wareHouseValue;
	/** 公会取出资金 */
	public int wareHouseCost;

	/** 主键 */
	public int getKey() {
		return this.order; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}