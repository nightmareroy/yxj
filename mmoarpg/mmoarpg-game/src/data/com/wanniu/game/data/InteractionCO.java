package com.wanniu.game.data; 

public class InteractionCO { 

	/** 编号 */
	public int id;
	/** 交互名称 */
	public String name;
	/** 交互类型 */
	public int type;
	/** 魅力值 */
	public int charm;
	/** 金币 */
	public int gold;
	/** 钻石 */
	public int diamond;
	/** 展示效果 */
	public String show;
	/** 消息 */
	public String message;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}