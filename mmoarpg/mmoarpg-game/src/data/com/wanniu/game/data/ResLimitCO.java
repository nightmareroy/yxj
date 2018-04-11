package com.wanniu.game.data; 

public class ResLimitCO { 

	/** 人物等级 */
	public int lv;
	/** 经验上限 */
	public int expLimit;
	/** 修为上限 */
	public int culLimit;
	/** 银两上限 */
	public int goldLimit;

	/** 主键 */
	public int getKey() {
		return this.lv; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}