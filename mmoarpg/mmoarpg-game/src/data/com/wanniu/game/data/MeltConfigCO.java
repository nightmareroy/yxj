package com.wanniu.game.data; 

public class MeltConfigCO { 

	/** 编号 */
	public int iD;
	/** 装备熔炼等级 */
	public int meltLevel;
	/** 装备品质 */
	public int equipQColor;
	/** 金币消耗 */
	public int costGold;
	/** 熔炼产出 */
	public String tcCode;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}