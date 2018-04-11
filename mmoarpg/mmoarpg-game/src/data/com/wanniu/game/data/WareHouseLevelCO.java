package com.wanniu.game.data; 

public class WareHouseLevelCO { 

	/** 等级 */
	public int wareHouseLevel;
	/** 需要资金 */
	public int funds;
	/** 需要金币 */
	public int gold;
	/** 需要公会等级 */
	public int gLevel;
	/** 存储格子 */
	public int spece;

	/** 主键 */
	public int getKey() {
		return this.wareHouseLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}