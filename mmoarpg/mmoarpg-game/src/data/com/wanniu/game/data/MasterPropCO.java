package com.wanniu.game.data; 

public class MasterPropCO { 

	/** 属性记录编号 */
	public int propID;
	/** 属性数量 */
	public int propCount;
	/** 属性1 */
	public String prop1;
	/** 参数1 */
	public int par1;
	/** 最小值1 */
	public int min1;
	/** 最大值1 */
	public int max1;
	/** 成长系数 */
	public float grow1;
	/** 属性2 */
	public String prop2;
	/** 参数2 */
	public int par2;
	/** 最小值2 */
	public int min2;
	/** 最大值2 */
	public int max2;
	/** 值成长 2 */
	public float grow2;
	/** 属性3 */
	public String prop3;
	/** 参数3 */
	public int par3;
	/** 最小值3 */
	public int min3;
	/** 最大值3 */
	public int max3;
	/** 值成长 3 */
	public float grow3;
	/** 属性4 */
	public String prop4;
	/** 参数4 */
	public int par4;
	/** 最小值4 */
	public int min4;
	/** 最大值4 */
	public int max4;
	/** 值成长4 */
	public float grow4;

	/** 主键 */
	public int getKey() {
		return this.propID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}