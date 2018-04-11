package com.wanniu.game.data; 

public class GuildConditionCO { 

	/** 等级ID */
	public int levelID;
	/** 条件 */
	public String condition;
	/** 等级 */
	public int roleLevel;
	/** 进阶 */
	public int upLevel;
	/** 成色 */
	public int qcolor;

	/** 主键 */
	public int getKey() {
		return this.levelID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}