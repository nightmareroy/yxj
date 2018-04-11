package com.wanniu.game.data; 

public class ActionCO { 

	/** 动作ID */
	public int iD;
	/** 表情动作 */
	public String actionName;
	/** 具体内容(无对象) */
	public String contentNull;
	/** 具体内容（对他人） */
	public String contentOther;
	/** 具体内容（对自己） */
	public String contentSelf;
	/** 世界有效 */
	public int worldValid;
	/** 公会有效 */
	public int guildValid;
	/** 私聊有效 */
	public int privateValid;
	/** 组队有效 */
	public int teamValid;
	/** 连服有效 */
	public int interServiceValid;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}