package com.wanniu.game.data; 

public class RecomPlayCO { 

	/** 玩法ID */
	public int playID;
	/** 玩法名称 */
	public String playName;
	/** 是否组队 */
	public int beParty;
	/** 产出分类 */
	public int outPut;
	/** 是否有效 */
	public int isValid;
	/** 功能类型 */
	public int funType;
	/** 日常分类 */
	public int taskCycle;
	/** 菜单名称 */
	public String fun;
	/** 快捷入口 */
	public String funID;
	/** 副本ID */
	public String mapID;
	/** 推荐进阶等级 */
	public int upOrder;
	/** 需要等级 */
	public int level;
	/** 进阶等级上限 */
	public int upLimit;
	/** 等级上限 */
	public int levelLimit;
	/** 产出描述 */
	public String bonusDesc;

	/** 主键 */
	public int getKey() {
		return this.playID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}