package com.wanniu.game.data; 

public class RankListCO { 

	/** 称号排序 */
	public int rankOrder;
	/** 称号ID */
	public int rankID;
	/** 称号名称 */
	public String rankName;
	/** 称号品质 */
	public int rankQColor;
	/** 是否显示 */
	public int ifAppear;
	/** 称号类型ID */
	public int sortID;
	/** 获得条件 */
	public String tips;
	/** 有效期 */
	public int validTime;
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
	/** 所属系统 */
	public int belongTo;
	/** 达成条件 */
	public String rankCondition;
	/** 条件参数 */
	public String conPara;
	/** 获得道具 */
	public String item;
	/** 显示方式 */
	public String show;

	/** 主键 */
	public int getKey() {
		return this.rankOrder; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}