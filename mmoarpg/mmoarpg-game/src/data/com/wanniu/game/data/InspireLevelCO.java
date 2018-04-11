package com.wanniu.game.data; 

public class InspireLevelCO { 

	/** 序号 */
	public int iD;
	/** 鼓舞类型 */
	public int inspireType;
	/** 鼓舞次数 */
	public int inspireNum;
	/** 鼓舞加成 */
	public int inspirePlus;
	/** 鼓舞消耗 */
	public int inspireCost;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}