package com.wanniu.game.data; 

public class SkillSlotCO { 

	/** 技能槽 */
	public int indexID;
	/** 开启需要人物等级序列 */
	public int level;

	/** 主键 */
	public int getKey() {
		return this.indexID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}