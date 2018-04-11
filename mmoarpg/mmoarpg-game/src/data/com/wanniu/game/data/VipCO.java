package com.wanniu.game.data; 

public class VipCO { 

	/** VIP等级 */
	public int vipLevel;
	/** 特权礼包 */
	public String vIPReward;
	/** 每日礼包 */
	public String dailyReward;
	/** 每日礼包价格（钻石币） */
	public int dailyPrice;
	/** Vip图标 */
	public String vipIcon;
	/** 点金石增加次数 */
	public int vipMoneyTime;
	/** 额外获得金币 */
	public int extraGold;
	/** 额外获得经验值 */
	public int extraExp;
	/** 自动熔炼的装备品质 */
	public int extraQcolor;
	/** 单人副本增加次数 */
	public int singleDungeonTime;
	/** 单人副本可购买次数 */
	public int buySingleDungeonTime;
	/** 组队副本增加次数 */
	public int teamDungeonTime;
	/** 组队副本可购买次数 */
	public int buyTeamDungeonTime;
	/** 秘境副本增加次数 */
	public int mysteriesDungeonTime;
	/** 秘境副本可购买次数 */
	public int buyMysteriesDungeonTime;
	/** 超级副本增加次数 */
	public int superDungeonTime;
	/** 超级副本可购买次数 */
	public int buySuperDungeonTime;
	/** 单挑王增加次数 */
	public int soloTime;
	/** 寄卖行上架数增加 */
	public int storeItemNum;
	/** VIP描述 */
	public String vipDes;

	/** 主键 */
	public int getKey() {
		return this.vipLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}