package com.wanniu.game.data; 

public class SuitConfigCO { 

	/** 编号 */
	public int iD;
	/** 套装编号 */
	public int suitID;
	/** 激活属性需要套装数量 */
	public int partReqCount;
	/** 属性 */
	public String prop;
	/** 参数 */
	public int par;
	/** 最小值 */
	public int min;
	/** 最大值 */
	public int max;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}