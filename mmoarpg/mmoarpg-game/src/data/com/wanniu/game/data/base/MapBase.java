package com.wanniu.game.data.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;

/**
 * 场景地图抽象类
 * 
 * @author Yangzz
 *
 */
public abstract class MapBase {
	/** 副本ID */
	public int mapID;
	/** 副本名称 */
	public String name;
	/** 地图ID */
	public int templateID;
	/** 地图类型 */
	public int type;
	/** 怪物强度 */
	public String monsterHard;
	/** 难度模式 */
	public int hardModel;
	/** 副本标签 */
	public int dungeonTab;
	/** 界面是否显示 */
	public int dungeonShow;
	/** 允许人数 */
	public int allowedPlayers;
	/** 允许佣兵进入 */
	public int allowedPet;
	/** 重置时间 */
	public int autoReset;
	/** 同类副本ID */
	public int mapTypeID;
	/** 每日默认进入次数 */
	public int defaultTimes;
	/** 每日可购买最多次数 */
	public int maxPurchase;
	/** 掉落预览 */
	public String bonusViewTC;
	/** 推荐战斗力 */
	public int adCombatPower;
	/** 所属阵营 */
	public int race;
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
	/** 地图等级 */
	public int mapLevel;
	/** 掉线后返回地图ID */
	public int disConnToMapID;
	/** 复活地图ID */
	public int revivedMapID;
	/** 人数软上限 */
	public int fullPlayers;
	/** 人数硬上限 */
	public int maxPlayers;
	/** 生命周期 */
	public int lifeTime;
	/** 状态分界值 */
	public int boundary;
	/** 需要等级 */
	public int reqLevel;
	/** 需要进阶等级 */
	public int reqUpLevel;
	/** 限制等级 */
	public int levellimit;
	/** 限制进阶等级 */
	public int upLevellimit;
	/** 需要VIP等级 */
	public int reqVip;
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
	/** 是否可以传送 */
	public int allowedTransfer;
	/** 传送消耗道具 */
	public String costItem;
	/** 传送消耗道具数量 */
	public int costItemNum;
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
	/** 是否重置状态 */
	public int recovery;
	/** 是否更改宠物AI */
	public int changePetAI;
	/** 宠物默认AI */
	public int petAI;
	/** 是否可扫荡 */
	public int isSweep;
	/** 怪物数量 */
	public String monsterInfo;
	/** BOSS列表显示 */
	public int bossInfoShow;
	/** 组队建议 */
	public int teamTips;
	/** 成色 */
	public int qcolor;
	/** 允许最少人数 */
	public int allowedPlayersMix;
	/** 允许最大人数 */
	public int allowedPlayersMax;
	/** Boss形象图 */
	public String bossPic;
	/** 战力阀值 */
	public int fcValue;
	/**退出倒计时 */
	public int timeCount;
	/**出副本传送场景*/
	public int leaveToMapID;

	public Map<String, Integer> toPath;
	public Map<Integer, float[]> toAreaXY;
	public Map<String, float[]> toPathXY;
	public List<Integer> OpenDate;
	/** monsterId:monsterNum */
	public Map<Integer, Integer> m_monsterInfo;

	public int getKey() {
		return mapID;
	}

	public void beforeProperty() {
	}

	/** 属性构造 */
	public void initProperty() {
		m_monsterInfo = new HashMap<>();
		toPath = new HashMap<>();
		toPathXY = new HashMap<>();
		toAreaXY = new HashMap<>();
		if (!StringUtil.isEmpty(this.connect)) {
			String[] connectStrs = this.connect.split(";");
			for (String connectStr : connectStrs) {
				if (!StringUtil.isEmpty(connectStr)) {
					String[] data = connectStr.split(":");
					int areaId = Integer.parseInt(data[0]);
					toPath.put(data[1], areaId);
					if (data.length > 2) {
						String[] xys = data[2].split(",");
						float[] xy = new float[2];
						xy[0] = Float.valueOf(xys[0]);
						xy[1] = Float.valueOf(xys[1]);
						toPathXY.put(data[1], xy);
						toAreaXY.put(areaId, xy);
					}
				}
			}
		}

		if (this.openRule == Const.OpenRuleType.EVERY_WEEK.getValue()) {
			String[] openDays = this.openDate.split(",");
			if (openDays.length > 0) {
				this.OpenDate = new ArrayList<>();
				for (int i = 0; i < openDays.length; i++) {
					this.OpenDate.add(Integer.parseInt(openDays[i]));
				}
				// 周日重置为0，方便和Date.getDay匹配
				int index = this.OpenDate.indexOf(7);
				if (index >= 0) {
					this.OpenDate.set(index, 0);
				}
			}
		}

		if (this.lifeTime > 300) {
			Out.warn("sceneProp lifeTime is inValid, it should be between 0 and 300 mapId:", this.mapID, " lifeTime:", this.lifeTime);
		} else if (this.lifeTime == 0) {
			this.lifeTime = 300;
		}
		
		boolean valid = false;
		for (Const.SCENE_TYPE sceneType : Const.SCENE_TYPE.values()) {
			if (this.type == sceneType.getValue()) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			Out.error("sceneProp Type value inValid, mapId:" , this.mapID);
		}
//		if (this.type != Const.SCENE_TYPE.ARENA.getValue() 
//				&& this.type != Const.SCENE_TYPE.FIGHT_LEVEL.getValue()
//				&& this.type != Const.SCENE_TYPE.NORMAL.getValue() 
//				&& this.type != Const.SCENE_TYPE.SIN_COM.getValue()
//				&& this.type != Const.SCENE_TYPE.CROSS_SERVER.getValue() 
//				&& this.type != Const.SCENE_TYPE.ALLY_FIGHT.getValue()
//				&& this.type != Const.SCENE_TYPE.GUILD_DUNGEON.getValue() 
//				&& this.type != Const.SCENE_TYPE.WORLD_BOSS.getValue()
//				&& this.type != Const.SCENE_TYPE.ILLUSION.getValue()
//				&& this.type != Const.SCENE_TYPE.FIVE2FIVE.getValue()) {
//
//			Out.error("sceneProp Type value inValid, mapId:" + this.mapID);
//		}

		if (!StringUtil.isEmpty(this.monsterInfo)) {

			int dungeonIdAdd = 0;

			if (this.hardModel == 2) {
				dungeonIdAdd = 10000;
			} else if (this.hardModel == 3) {
				dungeonIdAdd = 20000;
			}

			String[] datas = this.monsterInfo.split(";");
			for (String data : datas) {
				String[] as = data.split(":");

				if (as.length > 1) {

					m_monsterInfo.put(Integer.parseInt(as[0]) + dungeonIdAdd, Integer.parseInt(as[1]));
				}
			}
		}
	}
}
