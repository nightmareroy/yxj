package com.wanniu.game.data; 

public class PseudoRandomCO { 

	/** 伪随机组 */
	public int groupID;
	/** 初始几率 */
	public int initial;
	/** 伪随机每次增加 */
	public int ramAdd;
	/** 伪随机后减少 */
	public int ramRed;

	/** 主键 */
	public int getKey() {
		return this.groupID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}