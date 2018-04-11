package com.wanniu.game.area;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.activity.DemonTowerService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ScheduleCO;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DemonTowerPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import Xmds.GetPlayerData;
import pomelo.area.BattleHandler.FightLevelResultPush;
import pomelo.area.BattleHandler.ItemNormal;
import pomelo.area.BattleHandler.ItemNormal.Builder;
import pomelo.Common.DemonTowerFloorInfo;
import pomelo.area.BattleHandler.SceneNamePush;

/**
 * 镇妖塔
 * 
 * @author agui
 */
public class DemonTower extends Area {

//	public static final int MAX_LV = 80; // 层级上限

	public int level = 1; // 当前镇妖塔层级

	// public TeamData team;

	public WNPlayer curPlayer;
	public Date startDate;

	static class HistoryItem {
		/** 获得的虚拟物品 */
		private Map<String, Integer> historyVirtualItems = new HashMap<>();
		/** 获得的非 虚拟物品 */
		private Map<String, Integer> historyItems = new HashMap<>();

		void addItem(NormalItem item) {
			Integer num = historyItems.get(item.itemCode());
			if (num != null) {
				num += item.getNum();
			} else {
				num = item.getNum();
			}
			historyItems.put(item.itemCode(), num);
		}

		void addVirtualItem(String itemCode, int num) {
			Integer oldNum = historyVirtualItems.get(itemCode);
			if (oldNum != null) {
				num += oldNum;
			}
			historyItems.put(itemCode, num);
		}
	}

	Map<String, HistoryItem> historyTotals = new HashMap<>();

	public DemonTower(JSONObject opts) {
		super(opts);
		if (opts.containsKey("lv")) {
			this.level = opts.getIntValue("lv");
		}
		startDate=new Date();
	}

	@Override
	public void bindBattleServer(WNPlayer player) {
		super.bindBattleServer(player);
		// team = player.getTeamManager().getTeam();
		this.curPlayer = player;
//		this.level = player.demonTowerManager.po.maxFloor;
	}

	@Override
	public boolean isUseTC() {
		return false;
	}

