package com.wanniu.game.data; 

public class FashionCO { 

	/** 名称 */
	public String name;
	/** 专属职业 */
	public int pro;
	/** 类型 */
	public int type;
	/** 代码 */
	public String code;
	/** 套装编号 */
	public int fashionID;
	/** 成色 */
	public int qcolor;
	/** 等级需求 */
	public int levelReq;
	/** 模型文件 */
	public String avatarId;
	/** 基础属性1 */
	public String prop1;
	/** 值1 */
	public int num1;
	/** 基础属性2 */
	public String prop2;
	/** 值2 */
	public int num2;
	/** 基础属性3 */
	public String prop3;
	/** 值3 */
	public int num3;
	/** 基础属性4 */
	public String prop4;
	/** 值4 */
	public int num4;

	/** 主键 */
	public String getKey() {
		return this.name; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}