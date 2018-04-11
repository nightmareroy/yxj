package com.wanniu.game.data; 

public class MonsterSkillValueCO { 

	/** 技能ID */
	public int skillID;
	/** 技能名称 */
	public String skillName;
	/** 技能描述 */
	public String skillDesc;
	/** 技能类型 */
	public int skillType;
	/** 最高等级 */
	public int maxLevel;
	/** 仇恨系数 */
	public int hateRate;
	/** 基础仇恨 */
	public int baseHateValue;
	/** 动作时间 */
	public int actTime;
	/** 预间隔时间 */
	public int preCDTime;
	/** 冷却时间 */
	public int cDTime;
	/** 消耗法力万分比 */
	public int costManaPer;
	/** 等级消耗法力值序列 */
	public String costManaSet;
	/** 伤害类型 */
	public String dmgType;
	/** 伤害倍数($1) */
	public int dmgRate;
	/** 伤害倍数增加/等级 */
	public int dmgRatePerLvl;
	/** 额外伤害类型 */
	public String exdDmgType;
	/** 额外伤害序列($2) */
	public String exdDmgSet;
	/** 魔法属性 */
	public String prop;
	/** 属性编号 */
	public int propID;
	/** 触发几率($3) */
	public int chance;
	/** 持续时长($4) */
	public String buffTime;
	/** 属性名1 */
	public String valueAttributeName1;
	/** 属性值1含义 */
	public int valueAttribute1;
	/** 魔法属性值序列($5) */
	public String valueSet;
	/** 属性名2 */
	public String valueAttributeName2;
	/** 属性值2含义 */
	public int valueAttribute2;
	/** 魔法属性值序列2($6) */
	public String valueSet2;
	/** 属性名3 */
	public String valueAttributeName3;
	/** 属性值3含义 */
	public int valueAttribute3;
	/** 魔法属性值序列3($7) */
	public String valueSet3;
	/** 是否参与自动战斗 */
	public int canAuto;
	/** 是否可以被反弹 */
	public int canIronMaiden;

	/** 主键 */
	public int getKey() {
		return this.skillID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}