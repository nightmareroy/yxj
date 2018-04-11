package com.wanniu.game.data; 

public class SeedTimeCO { 

	/** 编号 */
	public int iD;
	/** 种子品质 */
	public int qcolor;
	/** 种植时间 */
	public int time;
	/** 减少时间 */
	public int reduceTime;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}