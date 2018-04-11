package com.wanniu.game.data; 

public class RankSortCO { 

	/** 类型排序 */
	public int sortOrder;
	/** 类型ID */
	public int sortID;
	/** 类型 */
	public String sort;

	/** 主键 */
	public int getKey() {
		return this.sortOrder; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}