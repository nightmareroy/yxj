package com.wanniu.game.data; 

public class CharacterCO { 

	/** 编号 */
	public int iD;
	/** 职业名称 */
	public String proName;
	/** 职业ID */
	public int pro;
	/** 性别 */
	public int sex;
	/** 初始等级 */
	public int initLevel;
	/** 天赋属性1 */
	public String giftProp1;
	/** 天赋属性值1 */
	public int giftValue1;
	/** 天赋属性2 */
	public String giftProp2;
	/** 天赋属性值2 */
	public int giftValue2;
	/** 天赋属性3 */
	public String giftProp3;
	/** 天赋属性值3 */
	public int giftValue3;
	/** 初始生命 */
	public int initHP;
	/** 生命成长系数 */
	public float hPGrowUp;
	/** 基础物攻 */
	public int basePhyDamage;
	/** 物攻成长系数 */
	public float phyGrowUp;
	/** 基础魔攻 */
	public int baseMagDamage;
	/** 魔攻成长系数 */
	public float magGrowUp;
	/** 初始命中 */
	public int initHit;
	/** 命中成长系数 */
	public float hitGrowUP;
	/** 初始闪避 */
	public int initDodge;
	/** 闪避成长系数 */
	public float dodgeGrowUP;
	/** 初始暴击 */
	public int initCrit;
	/** 暴击成长系数 */
	public float critGrowUP;
	/** 初始抗暴 */
	public int initResCrit;
	/** 抗暴成长系数 */
	public float resCritGrowUP;
	/** 初始物防 */
	public int initAc;
	/** 物防成长系数 */
	public float acGrowUp;
	/** 初始魔防 */
	public int initResist;
	/** 魔防/等级 */
	public float resistGrowUp;
	/** 初始暴击伤害 */
	public int critDamage;
	/** 初始生命恢复 */
	public int baseHPRegen;
	/** 初始治疗效果 */
	public int healEffect;
	/** 初始被治疗效果 */
	public int healedEffect;
	/** 初始模型 */
	public String model;
	/** 初始武器模型 */
	public String weaponmodel;
	/** 初始装备 */
	public String initEquip;
	/** 初始携带道具 */
	public String initItem;
	/** 初始技能列表 */
	public String initSkill;
	/** 总数 */
	public String count;
	/** 音效 */
	public String sound;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}