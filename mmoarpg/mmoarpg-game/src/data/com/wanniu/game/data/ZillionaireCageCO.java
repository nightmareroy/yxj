package com.wanniu.game.data; 

public class ZillionaireCageCO { 

	/** 排序 */
	public int sort;
	/** 道具ID */
	public String itemCode;
	/** 道具名称 */
	public String name;
	/** 获得数量 */
	public int nUM;
	/** 获得后是否绑定 */
	public int isBind;
	/** 是否可卖给商店 */
	public int saleShop;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}