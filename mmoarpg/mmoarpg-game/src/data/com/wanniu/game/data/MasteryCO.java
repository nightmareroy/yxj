package com.wanniu.game.data; 

public class MasteryCO { 

	/** 专精部位 */
	public int pos;
	/** 专精名称 */
	public String name;
	/** 图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.pos; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}