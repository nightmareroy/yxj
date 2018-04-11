package com.wanniu.game.data; 

public class ItemIdConfigCO { 

	/** 物品类型代码 */
	public String itemType;
	/** 对应编号 */
	public int typeID;
	/** 物品类型名称 */
	public String typeName;
	/** tips显示绑定 */
	public int showBind;
	/** 排序 */
	public int order;
	/** 次级排序规则 */
	public String orderRule;

	/** 主键 */
	public String getKey() {
		return this.itemType; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}