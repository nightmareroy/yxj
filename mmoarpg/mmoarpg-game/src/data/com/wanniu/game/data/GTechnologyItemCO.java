package com.wanniu.game.data; 

public class GTechnologyItemCO { 

	/** ID */
	public int iD;
	/** 物品ID */
	public String itemID;
	/** 物品名 */
	public String name;
	/** 数量 */
	public int count;
	/** 购买后是否绑定 */
	public int isBind;
	/** 权值 */
	public int pro;
	/** 需要公会贡献 */
	public int pointsPrice;
	/** 需要等级 */
	public int needLevel;
	/** 需要职位 */
	public int needPosition;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}