package com.wanniu.game.vip;

/**
 * vip特权增加的功能
 * 
 * @author c
 */
public enum VipFuncType {

	SINGLE_SCENE(1), // 单人副本
	TEAM_SCENE(2), // 组队副本
	SECRET_SCENE(3), // 地宫副本
	SUPER_SCENE(4), // 秘境副本
	SIN_COM(5), // 单挑王次数
	MONSTER_KILL_EXP(6), // 击杀怪物获得的经验值
	EVERY_SIGN(7), // 每日签到
	FAME_ADD(8), // 声望增加（已作废）
	MONERY_TREE(9), // 摇钱树次数增加
	CONSIGNMENT_STORE(10), // 寄卖行上架数增加
	BUY_SINGLE_SCENE(11), // 单人副本购买次数
	BUY_TEAM_SCENE(12), // 组队副本购买次数
	BUY_SUPER_SCENE(13); // 秘境副本购买次数

	private int value;

	private VipFuncType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}