	@Override
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint, WNPlayer player, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		Out.debug(getClass().getName(), " onMonsterDead : ", monsterId, " - ", x, ", ", y);
		// super.onMonsterDead(monsterId, level, x, y, attackType, refreshPoint,
		// player);
	}

	@Override
	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		Out.debug(getClass().getName(), " onPickItem : ", itemId, isGuard);
		return super.onPickItem(playerId, itemId, isGuard);
	}

	protected void addVirtureItem(WNPlayer player, NormalItem dropItem, GOODS_CHANGE_TYPE type) {
		super.addVirtureItem(player, dropItem, type);
		Actor actor = actors.get(player.getId());
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
	public void onRobotQuit(int second) {

	}

	@Override
	public void onGameOver(JSONObject event) {

		int winForce = event.getIntValue("winForce");
		boolean isWin = winForce == 2; // 通关

		onPlayerWin(isWin);
	}

	private Map<String, GetPlayerData> datas = new HashMap<>();

	private List<NormalItem> randomTC(List<DropListExt> drops, int level) {
		List<NormalItem> items = null;
		if (!drops.isEmpty()) {
			DropListExt prop = drops.get(0);
			items = ItemUtil.createItemsByItemCode(prop.firstRewardPreview);//prop.randomTC(level);
		} else {
			items = new ArrayList<>(0);
		}
		return items;
	}

	private void resultPush(WNPlayer player, FightLevelResultPush.Builder result, List<DropListExt> drops,DemonTowerPO curPlayerPO,long costTime,boolean refreshChord) {
		// int domenTowerCount = getDomenTowerCount(player);
		ScheduleCO co = GameData.Schedules.get(1);
		if (co != null) {// && co.maxCount >= domenTowerCount) {
			result.setIsMax(0);
			List<NormalItem> items=null;
			if(curPlayerPO.maxFloor==level) {
				items = randomTC(drops, player.getLevel());
			}
			else {
				items=new LinkedList<>();
			}
			HistoryItem totalItems = historyTotals.get(player.getId());
			if (totalItems == null) {
				totalItems = new HistoryItem();
				historyTotals.put(player.getId(), totalItems);
			}
			for (NormalItem item : items) {
				if (item.isVirtual()) {
					result.addItemLine1(newItemBuilder(item.itemCode(), item.getNum()));
					totalItems.addVirtualItem(item.itemCode(), item.getNum());
				} else {
					result.addItemLine2(newItemBuilder(item.itemCode(), item.getNum()));
					totalItems.addItem(item);
				}
				player.bag.addCodeItemMail(item.itemCode(), item.getNum(), ForceType.DEFAULT, GOODS_CHANGE_TYPE.DemonTower, SysMailConst.BAG_FULL_COMMON);
			}
			Actor actor = getActor(player.getId());
			if (actor != null && actor.historyVirtualItems != null) {
				for (Map.Entry<String, Integer> entry : actor.historyVirtualItems.entrySet()) {
					result.addItemLine1(newItemBuilder(entry.getKey(), entry.getValue()));
					totalItems.addVirtualItem(entry.getKey(), entry.getValue());
				}
			}
		}
		DemonTowerFloorInfo.Builder floorInfoBuilder=player.demonTowerManager.getFloorInfoBuilder(level);
		result.setDemonTowerFloorInfo(floorInfoBuilder);
		result.setCurrentTime((int)costTime/1000);
		result.setNewRecordFloor(curPlayerPO.maxFloor==level);
		result.setNewRecordTime(refreshChord);
		
		
		int maxFloor=curPlayerPO.maxFloor;
		//如果打的不是最高层，则需要传递已通关的最高层，是最高层-1
		if(level<maxFloor) {
			maxFloor--;
		}
		result.setMyMaxFloorId(maxFloor);
//		Out.error(result);
		player.receive("area.battlePush.fightLevelResultPush", result.build());
	}

	private Builder newItemBuilder(String itemCode, int num) {
		return ItemNormal.newBuilder().setItemCode(itemCode).setItemNum(num);
	}

	// private int getDomenTowerCount(WNPlayer player) {
	// if (player.demonTowerCount == 0) {
	// String demonTower = GCache.hget(ConstsTR.DAILY_DEMON_TOWER_COUNT.value,
	// player.getId());
	// player.demonTowerCount = StringUtil.isNotEmpty(demonTower) ?
	// Integer.parseInt(demonTower) : 0;
	// }
	// return player.demonTowerCount;
	// }

	// private void updateDomenTowerCounr(WNPlayer player) {
	// player.demonTowerCount += 1;
	// GCache.hset(ConstsTR.DAILY_DEMON_TOWER_COUNT.value, player.getId(),
	// String.valueOf(player.demonTowerCount));
	// }

	private void onPlayerWin(boolean isWin) {
		Out.debug(getClass().getName(), " onGameOver : ", isWin);
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		List<DropListExt> drops = GameData.findDropLists(t -> {
			return t.isWeek(week) && t.floorNo == level;// (isWin ? level : level - 1);
		});

		// BI上报
		BILogService.getInstance().ansycReportDemonTower(curPlayer.getPlayer(), isWin ? 1 : 0, level);

		DemonTowerPO curPlayerPO=curPlayer.demonTowerManager.po;
		if (isWin) {
			// team.confirm = false;
			boolean jumpToNext=this.level!=GameData.DropLists.size();
			long costTime=new Date().getTime()-startDate.getTime();
			boolean refreshRecord = DemonTowerService.getInstance().finishFloor(this.level, curPlayer.getId(), costTime);
			for (String rid : actors.keySet()) {
				WNPlayer player = getPlayer(rid);
				if (player != null) {
					FightLevelResultPush.Builder result = FightLevelResultPush.newBuilder();
					result.setTime(6);
					result.setLevel(level);
					result.setType(0);
					resultPush(player, result, drops,curPlayerPO,costTime,refreshRecord);
					

					// 成就
					player.achievementManager.onPassDemonTower(level, isWin);
				}
			}

			
			if(this.level==curPlayerPO.maxFloor) {
				curPlayerPO.maxFloor++;// as win
				curPlayerPO.firstTimeToPeak = new Date();
				curPlayerPO.leastTimeList.add(costTime);
			}
			
			else if(curPlayerPO.leastTimeList.get(this.level-1)>costTime) {
				curPlayerPO.leastTimeList.set(this.level-1,costTime);
			}
			
			curPlayer.sevenGoalManager.processGoal(SevenGoalTaskType.DEMON_TOWER_COUNT);
			if(jumpToNext) {
				JobFactory.addDelayJob(() -> {
					for (Map.Entry<String, Actor> entry : actors.entrySet()) {
						if (entry.getValue().alive) {
							datas.put(entry.getKey(), getPlayerData(entry.getKey()));
						} else {
							GetPlayerData data = datas.get(entry.getKey());
							if (data == null) {
								data = new GetPlayerData();
								datas.put(entry.getKey(), data);
							} else {
								data.hp = 0;
								data.mp = 0;
							}
						}
					}
					// WNPlayer leader = getPlayer(team.leaderId);
					// DemonTower area = (DemonTower) AreaUtil.createArea(leader,
					// Utils.toJSON("logicServerId",
					// leader.getLogicServerId(), "areaId", team.getTargetMap(), "lv", ++level));
					// area.datas = datas;
					// area.level = level;
					// area.team = team;
					// area.historyTotals = historyTotals;
					// Out.debug(datas.size(), " demon tower game
					// over===================================team:", team.id,
					// " change area!!! lv:", area.level);

					
					this.level ++;
					

					if (curPlayer.rankManager != null) {
						curPlayer.rankManager.onEvent(RankType.DEMON_TOWER, curPlayerPO.maxFloor - 1);
					}
					
					
					
//					int mapId = GlobalConfig.DemonTowerMapIds[RandomUtil.getIndex(GlobalConfig.DemonTowerMapIds.length)];
					DropListExt dropListExt = GameData.DropLists.get(level);
					if(dropListExt==null) {
						Out.error("参数错误");
						return;
					}
					int mapId = dropListExt.mapId;
					DemonTower area = (DemonTower) AreaUtil.createArea(curPlayer, Utils.toJSON("logicServerId", curPlayer.getLogicServerId(), "areaId", mapId, "lv", level));
					
					area.datas = datas;
					area.level = level;
					area.historyTotals = historyTotals;
					Out.debug(datas.size(), " demon tower game over===================================Player:", curPlayer.getName(), " change area!!! lv:", area.level);

					AreaData areaData = new AreaData(area.areaId, area.instanceId);
					for (String rid : actors.keySet()) {
						WNPlayer player = getPlayer(rid);
						if (player != null) {

							AreaUtil.changeArea(player, areaData);
						}
					}
				}, 8 * GGlobal.TIME_SECOND);
				return;
			}
			
		} else {
			curPlayer.demonTowerManager.po.failedMapId = this.areaId;
		}
		// team.confirm = true;

		for (String rid : actors.keySet()) {
			WNPlayer player = getPlayer(rid);
			if (player != null&&curPlayerPO.maxFloor==level) {
				FightLevelResultPush.Builder result = FightLevelResultPush.newBuilder();
				result.setTime(10);
				result.setLevel(level);// (isWin ? level : level - 1);
				result.setType(1);
				// int domenTowerCount = getDomenTowerCount(player);
				ScheduleCO co = GameData.Schedules.get(1);
				if (co != null) {// && domenTowerCount <= co.maxCount) {
					result.setIsMax(0);
					HistoryItem totalItems = historyTotals.get(rid);
					if (totalItems == null) {
						totalItems = new HistoryItem();
						historyTotals.put(player.getId(), totalItems);
					}

					if (level >= GameData.DropLists.size()) {
						List<NormalItem> items = randomTC(drops, player.getLevel());
						for (NormalItem item : items) {
							if (item.isVirtual()) {
								totalItems.addVirtualItem(item.itemCode(), item.getNum());
							} else {
								totalItems.addItem(item);
							}
							player.bag.addCodeItemMail(item.itemCode(), item.getNum(), ForceType.DEFAULT, GOODS_CHANGE_TYPE.DemonTower, SysMailConst.BAG_FULL_COMMON);
						}
					}

					for (Map.Entry<String, Integer> entry : totalItems.historyVirtualItems.entrySet()) {
						result.addItemLine1(newItemBuilder(entry.getKey(), entry.getValue()));
					}
					for (Map.Entry<String, Integer> entry : totalItems.historyItems.entrySet()) {
						result.addItemLine2(newItemBuilder(entry.getKey(), entry.getValue()));
					}
				} else {
					result.setIsMax(1);
					// 增加仙缘值
					Collection<String> teamData = player.getTeamMembers();
					if (teamData != null) {
						for (String teamMemPlayerId : teamData) {
							// 自己没收益其他人有收益
							if (this.actors.containsKey(teamMemPlayerId) && getActor(teamMemPlayerId).profitable) {
								int xianyuan = player.processXianYuanGet(GlobalConfig.Fate_Resource);
								result.addItemLine1(newItemBuilder("fate", xianyuan));
								break;
							}
						}
					}
				}
				DemonTowerFloorInfo.Builder floorInfoBuilder=player.demonTowerManager.getFloorInfoBuilder(level);
				result.setDemonTowerFloorInfo(floorInfoBuilder);
				result.setMyMaxFloorId(curPlayerPO.maxFloor-1);
				Out.error(result.build());
				player.receive("area.battlePush.fightLevelResultPush", result.build());
			}
		}

		super.onRobotQuit(10);
	}

	public String toJSON4EnterScene(WNPlayer player) {
		JSONObject json = player.toJSON4EnterScene(this);
		@SuppressWarnings("unchecked")
		Map<String, Number> tempData = (Map<String, Number>) json.get("tempData");
		tempData.put("x", 0);
		tempData.put("y", 0);
		GetPlayerData data = datas.get(player.getId());
		if (data != null) {
			int maxHp = player.btlDataManager.finalInflus.get(Const.PlayerBtlData.MaxHP);
			int hp = data.hp + maxHp * 20 / 100;
			tempData.put("hp", Math.min(hp, maxHp));
			tempData.put("mp", data.mp);
		}
		// else if (level != 1) {
		// int maxHp = player.btlDataManager.finalInflus.get(Const.PlayerBtlData.MaxHP);
		// tempData.put("hp", maxHp * 20 / 100);
		// tempData.put("mp", 0);
		// }
		return json.toJSONString();
	}

	protected void onDailyActivity(WNPlayer player) {
		super.onDailyActivity(player);
		player.dailyActivityMgr.onEvent(Const.DailyType.DEMON_TOWER, "0", 1);
	}

	public void pushRelive(WNPlayer player) {
		// 不可复活
	}

	@Override
	public void onPlayerDeadByMonster(WNPlayer player, AreaEvent.MonsterData monsterData, float playerX, float playerY) {
		Out.debug(getClass().getName(), " onPlayerDeadByMonster : ", player.getName());
		// super.onPlayerDeadByMonster(player, monsterData);
		if (isAllActorDie()) {
			this.isClose = true;
			onPlayerWin(false);
			addCloseFuture();
		}

		player.activityManager.triggerLimitTimeGift(2);
	};

	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadPlayer, WNPlayer hitPlayer, float x, float y) {
		Out.warn(getClass().getName(), " onPlayerDeadByPlayer : ", deadPlayer.getName());
	}

	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		// Actor actor = getActor(player.getId());
		// int domenTowerCount = getDomenTowerCount(player);
		// ScheduleCO co = GameData.Schedules.get(1);
		// actor.profitable = (co != null && domenTowerCount <= co.maxCount);
	}

	@Override
	public void onPlayerLeaved(WNPlayer player) {
		if (!this.isClose && isAllActorDie()) {
			this.isClose = true;
			onPlayerWin(false);
			addCloseFuture();
		}
	}

	@Override
	public void onEndEnterScene(WNPlayer player) {
		super.onEndEnterScene(player);
		player.receive(new PomeloPush() {
			@Override
			protected void write() throws IOException {
				SceneNamePush.Builder push = SceneNamePush.newBuilder();
				push.setSceneName(LangService.format("DEMON_TOWER_LV", level));
				body.writeBytes(push.build().toByteArray());
			}

			@Override
			public String getRoute() {
				return "area.battlePush.sceneNamePush";
			}
		});
	}

}
