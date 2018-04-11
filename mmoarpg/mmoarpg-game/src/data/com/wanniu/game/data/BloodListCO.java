package com.wanniu.game.data; 

public class BloodListCO { 

	/** 血脉ID */
	public int bloodID;
	/** 血脉CODE */
	public String code;
	/** 血脉名称 */
	public String bloodName;
	/** 血脉品质 */
	public int bloodQColor;
	/** 品质名称 */
	public String bloodQColorName;
	/** 血脉类型ID */
	public int sortID1;
	/** 血脉类型名称 */
	public String sortID2;
	/** 血脉格子ID */
	public int sortID3;
	/** 血脉提供经验 */
	public int exp;
	/** 描述 */
	public String desc;
	/** 随机属性数量 */
	public int proNum;
	/** 属性1 */
	public int prop1;
	/** 参数1 */
	public int par1;
	/** 值1 */
	public int num1;
	/** 属性2 */
	public int prop2;
	/** 参数2 */
	public int par2;
	/** 值2 */
	public int num2;
	/** 属性3 */
	public int prop3;
	/** 参数3 */
	public int par3;
	/** 值3 */
	public int num3;
	/** 属性4 */
	public int prop4;
	/** 参数4 */
	public int par4;
	/** 值4 */
	public int num4;
	/** 评分 */
	public int bScore;
	/** 熔炼TC */
	public String melting;

	/** 主键 */
	public int getKey() {
		return this.bloodID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}