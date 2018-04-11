package com.wanniu.game.data; 

public class SeedCO { 

	/** 编号 */
	public int iD;
	/** 名称 */
	public String name;
	/** 种子品质 */
	public int qcolor;
	/** 代码 */
	public String code;
	/** 银两价格 */
	public int price1;
	/** 元宝价格 */
	public int price2;
	/** 产出物品 */
	public String items;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}