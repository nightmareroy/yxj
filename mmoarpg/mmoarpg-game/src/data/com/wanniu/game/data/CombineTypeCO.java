package com.wanniu.game.data; 

public class CombineTypeCO { 

	/** 编号ID */
	public int iD;
	/** 类型名称 */
	public String itemName;
	/** 父类ID */
	public int parentID;
	/** 数据类型 */
	public int dataType;
	/** 合成物代码 */
	public String tagetCode;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}