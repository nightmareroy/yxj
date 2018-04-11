package com.wanniu.game.data; 

public class FunctionsCO { 

	/** 功能ID */
	public String funID;
	/** 功能索引 */
	public int funIndex;
	/** 功能名称 */
	public String funDes;
	/** 显示条件 */
	public int condition;
	/** 条件值 */
	public int value;
	/** 功能界面ID */
	public String funUIID;
	/** 提示文字 */
	public String tips;
	/** 传送ID */
	public int toLocation;
	/** 出售商品索引 */
	public String sellIndex;
	/** 兑换物品索引 */
	public String exchangeIndex;
	/** 组队消息ID */
	public int msgID;
	/** 剧情ID */
	public int storyID;
	/** 帮助信息ID */
	public int helpID;
	/** 功能图标 */
	public String funIcon;
	/** 小图标 */
	public String smallIcon;

	/** 主键 */
	public String getKey() {
		return this.funID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}