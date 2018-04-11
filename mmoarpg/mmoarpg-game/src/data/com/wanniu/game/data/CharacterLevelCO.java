package com.wanniu.game.data; 

public class CharacterLevelCO { 

	/** 等级 */
	public int level;
	/** 升级所需经验 */
	public int experience;

	/** 主键 */
	public int getKey() {
		return this.level; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}