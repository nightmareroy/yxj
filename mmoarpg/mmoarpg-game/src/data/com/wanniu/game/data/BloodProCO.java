package com.wanniu.game.data; 

public class BloodProCO { 

	/** 编号 */
	public int iD;
	/** 血魂类型 */
	public int bloodType;
	/** 品质参数 */
	public String quaType;
	/** 是否有效 */
	public int isValid;
	/** 属性稀有度 */
	public int rare;
	/** 属性 */
	public String prop4;
	/** 参数 */
	public int par4;
	/** 值 */
	public int num4;
	/** 属性 */
	public String prop3;
	/** 参数 */
	public int par3;
	/** 值 */
	public int num3;
	/** 属性 */
	public String prop2;
	/** 参数 */
	public int par2;
	/** 值 */
	public int num2;
	/** 属性 */
	public String prop1;
	/** 参数 */
	public int par1;
	/** 值 */
	public int num1;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}