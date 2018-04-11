package com.wanniu.game.data; 

public class SkillDataCO { 

	/** 技能ID */
	public int skillID;
	/** 显示序列 */
	public int skillIndex;
	/** 技能名称 */
	public String skillName;
	/** 专属职业 */
	public String pro;
	/** 系别 */
	public int tab;
	/** 技能类型 */
	public int skillType;
	/** 技能描述 */
	public String skillDesc;
	/** 最高等级 */
	public int maxLevel;
	/** 前置技能ID */
	public int preSkillID;
	/** 前置技能名称 */
	public String preSkillName;
	/** 前置技能等级 */
	public int preSkillLevel;
	/** 强化目标技能ID */
	public int enSkillID;
	/** 是否自动学习 */
	public int learnSkill;
	/** 技能图标 */
	public String skillIcon;
	/** 升级需要人物等级序列 */
	public String upReqLevel;
	/** 升级花费金币序列 */
	public String upCostGold;
	/** 升级花费技能点序列 */
	public String upCostSP;
	/** 升级消耗道具序列 */
	public String upCostItem;
	/** 预览视频文件 */
	public String videoFile;
	/** 技能受武器限制情况 */
	public int resByWeapon;
	/** 每等级提供战斗力 */
	public String power;

	/** 主键 */
	public int getKey() {
		return this.skillID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}