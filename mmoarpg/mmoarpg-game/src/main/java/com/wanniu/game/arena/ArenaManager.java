package com.wanniu.game.arena;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.area.Area.AreaItem;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.JJCRewardExt;
import com.wanniu.game.five2Five.Five2FiveService;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.data.ItemToBtlServerData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ArenaDataPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;
import com.wanniu.redis.PlayerPOManager;

import Xmds.GetPlayerData;
import pomelo.area.ArenaHandler.ArenaInfoResponse;
import pomelo.area.ArenaHandler.ArenaInfoResponse.Builder;
import pomelo.area.ArenaHandler.ArenaRewardResponse;
import pomelo.area.ArenaHandler.EnterArenaAreaResponse;
import pomelo.area.ArenaHandler.LeaveArenaAreaResponse;
import pomelo.area.PlayerHandler.SuperScriptType;

public class ArenaManager {
	private WNPlayer player;

	private ArenaDataPO arenaData;

	private MapBase mapProp;

	public ArenaManager(WNPlayer player) {
		this.player = player;
		init(this.player.getId());
	}

	/**
	 * 初始化存档及数据
	 * 
	 * @param playerId
	 */
	private void init(String playerId) {
		ArenaDataPO arenaDataDb = getFromRedis(playerId);
		if (arenaDataDb == null) {
			arenaDataDb = new ArenaDataPO();
			PlayerPOManager.put(ConstsTR.player_arena_dataTR, playerId, arenaDataDb);
		}
		this.arenaData = arenaDataDb;
		this.mapProp = ArenaService.getInstance().getArenaMap();
	}

	public Builder getArenaInfo() {
		ArenaInfoResponse.Builder res = ArenaInfoResponse.newBuilder();
		initSeasonAward();

		res.setS2CSingleRank(getSingleRankIndex());
		res.setS2CSingleReward(getSingleReward());
		res.setS2CTotalReward(this.arenaData.totalReward);
		res.setS2CTotalRank(getLastTotalRank());
		res.setS2CCurrentTotalRank(getCurrentTotalRank());
		res.setS2CCurrentTotalScore(arenaData.scoreMonth);
		res.setS2CSeasonEndTime(ArenaService.getInstance().getSeasonEndTime().getTime());

		this.player.updateSuperScriptList(this.getSuperScript());
		return res;
	}

