package com.wanniu.game.data; 

public class TreasuresItemCO { 

	/** 编号 */
	public int id;
	/** 分组编号 */
	public int groupID;
	/** 道具名称 */
	public String itemName;
	/** 道具 */
	public String item;
	/** 数量 */
	public int itemNum;
	/** 是否有效 */
	public int isValid;
	/** 职业 */
	public String profession;
	/** 权重 */
	public int prob;
	/** 购买后是否绑定 */
	public int isBind;
	/** 是否广播 */
	public int isShow;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}