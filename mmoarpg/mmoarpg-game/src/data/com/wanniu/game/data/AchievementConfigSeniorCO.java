package com.wanniu.game.data; 

public class AchievementConfigSeniorCO { 

	/** 成就类型ID */
	public int typeId;
	/** 成就类型 */
	public String type;
	/** 完成解锁章节 */
	public int unlock;
	/** 是否默认解锁 */
	public int lock;

	/** 主键 */
	public int getKey() {
		return this.typeId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}