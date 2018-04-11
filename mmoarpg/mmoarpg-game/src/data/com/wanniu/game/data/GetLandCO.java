package com.wanniu.game.data; 

public class GetLandCO { 

	/** 编号 */
	public int landNum;
	/** 土地开启条件 */
	public int getType;
	/** 属性值 */
	public int value;

	/** 主键 */
	public int getKey() {
		return this.landNum; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}