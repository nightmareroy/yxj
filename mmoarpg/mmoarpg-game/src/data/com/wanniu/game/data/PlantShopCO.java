package com.wanniu.game.data; 

public class PlantShopCO { 

	/** 编号 */
	public int iD;
	/** 兑换次数 */
	public int changeNum;
	/** 兑换参数 */
	public String parameter;
	/** 奖励道具 */
	public String itemCode;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}