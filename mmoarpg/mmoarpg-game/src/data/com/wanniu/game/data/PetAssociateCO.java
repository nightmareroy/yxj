package com.wanniu.game.data; 

public class PetAssociateCO { 

	/** 编号 */
	public int iD;
	/** 需称宠物名称及其阶级 */
	public String petID;
	/** 加成属性 */
	public String addPro;
	/** 序号 */
	public int order;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}