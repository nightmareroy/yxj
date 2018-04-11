package com.wanniu.game.data; 

public class BloodSuitConfigCO { 

	/** 编号 */
	public int iD;
	/** 套装编号 */
	public int suitID;
	/** 激活属性需要套装数量 */
	public int partReqCount;
	/** 属性 */
	public int prop;
	/** 参数 */
	public int par;
	/** 值 */
	public int num;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}