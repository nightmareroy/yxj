package com.wanniu.game.data; 

public class PlantingCO { 

	/** 编号 */
	public int iD;
	/** 名称 */
	public String name;
	/** 代码 */
	public String code;
	/** 种植等级 */
	public int plantLevel;
	/** 产量 */
	public int harvest;
	/** 种植经验 */
	public int getExp;
	/** 生长时间 */
	public int growTime;
	/** 产物 */
	public String product;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}