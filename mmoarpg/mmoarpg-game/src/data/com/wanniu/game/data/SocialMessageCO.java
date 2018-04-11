package com.wanniu.game.data; 

public class SocialMessageCO { 

	/** ID */
	public int iD;
	/** 消息类型 */
	public int messageType;
	/** 消息有效期 */
	public int messageTime;
	/** HUD显示时长 */
	public int hUDTime;
	/** 发送上限 */
	public int sendLimite;
	/** 保存上限 */
	public int receiveLimite;
	/** 提示ICON */
	public String hudICON;
	/** 消息内容 */
	public String messageText;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}