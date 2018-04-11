package com.wanniu.game.data; 

public class TowerMonRatioCO { 

	/** 塔层数 */
	public int tower_floor;
	/** 怪物生命加成 */
	public float towMon_hp_ratio;
	/** 怪物攻击加成 */
	public float towMon_atk_ratio;
	/** 怪物防御加成 */
	public float towMon_def_ratio;
	/** 怪物命中加成 */
	public float towMon_hit_ratio;
	/** 怪物闪避加成 */
	public float towMon_dodge_ratio;
	/** 怪物暴击加成 */
	public float towMon_crit_ratio;
	/** 怪物抗暴加成 */
	public float towMon_resCrit_ratio;
	/** 怪物等级 */
	public int towMon_lv;

	/** 主键 */
	public int getKey() {
		return this.tower_floor; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}