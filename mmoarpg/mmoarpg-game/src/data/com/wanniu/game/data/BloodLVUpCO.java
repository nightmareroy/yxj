package com.wanniu.game.data; 

public class BloodLVUpCO { 

	/** 等级 */
	public int level;
	/** 升级所需经验 */
	public int experience4;
	/** 升级所需经验 */
	public int experience3;
	/** 升级所需经验 */
	public int experience2;
	/** 升级所需经验 */
	public int experience1;

	/** 主键 */
	public int getKey() {
		return this.level; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}