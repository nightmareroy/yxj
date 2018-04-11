package com.wanniu.game.data; 

public class DefMsgCO { 

	/** 编号 */
	public int id;
	/** 频道 */
	public int channelId;
	/** 消息内容 */
	public String content;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}