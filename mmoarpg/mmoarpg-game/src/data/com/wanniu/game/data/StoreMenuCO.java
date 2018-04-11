package com.wanniu.game.data; 

public class StoreMenuCO { 

	/** ID */
	public int menuID;
	/** 列表分类 */
	public String menuName;
	/** 上级ID */
	public int parentsID;
	/** 物品类别代码 */
	public String menuCode;
	/** 是否有效 */
	public int isShow;

	/** 主键 */
	public int getKey() {
		return this.menuID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}