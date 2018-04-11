package com.wanniu.game.data; 

public class ShowMsgCO { 

	/** 编号 */
	public int id;
	/** 消息内容 */
	public String content;
	/** 需要VIP */
	public int vipLv;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}