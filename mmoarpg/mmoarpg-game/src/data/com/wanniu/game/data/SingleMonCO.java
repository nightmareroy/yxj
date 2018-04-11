package com.wanniu.game.data; 

public class SingleMonCO { 

	/** 怪物等级 */
	public int mon_level;
	/** 怪物类型 */
	public int mon_type;
	/** 生命 */
	public int maxHP;
	/** 攻击 */
	public int attack;
	/** 防御 */
	public int def;
	/** 命中 */
	public int hit;
	/** 闪避 */
	public int dodge;
	/** 暴击 */
	public int crit;
	/** 抗暴 */
	public int resCirt;

	/** 主键 */
	public int getKey() {
		return this.mon_level; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}