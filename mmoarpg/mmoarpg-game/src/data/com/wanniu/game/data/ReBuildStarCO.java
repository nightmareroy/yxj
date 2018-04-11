package com.wanniu.game.data; 

public class ReBuildStarCO { 

	/** 编号 */
	public int iD;
	/** 装备部位 */
	public String type;
	/** 同类型属性数量 */
	public int enClass;
	/** 奖励属性 */
	public String prop;
	/** 属性值 */
	public int proNum;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}