package com.wanniu.game.data; 

public class GuildBossRatioCO { 

	/** 怪物等级 */
	public int gBoss_lv;
	/** 怪物生命加成 */
	public float gBoss_hp_ratio;
	/** 怪物攻击加成 */
	public float gBoss_atk_ratio;
	/** 怪物防御加成 */
	public float gBoss_def_ratio;
	/** 怪物命中加成 */
	public float gBoss_hit_ratio;
	/** 怪物闪避加成 */
	public float gBoss_dodge_ratio;
	/** 怪物暴击加成 */
	public float gBoss_crit_ratio;
	/** 怪物抗暴加成 */
	public float gBoss_resCrit_ratio;

	/** 主键 */
	public int getKey() {
		return this.gBoss_lv; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}