package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.DungeonMapCostCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FightLevelsPO.RDDoubleRewardPO;
import com.wanniu.game.poes.FightLevelsPO.ResourceDungeonPO;

import pomelo.area.BagHandler.BagNewItemFromResFubenPush;
import pomelo.area.BattleHandler.ItemNormal;
import pomelo.area.BattleHandler.ResourceDungeonResultPush;

/**
 * 资源副本
 * 
 * @author Yangzz
 */
public class ResourceDungeon extends Area {

	/** 杀怪数量 */
	public int killedMonster;

	public long createTime;

	public ResourceDungeon(JSONObject opts) {
		super(opts);
		killedMonster = 0;
	}

	@Override
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint, WNPlayer hitFinalPlayer, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		Out.debug(getClass().getName(), " onMonsterDead : ", monsterId, " - ", x, ", ", y);
		super.onMonsterDead(monsterId, level, x, y, attackType, refreshPoint, hitFinalPlayer, teamSharedIdList, null);
		killedMonster++;
	}

	@Override
	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		Out.debug(getClass().getName(), " onPickItem : ", itemId, isGuard);
		// if (this.isClose) {
		// return null;
		// }
		AreaItem areaItem = super.onPickItem(playerId, itemId, isGuard);
		if (areaItem == null) {
			return null;
		}
		Actor actor = actors.get(playerId);
		if (actor.historyItems == null) {
			actor.historyItems = new ArrayList<>();
		}

		synchronized (actor.historyItems) {
			actor.historyItems.add(areaItem.item);
		}

		// 极限挑战...
		DungeonMapCostCO resourceConfig = GameData.DungeonMapCosts.get(prop.mapID);
		if (resourceConfig.playType == 1) {
			BagNewItemFromResFubenPush.Builder push = BagNewItemFromResFubenPush.newBuilder();
			push.addS2CData(areaItem.item.toJSON4MiniItem());
			WNPlayer player = getPlayer(playerId);
			player.receive("area.bagPush.bagNewItemFromResFubenPush", push.build());
		}

		return areaItem;
	}

	protected void addVirtureItem(WNPlayer player, NormalItem dropItem, GOODS_CHANGE_TYPE type) {
		if (this.isClose) {
			return;
		}
		super.addVirtureItem(player, dropItem, type);
		Actor actor = actors.get(player.getId());
		if (actor == null) {
			return;
		}
		Map<String, Integer> historyItems = actor.historyVirtualItems;
		if (historyItems == null) {
			actor.historyVirtualItems = historyItems = new HashMap<>();
		}
		String itemCode = dropItem.itemCode();
		int count = historyItems.containsKey(itemCode) ? historyItems.get(dropItem.itemCode()) : 0;
		count += dropItem.getWorth();
		historyItems.put(itemCode, count);
	}

	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);

		createTime = System.currentTimeMillis();

		// 扣除次数
		FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
		ResourceDungeonPO resourceDungeon = fightLevelsPO.resourceDungeon.get(areaId);
		if (resourceDungeon.entering) {
			resourceDungeon.entering = false;
			resourceDungeon.usedTimes += 1;
			Out.info(player.getId(), " 成功进入资源副本:", player.getArea().getSceneName(), " 本日进入次数为:", resourceDungeon.usedTimes);
		}

		DungeonMapCostCO resourceConfig = GameData.DungeonMapCosts.get(prop.mapID);
		if (resourceConfig.playType == 1) {
			player.dailyActivityMgr.onEvent(Const.DailyType.RESOURCE_CHALLENGE, "0", 1);
		} else if (resourceConfig.playType == 2) {
			player.dailyActivityMgr.onEvent(Const.DailyType.RESOURCE_WATCH_PET, "0", 1);
		} else if (resourceConfig.playType == 3) {
			player.dailyActivityMgr.onEvent(Const.DailyType.RESOURCE_FARM, "0", 1);
		}

		// 极限挑战进入，尝试触发限时礼包
		if (resourceConfig.playType == 1) {
			player.activityManager.triggerLimitTimeGift(1);

		}
	}

	@Override
	public void pushRelive(WNPlayer player) {
		DungeonMapCostCO resourceConfig = GameData.DungeonMapCosts.get(prop.mapID);
		if (resourceConfig.playType == 2) {
			super.pushRelive(player);
		}
	}

	@Override
	public void onGameOver(JSONObject event) {

		int winForce = event.getIntValue("winForce");

		DungeonMapCostCO resourceConfig = GameData.DungeonMapCosts.get(prop.mapID);
		if (resourceConfig.playType == 1) {
			// 延迟3秒结算，boss死亡后战斗服发拾取协议有延迟2秒的
			JobFactory.addDelayJob(() -> {
				onPlayerWin(winForce == 1, resourceConfig);
			}, 3000);
		} else {
			// 延迟3秒结算，boss死亡后战斗服发拾取协议有延迟2秒的
			JobFactory.addDelayJob(() -> {
				onPlayerWin(winForce == 2, resourceConfig);
			}, 3000);
		}

	}

	private void onPlayerWin(boolean isWin, DungeonMapCostCO resourceConfig) {
		Out.debug(getClass().getName(), " onGameOver : ");

		for (String rid : actors.keySet()) {
			Actor actor = actors.get(rid);
			WNPlayer player = getPlayer(rid);
			if (player != null && actor != null) {
				// 资源副本 结算通知
				if (resourceConfig != null) {
					// 保存双倍奖励,下次进入副本清除
					if (resourceConfig.isDoubleBonus == 1) {
						FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
						fightLevelsPO.doubleReward = new RDDoubleRewardPO();

						fightLevelsPO.doubleReward.doubleVirtualItems = actor.historyVirtualItems;
						fightLevelsPO.doubleReward.doubleItems = new ArrayList<>();
						if (actor.historyItems != null) {
							synchronized (actor.historyItems) {
								for (NormalItem item : actor.historyItems) {
									fightLevelsPO.doubleReward.doubleItems.add(item.itemDb);
								}
							}
						}
					}
					ResourceDungeonResultPush.Builder resourcePush = ResourceDungeonResultPush.newBuilder();
					resourcePush.setDungeonId(prop.mapID);
					resourcePush.setKillMonster(killedMonster);
					resourcePush.setDoubleCost(resourceConfig.isDoubleBonus == 1 ? resourceConfig.bounsCostDiamond : -1);

					resourcePush.setExp(0);
					resourcePush.setGold(0);
					if (actor.historyVirtualItems != null && actor.historyVirtualItems.size() > 0) {
						for (String code : actor.historyVirtualItems.keySet()) {
							int value = actor.historyVirtualItems.get(code);
							switch (code) {
							case "exp":
								resourcePush.setExp(value);
								break;
							case "gold":
								resourcePush.setGold(value);
								break;
							}
						}
					}

					Map<String, Integer> finalItems = new HashMap<>();
					if (actor.historyItems != null && actor.historyItems.size() > 0) {
						synchronized (actor.historyItems) {
							for (NormalItem item : actor.historyItems) {
								if (finalItems.get(item.itemDb.code) == null) {
									finalItems.put(item.itemDb.code, item.itemDb.groupCount);
								} else {
									finalItems.put(item.itemDb.code, finalItems.get(item.itemDb.code) + item.itemDb.groupCount);
								}
							}
						}
					}
					List<ItemNormal> list_rewards = new ArrayList<>();
					for (String code : finalItems.keySet()) {
						ItemNormal.Builder itemNormal = ItemNormal.newBuilder();
						itemNormal.setItemCode(code);
						itemNormal.setItemNum(finalItems.get(code));
						list_rewards.add(itemNormal.build());
					}

					resourcePush.addAllItemLine1(list_rewards);
					resourcePush.setSucc(isWin ? 1 : 0);
					player.receive("area.battlePush.resourceDungeonResultPush", resourcePush.build());
				}

				// 资源副本结算时，上报BI
				BILogService.getInstance().ansycReportResourceDungeon(player.getPlayer(), isWin ? 1 : 0, resourceConfig.mapID);
			}
		}
	}

	/**
	 * 怪物击杀玩家
	 */
	@Override
	public void onPlayerDeadByMonster(WNPlayer player, AreaEvent.MonsterData monsterData, float playerX, float playerY) {
		super.onPlayerDeadByMonster(player, monsterData, playerX, playerY);

	}

}
