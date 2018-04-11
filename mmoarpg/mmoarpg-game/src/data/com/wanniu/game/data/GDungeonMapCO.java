package com.wanniu.game.data; 

public class GDungeonMapCO { 

	/** 副本ID */
	public int mapID;
	/** 副本名称 */
	public String name;
	/** 层数 */
	public int layer;
	/** 地图ID */
	public int templateID;
	/** 地图类型 */
	public int type;
	/** 怪物强度 */
	public String monsterHard;
	/** 难度模式 */
	public int hardModel;
	/** 允许人数 */
	public int allowedPlayers;
	/** 允许佣兵进入 */
	public int allowedPet;
	/** 进度保存时长 */
	public int keepPeriod;
	/** 每日默认进入次数 */
	public int defaultTimes;
	/** 每日可购买最多次数 */
	public int maxPurchase;
	/** 掉落预览 */
	public String bonusViewTC;
	/** 推荐战斗力 */
	public int adCombatPower;
	/** 是否安全区 */
	public int isSafe;
	/** 默认PK模式 */
	public int pktype;
	/** 是否允许改变PK模式 */
	public int changePKtype;
	/** 是否无视PK红名规则 */
	public int ignorePkRule;
	/** 是否可传送进入 */
	public int canBeTrans;
	/** 生命周期 */
	public int lifeTime;
	/** 需要等级 */
	public int reqLevel;
	/** 需要进阶等级 */
	public int reqUpLevel;
	/** 需要任务ID */
	public int reqQuestId;
	/** 可进入阵营 */
	public int allowedRace;
	/** 状态条件 */
	public String reqState;
	/** 状态值 */
	public int stateValue;
	/** 开放策略 */
	public int openRule;
	/** 开放日 */
	public String openDate;
	/** 开始时间 */
	public String beginTime;
	/** 结束时间 */
	public String endTime;
	/** 关闭时强制传送到地图ID */
	public int closedToMapID;
	/** 进入需要物品Code */
	public String reqItemCode;
	/** 进入需要物品数量 */
	public int reqItemCount;
	/** 进入后扣除物品数量 */
	public int reduceItemCount;
	/** 扣除金币 */
	public int costGold;
	/** 随机宝箱出现概率 */
	public int randChestChance;
	/** 随机宝箱最大数量 */
	public int maxRandChest;
	/** 随机宝箱TC */
	public String randChestTC;
	/** 场景小地图 */
	public String sceneSmallMap;
	/** 地图描述 */
	public String mapDesc;
	/** 副本缩略图 */
	public String mapPic;
	/** 场景传送 */
	public String connect;
	/** 是否允许改变分配方式 */
	public int isChange;
	/** 默认分配模式 */
	public int distribution;
	/** 能否自动战斗 */
	public int autoFight;
	/** 能否吃药剂 */
	public int useAgent;
	/** 能否使用坐骑 */
	public int rideMount;
	/** 能否携带宠物 */
	public int takePet;
	/** 复活次数 */
	public int revival;
	/** 积分获得 */
	public int gpoints;
	/** 解锁需要的公会等级 */
	public int unlockLv;

	/** 主键 */
	public int getKey() {
		return this.mapID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}