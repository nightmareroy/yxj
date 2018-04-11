package com.wanniu.game.data; 

public class UpLevelExpCO { 

	/** 进阶排序 */
	public int upOrder;
	/** 境界编号 */
	public int classID;
	/** 境界名称 */
	public String className;
	/** 是否开启 */
	public int isValid;
	/** 境界阶数 */
	public int classUPLevel;
	/** 阶数名称 */
	public String uPName;
	/** 成色 */
	public int qcolor;
	/** 提升境界所需人物等级 */
	public int reqLevel;
	/** 进阶需要完成事件 */
	public String reqEvents;
	/** 进阶所需修为 */
	public int reqClassExp;
	/** 属性1 */
	public String prop1;
	/** 参数1 */
	public int par1;
	/** 最小值1 */
	public int min1;
	/** 最大值1 */
	public int max1;
	/** 属性2 */
	public String prop2;
	/** 参数2 */
	public int par2;
	/** 最小值2 */
	public int min2;
	/** 最大值2 */
	public int max2;
	/** 属性3 */
	public String prop3;
	/** 参数3 */
	public int par3;
	/** 最小值3 */
	public int min3;
	/** 最大值3 */
	public int max3;
	/** 属性4 */
	public String prop4;
	/** 参数4 */
	public int par4;
	/** 最小值4 */
	public int min4;
	/** 最大值4 */
	public int max4;
	/** 属性5 */
	public String prop5;
	/** 参数5 */
	public int par5;
	/** 最小值5 */
	public int min5;
	/** 最大值5 */
	public int max5;

	/** 主键 */
	public int getKey() {
		return this.upOrder; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}