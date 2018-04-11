package com.wanniu.game.data; 

public class TreasuresShowCO { 

	/** 编号 */
	public int id;
	/** 展示道具 */
	public String showItem;
	/** 数量 */
	public int showItemNum;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}