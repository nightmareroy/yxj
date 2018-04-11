package com.wanniu.game.data; 

public class MagicRingCO { 

	/** 编号ID */
	public int iD;
	/** 职业 */
	public int pro;
	/** 需要人物等级 */
	public int reqLevel;
	/** 需要人物阶级 */
	public int upOrder;
	/** 消耗魔界威望 */
	public int ringPoint;
	/** 戒指代码 */
	public String ringCode;
	/** 需要穿戴戒指代码 */
	public String preRingCode;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}