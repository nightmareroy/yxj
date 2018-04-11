package com.wanniu.game.data; 

public class NpcListCO { 

	/** NPC编号 */
	public int npcID;
	/** NPC名字 */
	public String name;
	/** 副名字/称号 */
	public String title;
	/** 场景 */
	public int mapID;
	/** 所属阵营 */
	public int race;
	/** 性别 */
	public int sex;
	/** 等级 */
	public int level;
	/** 是否为治愈NPC */
	public int isCure;
	/** 功能ID */
	public String funID;
	/** 默认对白 */
	public String dialog;
	/** 非交互时，NPC特殊语音概率 */
	public int waitSpeakChance;
	/** 非交互时，NPC特殊语音 */
	public String waitSpeakWords;
	/** 非交互时，检索半径 */
	public int waitSpeakRange;
	/** 交互时，NPC特殊语音概率 */
	public int speakChance;
	/** 交互时，NPC特殊语音 */
	public String speakWords;
	/** NPC故事 */
	public String npcStory;
	/** 故事标题按钮文字 */
	public String beginStoryBtn;
	/** 继续按钮文字 */
	public String continueBtn;
	/** 结束按钮文字 */
	public String endBtn;
	/** 3D模型文件 */
	public String modelFile;
	/** 称号图标 */
	public String icon;
	/** NPC显示 */
	public String coord;
	/** 携带物品TC */
	public String carryTC;
	/** 类型 */
	public String type;
	/** 偷窃成功执行TC概率 */
	public int sucessDrop;
	/** 偷窃失败大骂概率 */
	public int failScold;
	/** 大骂对白 */
	public String scoldDia;
	/** 大骂按钮文字 */
	public String scoldBtn;
	/** 偷窃失败战斗概率 */
	public int failBattle;
	/** 战斗传送ID */
	public String transportID;
	/** 战斗怪物ID */
	public int monsterID;
	/** 怪物数量 */
	public int monsterNum;

	/** 主键 */
	public int getKey() {
		return this.npcID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}