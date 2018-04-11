package com.wanniu.game.data; 

public class WingsListCO { 

	/** 翅膀ID */
	public int wingsID;
	/** 翅膀名称 */
	public String wingsName;
	/** 翅膀品质 */
	public int wingsQuality;
	/** 默认品阶 */
	public int wingsLevel;
	/** 初始星级 */
	public int defaultStar;
	/** 下一阶翅膀ID */
	public int nextRideLevel;
	/** 升星消耗材料Code */
	public String upStarItemCode;
	/** 每星级所需经验 */
	public int upStarMaxExp;
	/** 翅膀图标 */
	public String icon;
	/** 模型资源文件 */
	public String modelFile;
	/** 进阶属性 */
	public String prop;
	/** 参数 */
	public int par;
	/** 最小值 */
	public int min;
	/** 最大值 */
	public int max;
	/** 基础属性1 */
	public String prop1;
	/** 参数1 */
	public int par1;
	/** 最小值1 */
	public int min1;
	/** 最大值1 */
	public int max1;
	/** 基础属性2 */
	public String prop2;
	/** 参数2 */
	public int par2;
	/** 最小值2 */
	public int min2;
	/** 最大值2 */
	public int max2;
	/** 基础属性3 */
	public String prop3;
	/** 参数3 */
	public int par3;
	/** 最小值3 */
	public int min3;
	/** 最大值3 */
	public int max3;
	/** 基础属性4 */
	public String prop4;
	/** 参数4 */
	public int par4;
	/** 最小值4 */
	public int min4;
	/** 最大值4 */
	public int max4;

	/** 主键 */
	public int getKey() {
		return this.wingsID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}