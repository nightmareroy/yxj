package com.wanniu.game.data; 

public class FashSuitConfigCO { 

	/** 专属职业 */
	public int pro;
	/** 套装名称 */
	public String name;
	/** 套装编号 */
	public int suitID;
	/** 两件激活属性 */
	public String attr2;
	/** 三件激活属性 */
	public String attr3;

	/** 主键 */
	public int getKey() {
		return this.pro; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}