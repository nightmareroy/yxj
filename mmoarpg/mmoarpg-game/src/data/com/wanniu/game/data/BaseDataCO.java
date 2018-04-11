package com.wanniu.game.data; 

public class BaseDataCO { 

	/** 宠物编号 */
	public int petID;
	/** 宠物名称  */
	public String petName;
	/** 宠物称号 */
	public String petRank;
	/** 宠物成色 */
	public int qcolor;
	/** 品质名称 */
	public String type;
	/** 激活所需道具 */
	public String petItemCode;
	/** 所需道具数量 */
	public int itemCount;
	/** 经验道具代码 */
	public String expCode;
	/** 初始等级 */
	public int initLevel;
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
	/** 初始暴击 */
	public int initCrit;
	/** 暴击成长系数 */
	public float critGrowUP;
	/** 初始暴击伤害 */
	public int initCritDamage;
	/** 暴击伤害成长系数 */
	public int critDamageGrowUp;
	/** 移动速度 */
	public float moveSpeed;
	/** 初始技能ID序列 */
	public String initSkill;
	/** 对主人加成属性编号 */
	public int masterPropID;
	/** 宠物描述 */
	public String desc;
	/** 默认图标 */
	public String icon;
	/** 初始模型 */
	public String model;
	/** 模型缩放倍数 */
	public int modelPercent;
	/** 模型高度偏移 */
	public float modelY;
	/** 音效 */
	public String sound;

	/** 主键 */
	public int getKey() {
		return this.petID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}