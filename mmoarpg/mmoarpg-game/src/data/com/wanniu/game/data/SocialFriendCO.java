package com.wanniu.game.data; 

public class SocialFriendCO { 

	/** ID */
	public int iD;
	/** 好友行为 */
	public int mSocialAction;
	/** 好友度增加 */
	public int favorNum;
	/** 友情点数增加 */
	public int friendshipNum;
	/** 追杀值 */
	public int killNum;
	/** 消息反馈 */
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