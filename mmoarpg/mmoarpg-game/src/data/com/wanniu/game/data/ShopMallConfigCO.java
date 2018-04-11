package com.wanniu.game.data; 

public class ShopMallConfigCO { 

	/** 标签类型 */
	public int itemType;
	/** 标签名 */
	public String labelName;
	/** 消耗类型 */
	public int consumeType;
	/** 是否开放 */
	public int isOpened;
	/** 道具剩余数量显示文本 */
	public String remainNum;

	/** 主键 */
	public int getKey() {
		return this.itemType; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}