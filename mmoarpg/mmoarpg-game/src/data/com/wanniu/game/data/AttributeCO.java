package com.wanniu.game.data; 

public class AttributeCO { 

	/** 属性编号 */
	public int iD;
	/** 名称 */
	public String attName;
	/** 英文简称 */
	public String attKey;
	/** 描述 */
	public String attDesc;
	/** 属性参数数量 */
	public int attParamCount;
	/** 是否百分比格式化 */
	public int isFormat;
	/** Par是否百分比格式化 */
	public int pFormat;
	/** 评分参数 */
	public float scoreRatio;
	/** 是否影响人物战力 */
	public int isEffect;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}