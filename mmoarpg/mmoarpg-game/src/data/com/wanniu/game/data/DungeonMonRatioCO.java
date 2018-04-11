package com.wanniu.game.data; 

public class DungeonMonRatioCO { 

	/** 怪物类型 */
	public int mon_type;
	/** 普通难度怪物生命加成 */
	public float normal_hp_ratio;
	/** 普通难度怪物攻击加成 */
	public float normal_atk_ratio;
	/** 普通难度怪物防御加成 */
	public float normal_def_ratio;
	/** 普通难度怪物四维加成 */
	public float normal_dM_ratio;
	/** 困难难度怪物生命加成 */
	public float hard_hp_ratio;
	/** 困难难度怪物攻击加成 */
	public float hard_atk_ratio;
	/** 困难难度怪物防御加成 */
	public float hard_def_ratio;
	/** 困难难度怪物四维加成 */
	public float hard_dM_ratio;

	/** 主键 */
	public int getKey() {
		return this.mon_type; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}