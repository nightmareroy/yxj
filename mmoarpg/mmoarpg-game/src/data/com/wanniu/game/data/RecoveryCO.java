package com.wanniu.game.data; 

public class RecoveryCO { 

	/** 编号 */
	public int iD;
	/** 找回类型 */
	public int type;
	/** 名称 */
	public String name;
	/** 等级区间 */
	public String level;
	/** 资源1 */
	public String item1code;
	/** 数量1 */
	public int num1;
	/** 资源2 */
	public String item2code;
	/** 数量2 */
	public int num2;
	/** 资源3 */
	public String item3code;
	/** 数量3 */
	public int num3;
	/** 资源4 */
	public String item4code;
	/** 数量4 */
	public int num4;
	/** 完美找回花费 */
	public int cost;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}