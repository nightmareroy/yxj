package com.wanniu.game.data; 

public class SuitListCO { 

	/** 套装编号 */
	public int suitID;
	/** 套装等级 */
	public int level;
	/** 职业限制 */
	public String pro;
	/** 套装名称 */
	public String suitName;
	/** 部件数量 */
	public int partCount;
	/** 是否有效 */
	public int isValid;
	/** 套装描述 */
	public String suitDesc;
	/** 部件物品代码 */
	public String partCodeList;

	/** 主键 */
	public int getKey() {
		return this.suitID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}