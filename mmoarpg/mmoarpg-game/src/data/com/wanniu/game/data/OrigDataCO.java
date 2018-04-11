package com.wanniu.game.data; 

public class OrigDataCO { 

	/** 等级 */
	public int level;
	/** 生命 */
	public float hp;
	/** 攻击 */
	public float atk;
	/** 防御 */
	public float def;
	/** 命中 */
	public float hit;
	/** 闪避 */
	public float dodge;
	/** 暴击 */
	public float critical;
	/** 抗暴 */
	public float resCrit;

	/** 主键 */
	public int getKey() {
		return this.level; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}