	/**
	 * 可进入返回null
	 * 
	 * @return
	 */
	private String canEnter() {
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.JJC.getValue())) {
			return LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN");
		}
		// 在5V5里面
		if (Five2FiveService.getInstance().applyMatchTime(player.getId()) != null) {
			return LangService.getValue("JJC_IN_PVP_NOT_JOIN");
		}
		// 在问道里面
		if (player.soloManager.isBusy() || player.soloManager.isInMatching()) {
			return LangService.getValue("JJC_IN_PVP_NOT_JOIN");
		}
		if (this.arenaData.usedDefTimes >= GlobalConfig.JJC_EnterCount) {
			return LangService.getValue("DUNGEON_JJC_NUM_NOT_ENOUGH");
		}
		if (this.mapProp.reqUpLevel > 0 && this.player.getPlayer().upLevel < this.mapProp.reqUpLevel) {
			return LangService.getValue("PLAER_UPLEVEL_NOT_ENOUGH");
		} else if (this.mapProp.reqLevel > 0 && this.player.getLevel() < this.mapProp.reqLevel) {
			return LangService.getValue("PLAYER_LEVEL_NOT_ENOUGH") + this.mapProp.reqUpLevel;
		} else {
			if (ArenaService.getInstance().isInOpenTime()) {
				// 判断是否在时段内
				return null;
			} else {
				return LangService.getValue("DUNGEON_JJC_NOT_OPEN");
			}
		}
	};

	/**
	 * 在WNPlayer的每日定时器里被调用
	 */
	public void refreshNewDay() {
		if (this.player.functionOpenManager.isOpen(Const.FunctionType.JJC.getValue())) {
			resetDaily();
			this.arenaData.usedDefTimes = 0;
			this.player.updateSuperScriptList(this.getSuperScript());
			Out.debug("ArenaManager.refreshNewDay() on ---------------");
		}
	}

	private void resetDaily() {
		// 进入新的竞技场，积分重置为0
		this.arenaData.score = 0;
		// 当场连杀次数重置为0
		this.arenaData.combo = 0;
		this.arenaData.comboDaily = 0;
		this.arenaData.killDaily = 0;
	}

	// 进入竞技场场景
	private void enterArenaArea() {
		if (this.getActivityTime() != ArenaService.getInstance().getBeginTime()) {
			this.setActivityTime(ArenaService.getInstance().getBeginTime());
		}
		String instanceId = getArenaInstanceId();
		if (StringUtil.isNotEmpty(instanceId)) {
			Area area = AreaManager.getInstance().getArea(instanceId);
			if (area != null && !area.isClose()) {
				AreaUtil.dispatchByInstanceId(player, new AreaData(area.areaId, instanceId));
				return;
			}
		}
		AreaUtil.dispatchByAreaId(this.player, this.mapProp.mapID,null);
	}

	/**
	 * 获取当天排名， 为0表示尚未参与
	 */
	public int getSingleRankIndex() {
		return (int) RankType.ARENA_SCORE.getHandler().getRank(GWorld.__SERVER_ID, player.getId());
	}

	public int getScore() {
		return this.arenaData.score;
	}

	/**
	 * @return 返回当月最大杀人头数
	 */
	public int getKillMonth() {
		return this.arenaData.killMonth;
	}

	public int getCombo() {
		return this.arenaData.combo;
	}

	/**
	 * 获取单日奖励领取状态 0-不可领取 1-可领取 2-已领取
	 * 
	 * @return
	 */
	public int getSingleReward() {
		if (!ArenaService.getInstance().canDrawDayAward()) {
			return 0;
		}
		return this.arenaData.singleReward;
	}

	private long getActivityTime() {
		return this.arenaData.activityTime;
	}

	/**
	 * 设置当前参与活动的时间
	 * 
	 * @param timeMillis
	 */
	private void setActivityTime(long timeMillis) {
		this.arenaData.activityTime = timeMillis;
	}

	/**
	 * 获取上赛季排名
	 */
	public int getLastTotalRank() {
		return ArenaService.getInstance().getLastAllScoreRank(this.player.getId());
	}

	/**
	 * 获取当前赛季排名
	 */
	public int getCurrentTotalRank() {
		return ArenaService.getInstance().getCurrentAllScoreRank(this.player.getId());
	}

	/**
	 * 获取当前赛季分数
	 */
	public int getCurrentTotalScore() {
		return arenaData.scoreMonth;
	}

	private void reCalcScore(int areaPlayerCount, int changeAddRate) {
		float addRate = (20 - areaPlayerCount) * 0.1f + 1;
		addRate = addRate > 2.8f ? 2.8f : addRate;
		int score = (int) (GlobalConfig.JJC_KillScore * changeAddRate * addRate);
		addScore(score);
	}

	public void onDead() {
		// 死亡终止连杀
		this.arenaData.combo = 0;
		this.arenaData.deadMonth += 1;
	}

	public void onHit(int areaPlayerCount, int changeAddRate) {
		this.arenaData.combo = this.arenaData.combo + 1;
		// 更新最大连杀次数和获得时间
		if (this.arenaData.comboDaily < this.arenaData.combo) {
			this.arenaData.comboDaily = this.arenaData.combo;
			// this.arenaData.maxKillCountTime = System.currentTimeMillis();
		}
		this.arenaData.killDaily += 1;
		this.arenaData.killMonth += 1;
		reCalcScore(areaPlayerCount, changeAddRate);
	}

	/**
	 * 增加积分
	 * 
	 * @param addNumber
	 */
	public void addScore(int addNumber) {
		this.arenaData.score += addNumber;
	}

	/*
	 * 添加连杀次数, 计算积分
	 */
	public void killPlayer(WNPlayer enemy, Actor deadActor, int areaPlayerCount) {
		if (this.isInArenaMap(this.player) && this.isInArenaMap(enemy)) {
			// 死亡玩家身上是否有【天神】buffer并清除
			boolean hasTsBuff = deadActor.buffers.remove(String.valueOf(Const.Arena.ARENA_TIANSHEN.value));
			int changeAddRate = hasTsBuff ? 3 : 1;
			this.onHit(areaPlayerCount, changeAddRate);
			enemy.arenaManager.onDead();

			// 成就
			this.player.achievementManager.onArenaKill();
			this.player.achievementManager.onArenaScore(this.getScore());
			Out.debug("killPlayer: ", this.arenaData.score, " ", this.arenaData.combo);
			// this.player.biServerManager.winArena();
		}
	};

	/**
	 * 掉落玩家积分
	 * 
	 * @param hitPlayer if killed by monster ,the hitPlayer can be null
	 * @param x
	 * @param y
	 * @param score
	 */
	public void dropPlayerScore(WNPlayer hitPlayer, float x, float y, int score) {
		if (score == 0) {
			return;
		}
		Area arenaArea = player.getArea();
		if (arenaArea == null) {
			return;
		}

		List<NormalItem> normalItems = new ArrayList<>();
		int minDropCount = 3;
		int maxDropCount = 7;
		if (score < minDropCount) {
			minDropCount = score;
		}
		if (score < maxDropCount) {
			maxDropCount = score;
		}
		// 能量球个数
		int randomDropCount = RandomUtil.getInt(minDropCount, maxDropCount);
		float scoreF = score;
		for (; randomDropCount > 0; randomDropCount--) {
			int randomScore = getRandomNum(randomDropCount, score);
			score -= randomScore;

			String itemCode = "";
			if (randomScore / scoreF * 100 > 22) {
				itemCode = GlobalConfig.JJC_BigItemCode;
			} else if (randomScore / scoreF * 100 > 15 && randomScore / scoreF * 100 <= 22) {
				itemCode = GlobalConfig.JJC_MiddleItemCode;
			} else {
				itemCode = GlobalConfig.JJC_SmallItemCode;
			}
			List<NormalItem> tempNormalItems = ItemUtil.createItemsByItemCode(itemCode, randomScore);
			normalItems.addAll(tempNormalItems);
		}

		Out.debug(getClass(), "Area onPlayerDeadByPlayer items:", normalItems.size());
		List<ItemToBtlServerData> itemsPayLoad = new ArrayList<>();
		for (NormalItem dropItem : normalItems) {
			dropItem.itemDb.gotTime = new Date();
			AreaItem areaItem = new AreaItem(dropItem);
			areaItem.dropPlayer = this.player;

			// int randomAdd = RandomUtil.getInt(8);
			// boolean xIsAdd = RandomUtil.random(2) > 0 ? true : false;
			// if (xIsAdd) {
			// x += randomAdd;
			// }
			areaItem.dropX = x;
			// boolean yIsAdd = RandomUtil.random(2) > 0 ? true : false;
			// if (yIsAdd) {
			// y += randomAdd;
			// }
			areaItem.dropY = y;

			arenaArea.items.put(dropItem.itemDb.id, areaItem);
			List<String> list_pids = new ArrayList<>();
			if(hitPlayer!=null) {
				list_pids.add(hitPlayer.getId());
			}			
			ItemToBtlServerData itemData = dropItem.toJSON4BatterServer(list_pids, Const.TEAM_DISTRIBUTE_TYPE.FREEDOM, true);
			itemData.protectTime = 0;
			itemData.distributeType = 0;// 解决战斗服检测背包问题。
			itemsPayLoad.add(itemData);
		}
		// 向战斗服发送死亡物品掉落数据
		String data = Utils.toJSON("pos", Utils.ofMap("x", x, "y", y), "items", itemsPayLoad).toJSONString();
		Out.debug(getClass(), " onPlayerDeadByPlayer:", data);
		player.getXmdsManager().onMonsterDiedDrops(arenaArea.instanceId, data);
		// 扣除积分
		addScore(-(int) scoreF);
	}

	/**
	 * 红包算法
	 * 
	 * @param people
	 * @param wmoney
	 * @return
	 */
	private static int getRandomNum(int people, int wmoney) {
		if (people == 1) {
			return wmoney;
		}
		// 随机分配算法
		double min = 1;
		double max = wmoney / people * 2;

		double money = RandomUtil.randomDouble() * max;
		money = money <= min ? 1 : money;
		return (int) money;
	}

	/**
	 * 在竞技场场景GameOver事件发生时被调用
	 * 
	 * @param rankIndex 单场积分排名
	 */
	public void onAreaClose(int rankIndex) {
		// this.arenaData.usedDefTimes += 1;
		if (rankIndex == 1) {// 第一名
			this.arenaData.singleWinTimes += 1;// 单场第一次数加1
			if (this.arenaData.singleWinTimes == 1) {// 第一次获得单场第一名
				this.arenaData.firstSingleWinTime = System.currentTimeMillis();
			}
		}
		if (!ArenaService.getInstance().canDrawDayAward()) {// 不在领奖时间才计入单日排行榜
			this.arenaData.singleReward = 1;// 单场奖励可领
			// 积分榜，积分榜保留到下次活动开始之前
			ArenaService.getInstance().refreshScoreRank(this.player.getId(), this.getScore());
		}
		this.arenaData.scoreMonth += this.getScore();
		// 刷新总排行榜
		ArenaService.getInstance().refreshMonthScoreRank(this.player.getId(), this.arenaData.scoreMonth);
		resetDaily();
		this.player.updateSuperScriptList(this.getSuperScript());
	}

	/**
	 * @return 返回角标信息列表
	 */
	public List<SuperScriptType> getSuperScript() {
		ArrayList<SuperScriptType> list = new ArrayList<SuperScriptType>();
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.JJC.getValue())) {
			return list;
		}

		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.JJC_ENTER.getValue());
		data.setNumber(canEnter() == null ? 1 : 0);

		SuperScriptType.Builder data1 = SuperScriptType.newBuilder();
		data1.setType(Const.SUPERSCRIPT_TYPE.JJC_REWARD.getValue());
		data1.setNumber(0);

		if (getSingleReward() == 1) {
			data1.setNumber(data1.getNumber() + 1);
		} else if (this.arenaData.totalReward == 1 && getLastTotalRank() != 0) {
			data1.setNumber(data1.getNumber() + 1);
		}

		list.add(data.build());
		list.add(data1.build());

		return list;
	}

	/**
	 * 处理请求进入竞技场战斗场景
	 * 
	 * @param result
	 */
	public void handleEnterArenaArea(EnterArenaAreaResponse.Builder result) {
		String msg = this.canEnter();
		if (msg == null) {
			try {
				this.player.taskManager.dealTaskEvent(TaskType.JOAN_ARENA, 1);
				this.player.dailyActivityMgr.onEvent(Const.DailyType.ARENA, "0", 1);
				// TeamUtil.leaveTeamInAreaServer(this.player);// TODO 提示用户要暂离
				// 取消跟随
				Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
				if (members != null) {
					if (player.getTeamManager().isTeamLeader()) {
						for (TeamMemberData member : members.values()) {
							if (member.isFollow()) {
								TeamService.followLeader(member.getPlayer(), false);
							}
						}
					} else {
						TeamService.followLeader(this.player, false);
					}
				}
				this.enterArenaArea();
				result.setS2CCode(Const.CODE.OK);
			} catch (Exception e) {
				Out.error(getClass(), "_enterArenaArea rpc error!", e);
				result.setS2CCode(Const.CODE.FAIL);
				result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			}
		} else {
			result.setS2CCode(Const.CODE.FAIL);
			result.setS2CMsg(msg);
		}
	}

	/**
	 * 判断该玩家是否在竞技场
	 * 
	 * @param player
	 * @return
	 */
	private boolean isInArenaMap(WNPlayer player) {
		return this.isInArenaMap(player.getAreaId());
	}

	public boolean isInArenaMap(int areaId) {
		return areaId == this.mapProp.mapID;
	}

	/**
	 * 处理用户主动离开竞技场请求
	 * 
	 * @param result
	 */
	public void handleLeaveArenaArea(LeaveArenaAreaResponse.Builder result) {
		if (this.isInArenaMap(this.player)) {
			Area leaveArea = player.getArea();
			float x = 0;
			float y = 0;
			if (leaveArea != null) {
				try {
					if (leaveArea.hasPlayer(player.getId())) {
						GetPlayerData playerData = leaveArea.getPlayerData(player.getId());
						if (playerData != null) {
							x = playerData.x;
							y = playerData.y;
						}
						// 爆出玩家分数
						int score = arenaData.score;
						if (score > 0) {
							this.dropPlayerScore(player, x, y, score);
						}

						// 从排行榜中移除
						// ((ArenaArea)
						// leaveArea).removeFromScoreList(player.getId());
					}
				} catch (Exception e) {
					Out.error(e);
				}
			}

			// 回到进入竞技场原来的场景
			Area area = AreaUtil.dispatchByAreaId(this.player, this.player.getPlayerTempData().historyAreaId, this.player.getPlayerTempData().historyX, this.player.getPlayerTempData().historyY);
			// 退出时刷新原来的pk模式到战斗服
			player.getXmdsManager().refreshPlayerPKMode(this.player.getId(), this.player.pkRuleManager.pkData.pkModel);
			if (area != null) {
				result.setS2CCode(Const.CODE.OK);
			} else {
				result.setS2CCode(Const.CODE.FAIL);
				result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			}

		} else {
			result.setS2CCode(Const.CODE.FAIL);
			result.setS2CMsg(LangService.getValue("AREA_ID_NULL"));
		}
	}

	/**
	 * 处理奖励领取requestRewardType 1 ：单场 2： 全场
	 */
	public void handleGetReward(int requestRewardType, ArenaRewardResponse.Builder result) {

		if ((getSingleReward() != 1 && requestRewardType == 1) || (this.arenaData.totalReward != 1 && requestRewardType == 2)) {
			// 已领取
			result.setS2CCode(Const.CODE.FAIL);
			result.setS2CMsg(LangService.getValue("SOLO_REWARD_HAS_DRAWED"));
			// } else if (!this.checkPlayedLast()) {
			// result.setS2CCode(Const.CODE.FAIL);
			// result.setS2CMsg(LangService.getValue("SOLO_REWARD_CANNOT_DRAW"));
		} else {
			int rankIndex = 0;
			if (requestRewardType == 1) {// 单场奖励
				rankIndex = getSingleRankIndex();
			} else if (requestRewardType == 2) {// 总场奖励
				rankIndex = this.getLastTotalRank();
			}
			Out.debug("handleGetReward rankIndex: ", rankIndex);
			if (rankIndex > 0) {
				JJCRewardExt prop = null;
				for (JJCRewardExt rewExt : GameData.JJCRewards.values()) {
					if (requestRewardType == rewExt.type) {
						if ((rankIndex >= rewExt.startRank && rankIndex <= rewExt.stopRank) || rewExt.stopRank == 0) {// stopRank=0时为参与奖
							prop = rewExt;
							break;
						}
					}
				}

				Out.debug("handleGetReward prop: ", prop);
				if (prop == null) {
					result.setS2CCode(Const.CODE.FAIL);
					result.setS2CMsg(LangService.getValue("SOLO_REWARD_NOT_EXIST"));
				} else {
					List<NormalItem> items = ItemUtil.createItemsByItemCode(prop._rankReward);

					if (!this.player.getWnBag().testAddEntityItems(items, true)) {// 检测背包空间
						result.setS2CCode(Const.CODE.FAIL);
						result.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
						return;
					}
					this.player.getWnBag().addEntityItems(items, Const.GOODS_CHANGE_TYPE.arena);
					result.setS2CCode(Const.CODE.OK);
					if (requestRewardType == 1) {// 设置为已领取
						this.arenaData.singleReward = 2;
					} else if (requestRewardType == 2) {
						this.arenaData.totalReward = 2;
					}
					// this.update();// 发一个持久化数据的命令
				}
			} else {
				result.setS2CCode(Const.CODE.FAIL);
				result.setS2CMsg(LangService.getValue("SOLO_REWARD_CANNOT_DRAW"));
			}
		}
	}

	// /**
	// * add by wfy 向GDao发一个存储到数据库的请求
	// */
	// public void update() {
	// GameDao.update(ConstsTR.player_arena_dataTR, this.player.getId(),
	// arenaData);
	// }

	private static ArenaDataPO getFromRedis(String playerId) {
		return PlayerPOManager.findPO(ConstsTR.player_arena_dataTR, playerId, ArenaDataPO.class);
	}

	/**
	 * 更新场景实例ID
	 * 
	 * @param instanceId
	 */
	public void setArenaInstanceId(String instanceId) {
		this.arenaData.arenaInstanceId = instanceId;
	}

	public String getArenaInstanceId() {
		return this.arenaData.arenaInstanceId;
	}

	/**
	 * 是否在五岳一战(进入过五岳一战场景并且场景没销毁前都返回true)
	 * 
	 * @return
	 */
	public boolean isInArena() {
		String instanceId = getArenaInstanceId();
		if (StringUtil.isNotEmpty(instanceId)) {
			Area area = AreaManager.getInstance().getArea(instanceId);
			if (area != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 重置赛季奖励
	 */
	public void initSeasonAward() {
		if (arenaData.season != ArenaService.getInstance().getTerm()) {
			arenaData.totalReward = 1;
			arenaData.season = ArenaService.getInstance().getTerm();

			this.arenaData.deadMonth = 0;
			this.arenaData.killMonth = 0;
			this.arenaData.scoreMonth = 0;
			this.arenaData.singleWinTimes = 0;
			this.arenaData.firstSingleWinTime = 0;
		}
	}

}
