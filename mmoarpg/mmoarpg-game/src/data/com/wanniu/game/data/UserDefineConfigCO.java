package com.wanniu.game.data; 

public class UserDefineConfigCO { 

	/** 怪物类型 */
	public int mon_type;
	/** 防御比 */
	public float def_ratio;
	/** 命中比 */
	public float hit_ratio;
	/** 闪避比 */
	public float dodge_ratio;
	/** 暴击比 */
	public float crit_ratio;
	/** 抗暴比 */
	public float resCrit_ratio;

	/** 主键 */
	public int getKey() {
		return this.mon_type; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}