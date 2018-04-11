package com.wanniu.game.data; 

public class SoloRankCO { 

	/** 段位ID */
	public int iD;
	/** 段位名称 */
	public String rankName;
	/** 段位等级 */
	public int rankLevel;
	/** 段位积分 */
	public int rankScore;
	/** 参赛宝箱开启奖励 */
	public String chestReward;
	/** 宝箱预览 */
	public String chestPreview;
	/** 段位奖励 */
	public String rankReward;
	/** 段位继承ID */
	public int rankInherit;
	/** 额外奖励的连胜需求 */
	public int extraRequire;
	/** 对应图标 */
	public String icon;
	/** 文字显示颜色 */
	public String textColour;
	/** 头像框 */
	public String headIcon;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}