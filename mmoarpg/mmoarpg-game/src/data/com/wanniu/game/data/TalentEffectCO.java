package com.wanniu.game.data; 

public class TalentEffectCO { 

	/** 天赋项ID */
	public int talentID;
	/** 天赋名称 */
	public String talentName;
	/** 天赋描述 */
	public String talentDesc;
	/** 所属天赋组 */
	public int talentGroup;
	/** 开启等级 */
	public int openLevel;
	/** 所处层数 */
	public int floor;
	/** 所处位置 */
	public int location;
	/** 天赋类型 */
	public int talentType;
	/** 新增技能 */
	public int plusSkill;
	/** 被替换的技能 */
	public int beforeSkill;
	/** 替换的技能 */
	public String replaceSkill;
	/** 最高等级 */
	public int maxLevel;
	/** 每升级消耗点数 */
	public int usePoint;
	/** 作用目标 */
	public int target;
	/** 天赋图标 */
	public String talentIcon;
	/** 仇恨系数 */
	public int hateRate;
	/** 基础仇恨 */
	public String baseHateValue;
	/** 冷却时间 */
	public int cDTime;
	/** 最小冷却 */
	public int minCDTime;
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
	/** 触发条件 */
	public int triggerCondition;
	/** 被动触发参数1 */
	public int passiveProp1;
	/** 被动触发参数2 */
	public int passiveProp2;
	/** 魔法属性 */
	public String prop;
	/** 触发几率($3) */
	public int chance;
	/** 持续时长($4) */
	public int buffTime;
	/** 魔法属性参数1 */
	public int magValueSet1;
	/** 魔法属性参数2 */
	public int magValueSet2;
	/** 属性名1 */
	public String valueAttributeName1;
	/** 魔法属性值序列($5) */
	public String valueSet;
	/** 属性名2 */
	public String valueAttributeName2;
	/** 魔法属性值序列2($6) */
	public String valueSet2;
	/** 属性名3 */
	public String valueAttributeName3;
	/** 魔法属性值序列3($7) */
	public String valueSet3;
	/** 每等级提供战斗力 */
	public String power;

	/** 主键 */
	public int getKey() {
		return this.talentID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}