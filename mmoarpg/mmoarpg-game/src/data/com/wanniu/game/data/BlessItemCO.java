package com.wanniu.game.data; 

public class BlessItemCO { 

	/** ID */
	public int iD;
	/** 祈福等级 */
	public int blessLevel;
	/** 物品ID */
	public String itemID;
	/** 物品名 */
	public String name;
	/** 最小值 */
	public int minNeed;
	/** 最大值 */
	public int maxNeed;
	/** 是否显示 */
	public int isShow;
	/** 权值 */
	public int pro;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}