package com.wanniu.game.fightLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.Illusion2Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.HandsUpState;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.CircleSceneCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MonsterRefreshCO;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItem;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.PathService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.request.fightLevel.GetBossDamageRankHandler.GetBossDamageRankResult;
import com.wanniu.game.request.fightLevel.GetMonsterLeaderHandler.GetMonsterLeaderData;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamUtil;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.FightLevelHandler.CloseHandUpPush;
import pomelo.area.FightLevelHandler.MonsterInfo;
import pomelo.area.FightLevelHandler.OnMemberEnterFubenStateChangePush;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 副本管理器
 * 
 * @author agui
 *
 */
public class FightLevelManager {

	public WNPlayer player;

	private FightLevelsPO po;

	public FightLevelsPO getFightLevelsPo() {
		return this.po;
	}

	public FightLevelManager(WNPlayer player, FightLevelsPO po) {
		this.player = player;
		if (po == null) {
			po = new FightLevelsPO();
		}
		if (po.todays == null) {
			po.todays = new HashMap<>();
		}
		if (po.finishes == null) {
			po.finishes = new HashMap<>();
		}
		if (po.resourceDungeon == null) {
			po.resourceDungeon = new HashMap<>();
		}
		this.po = po;

		PlayerPOManager.put(ConstsTR.player_fightlevelTR, player.getId(), po);
	};

	public int getTodayFinish(int templateID) {
		return po.todays.containsKey(templateID) ? po.todays.get(templateID) : 0;
	}

	public int getTodayBuy(int templateID) {
		return po.buys.getOrDefault(templateID, 0);
	}

	public int getCurrHard(int templateID) {
		return po.finishes.containsKey(templateID) ? po.finishes.get(templateID) : 1;
	}

	public void useProduce(int templateID) {
		if(!needProduce(templateID)) {//如果该次副本通关没有收益，就不增加已完成的有收益副本次数
			Out.info("needProduce false,player id:" + this.player.getId() + " mapId:" + templateID);
			return;
		}
		Map<Integer, Integer> todays = po.todays;
		int count = getTodayFinish(templateID);
		synchronized (po.todays) {
			todays.put(templateID, count + 1);
		}
	}

	public boolean needProduce(int templateID) {
		return getTodayFinish(templateID) < Const.FB_PRODUCE_COUNT + getTodayBuy(templateID);
	}

	public void refreshNewDay() {
		synchronized (po.todays) {
			this.po.todays.clear();
			this.po.buys.clear();

			// 资源副本每日重置
			this.po.resourceDungeon.clear();
			for (Map<Integer, Integer> val : this.po.dropedBossMap.values()) {// 清除所有副本的boss掉落计数
				val.clear();
			}
		}
	}

	
	/**
	 * 是否为掉落控制场景
	 * @param area
	 * @return 是返回true
	 */
	private boolean isDropedControledArea(Area area) {
		if(area.sceneType==Const.SCENE_TYPE.FIGHT_LEVEL.getValue()
				|| area.sceneType==Const.SCENE_TYPE.LOOP.getValue()) {//副本皓月镜等多人组队情况下要控制
			return true;
		}
		
		return false;
	}
	
	/**
	 * 当副本通关或重置后
	 */
	public void onDungeonReset(int areaId) {
		Map<Integer, Integer> val = this.po.dropedBossMap.get(areaId);
		if (val != null) {
			val.clear();
		}
	}

	/**
	 * 给定的bossId是否可以掉落装备
	 * 
	 * @param bossId
	 * @return
	 */
	public boolean canDrop(int bossId, Area area) {
		if (bossId == 0 || !isDropedControledArea(area)) {
			return true;
		}

		Map<Integer, Integer> val = this.po.dropedBossMap.get(area.areaId);
		if (val != null) {
			if (val.containsKey(bossId)) {
				int times = val.get(bossId);
				if (times > 0) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 当boss死亡获得收益时
	 * 
	 * @param bossId
	 */
	public void onBossDead(int bossId, Area area) {
		if(!isDropedControledArea(area)) {
			return;
		}
		MonsterBase prop = MonsterConfig.getInstance().get(bossId);
		if (prop == null || prop.type < 3) {// 普通小怪不记录
			return;
		}

		Map<Integer, Integer> val = this.po.dropedBossMap.get(area.areaId);
		if (val != null) {
			if (val.containsKey(bossId)) {
				val.put(bossId, val.get(bossId) + 1);
			} else {
				val.put(bossId, 1);
			}
		} else {
			Map<Integer, Integer> newVal = new ConcurrentHashMap<>();
			newVal.put(bossId, 1);
			this.po.dropedBossMap.put(area.areaId, newVal);
		}
	}

	public List<MonsterInfo> getMonsterInfo(Map<Integer, Map<Integer, Integer>> datas, int areaId) {

		List<MonsterInfo> result = new ArrayList<>();

		Map<Integer, Integer> data = datas.get(areaId);

		List<MonsterRefreshExt> props = GameData.findMonsterRefreshs(t -> {
			return t.mapID == areaId;
		});

		for (MonsterRefreshCO prop : props) {
			MonsterInfo.Builder m = MonsterInfo.newBuilder();
			m.setMonsterId(prop.monsterID);
			if (data != null && data.get(prop.iD) != null && data.get(prop.iD) != 0) {
				m.setTime(data.get(prop.iD));
			} else {
				m.setTime(data.get(0));
			}
			result.add(m.build());
		}

		return result;
	}

	public int vipAddTimes(WNPlayer player, int type) {
		switch (type) {
		// case 1:
		// return player.vipManager.getVipFunc(VipFuncType.SINGLE_SCENE);
		// case 2:
		// return player.vipManager.getVipFunc(VipFuncType.TEAM_SCENE);
		// case 3:
		// return player.vipManager.getVipFunc(VipFuncType.SECRET_SCENE);
		// case 4:
		// return player.vipManager.getVipFunc(VipFuncType.SUPER_SCENE);

		default:
			return 0;
		}
	}

	public List<MiniItem> getDropItems(String itemTC) {

		List<MiniItem> dropItems = new ArrayList<>();

		List<NormalItem> items = ItemUtil.createItemsByTcCode(itemTC);
		for (NormalItem item : items) {

			MiniItem.Builder dropItem = ItemUtil.getMiniItemData(item.prop.code, 1);

			if (dropItem != null) {
				dropItems.add(dropItem.build());
			}
		}

		return dropItems;
	}

	public String enterDungeonReq(WNPlayer player, int dungeonId) {
		MapBase prop = AreaDataConfig.getInstance().get(dungeonId);
		String data = this.isDungeonOpen(prop);
		if (data != null) {
			return data;
		}
		data = this.canEnterDungeon(player, prop, true);
		if (data != null) {
			return data;
		}

		// if (prop.dungeonTab == 1 || prop.dungeonTab == 4) { // 单人副本或秘境
		// int state =
		// DungeonService.getInstance().getHandsUpStateByPlayerId(player.getLogicServerId(),
		// player.getId());
		// if (state != 0) {
		// if (state == HandsUpState.HANDS_UP_WAITING.value || state ==
		// HandsUpState.HANDS_UP_ACCEPT.value) {
		// // 排队状态下不能进入
		// return LangService.getValue("DUNGEON_SINGLE_IN_TEAM");
		// }
		// }
		// }

		if ((prop.allowedPlayersMix > 1 || player.getTeamManager().isInTeam()) && !PlayerUtil.isRobot(player.player)) {
			int teamCount = 0;
			TeamMemberData teamMember = player.getTeamManager().getTeamMember();
			TeamData team = player.getTeamManager().getTeam();
			if (teamMember != null && team != null) {
				teamCount = team.memberCount();
				if (!teamMember.isLeader) {
					return LangService.getValue(prop.allowedPlayersMax == 1 ? "DUNGEON_SINGLE_MORE" : "TEAM_NO_AUTHORITY");
				}
			}
			if (teamCount < prop.allowedPlayersMix || teamCount > prop.allowedPlayersMax) {
				return LangService.getValue(teamCount < prop.allowedPlayersMix ? "TEAM_MEMBER_COUNT" : prop.allowedPlayersMax == 1 ? "DUNGEON_SINGLE_MORE" : "DUNGEON_TEAM_PLAYER_MORE");
			}
			if (teamCount > 1) {
				if (team.confirm) {
					return DungeonService.getInstance().enterDungeonInTeam(team, prop, dungeonId);
				}
				if (dungeonId == Illusion2Area.DEFAULT_ID) {
					AreaUtil.dispatchByAreaId(player, dungeonId, null);
				} else {
					this.enterDungeonMutiPlayers(team, dungeonId);
				}

				return null;
			}
		}
		if (dungeonId == Illusion2Area.DEFAULT_ID) {
			AreaUtil.dispatchByAreaId(player, dungeonId, null);
		} else {
			this.enterDungeon(player, dungeonId);
		}
		return null;
	};

	public String canEnterDungeon(WNPlayer player, MapBase prop, boolean bAlone) {
		Area area = player.getArea();

		if (area != null && !area.isNormal()) {
			return bAlone ? LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON") : LangService.format("TEAM_MEMBER_BATTLE", player.getName());
		}

		return AreaUtil.canEnterArea(prop, player);
	};

	public String replyEnterDungeon(WNPlayer player, int type, int dungeonId) {
		if (player.isRomote()) {
			ProxyClient.getInstance().add(new Message() {
				@Override
				protected void write() throws IOException {
					body.writeByte(3);
					body.writeString(player.getId());
					body.writeByte(type);
				}

				@Override
				public short getType() {
					return ProxyType.TEAM;
				}
			});
			return null;
		}
		if (type != HandsUpState.ACCEPT.value && type != HandsUpState.REFUSE.value) {
			return LangService.getValue("DATA_ERR");
		}

		TeamMemberData teamMember = player.getTeamManager().getTeamMember();
		if (teamMember == null) {
			return LangService.getValue("EXPIRED_MSG");
		}

		TeamData team = player.getTeamManager().getTeam();
		if (team == null || !team.teamMembers.containsKey(player.getId()) || !team.islock()) {
			return LangService.getValue("EXPIRED_MSG");
		}
		if (type == HandsUpState.REFUSE.value && teamMember.isLeader) {
			CloseHandUpPush push = CloseHandUpPush.newBuilder().setMsg(LangService.getValue("TEAM_GOTO_CANCEL")).build();
			for (TeamMemberData member : team.teamMembers.values()) {
				member.handup = HandsUpState.WAITING.value;
				if (!member.id.equals(player.getId())) {
					MessageUtil.sendMessage(member.id, "area.fightLevelPush.closeHandUpPush", push);
				}
			}
			team.unlock();
			return null;
		}

		String data = this.isDungeonOpen(dungeonId);
		if (data != null) {
			return data;
		}
		Area area = player.getArea();
		if (area != null && AreaUtil.needCreateArea(area.areaId)) {
			return LangService.getValue("PLAYER_CANT_DO");
		}

		teamMember.handup = type;

		boolean allAccept = true, allReply = true;
		OnMemberEnterFubenStateChangePush msgData = OnMemberEnterFubenStateChangePush.newBuilder().setS2CPlayerId(player.getId()).setS2CIsReady(type == HandsUpState.ACCEPT.value ? 1 : 0).build();
		for (TeamMemberData member : team.teamMembers.values()) {
			allAccept = allAccept && (member.handup == HandsUpState.ACCEPT.value);
			allReply = allReply && (member.handup != HandsUpState.WAITING.value);
			MessageUtil.sendMessage(member.id, "area.fightLevelPush.onMemberEnterFubenStateChangePush", msgData);
		}

		if (allReply) {
			team.unlock();
		}

		if (allAccept) {
			if (dungeonId == Illusion2Area.DEFAULT_ID) {
				AreaUtil.dispatch(player, team.teamMembers.keySet(), team.logicServerId, dungeonId, null);
			} else {
				this.enterDungeonMutiPlayers(team, dungeonId);
			}
		}
		return null;
	};

	public void enterDungeon(WNPlayer player, int mapId) {
		AreaUtil.createAreaAndDispatch(player, Arrays.asList(player.getId()), player.getLogicServerId(), mapId, null);
	};

	public void enterDungeonMutiPlayers(TeamData team, int mapId) {
		AreaUtil.createAreaAndDispatch(player, team.teamMembers.keySet(), team.logicServerId, mapId, null);
	};

	public String isDungeonOpen(int mpaId) {
		return isDungeonOpen(AreaUtil.getAreaProp(mpaId));
	}

	public String isDungeonOpen(MapBase prop) {
		if (prop == null) {
			return LangService.getValue("DUNGEON_NULL");
		}
		// 副本的难度等级已经去除，只有单人和组队模式
		// if (prop.hardModel > getCurrHard(prop.templateID)) {
		// return LangService.getValue("DUNGEON_NOT_OPEN");
		// }
		long currTime = GWorld.APP_TIME;
		Date beginTime = null;
		if (StringUtil.isNotEmpty(prop.beginTime)) {
			beginTime = AreaUtil.formatToday(prop.beginTime);
		}
		Date endTime = null;
		if (StringUtil.isNotEmpty(prop.endTime)) {
			endTime = AreaUtil.formatToday(prop.endTime);
		}
		if ((beginTime != null && currTime < beginTime.getTime()) || (endTime != null && currTime > endTime.getTime())) {
			return LangService.getValue("DUNGEON_TEAM_NOT_OPEN");
		}

		if (prop.openRule == Const.OpenRuleType.EVERY_WEEK.getValue()) {
			Calendar calendar_curr = Calendar.getInstance();
			int pos = prop.OpenDate.indexOf(calendar_curr.get(Calendar.DAY_OF_WEEK));
			if (-1 == pos) {
				return LangService.getValue("DUNGEON_TEAM_NOT_OPEN");
			}
		}
		return null;
	}

	public String leaveDungeon(WNPlayer player, Area area) {
		TeamUtil.removeAcrossMatch(player);
		int targetAreaId = player.playerTempData.historyAreaId;
		if (area.prop.leaveToMapID != 0) {
			targetAreaId = area.prop.leaveToMapID;
		}
		AreaData areaData = new AreaData(targetAreaId);
		if (targetAreaId == player.playerTempData.historyAreaId) {
			areaData.targetX = player.playerTempData.historyX;
			areaData.targetY = player.playerTempData.historyY;
		}
		float[] xy = PathService.findToAreaXYByAreaId(area.areaId, targetAreaId);
		if (xy != null) {
			areaData.targetX = xy[0];
			areaData.targetY = xy[1];
		}
		Out.debug(player.getName(), " ==leaveDungeon== ", area.getSceneName(), " to ", areaData);
		// 在做一条龙任务从副本中出来，位置不要回到 触发点
		TeamData team = player.getTeamManager().getTeam();
		TeamMemberData member = player.getTeamManager().getTeamMember();
		if (member != null) {
			member.follow = true;
		}
		if (team != null && team.loopTasks != null) {
			boolean quit = false;
			for (TaskPO taskData : team.loopTasks.values()) {
				if (taskData == null)
					continue;
				TaskBase taskProp = GameData.CircleScenes.get(taskData.templateId);
				if (taskProp == null)
					continue;
				if (taskProp.circleDungeonID == area.areaId) {
					for (CircleSceneCO posProp : GameData.CircleScenes.values()) {
						if (posProp.circleDungeonID == area.areaId) {
							if (player.playerTempData.historyAreaId == posProp.startScene) {
								areaData.targetX = posProp.loopOutPos[0];
								areaData.targetY = posProp.loopOutPos[1];
							}
							quit = true;
							break;
						}
					}
					if (quit) {
						// 恢复一条龙队伍的自动开战模式
						team.confirm = true;
						break;
					}
				}
			}
		}
		MapBase prop = AreaDataConfig.getInstance().get(targetAreaId);
		area = prop.type == SCENE_TYPE.CROSS_SERVER.getValue() ? AreaUtil.dispatchByCrossServerId(player, areaData) : AreaUtil.dispatchByAreaId(player, areaData, null);

		return area != null ? null : LangService.getValue("SOMETHING_ERR");
	};

	public Object enterWaitDungeon(WNPlayer player, int dungeonId) {
		MapBase prop = AreaUtil.getAreaProp(dungeonId);

		String data = this.isDungeonOpen(prop);
		if (data != null) {
			return data;
		}

		data = this.canEnterDungeon(player, prop, true);

		if (data != null) {
			return data;
		}

		if (prop == null || prop.dungeonTab != 2) {
			return LangService.getValue("PARAM_ERROR");
		}

		TeamMemberData teamMember = player.getTeamManager().getTeamMember();

		if (teamMember != null) {
			return LangService.getValue("MATCH_IN_TEAM");
		}

		return null;
	};

	public boolean addStackItem(String code, int worth, List<NormalItem> awardDropItems) {

		for (NormalItem awardDropItem : awardDropItems) {

			if (awardDropItem.itemDb.code.equals(code)) {

				((VirtualItem) awardDropItem).addWorth(worth);

				return true;
			}
		}
		return false;
	}

	public int addStackItemByCount(String code, int groupCount, int groupCountMax, List<NormalItem> awardDropItems) {

		for (NormalItem awardDropItem : awardDropItems) {

			if (awardDropItem.itemDb.code.equals(code)) {

				// awardDropItem.itemDb.groupCount += groupCount;
				awardDropItem.addGroupNum(groupCount);

				int leftCount = awardDropItem.itemDb.groupCount - groupCountMax;

				if (leftCount > 0) {

					// awardDropItem.itemDb.groupCount = groupCountMax;
					awardDropItem.setNum(groupCountMax);

					return leftCount;
				} else {

					return 0;
				}
			}
		}

		return groupCount;
	}

	public GetMonsterLeaderData getMonsterLeader(WNPlayer player, int monsterId, int areaId) {
		List<MiniItem> dropItems = new ArrayList<>();
		GetMonsterLeaderData data = new GetMonsterLeaderData(dropItems, 0, 0, "");

		MonsterBase monsterProp = MonsterConfig.getInstance().get(monsterId);

		if (monsterProp != null) {

			data.dropItems = this.getDropItems(monsterProp.showTc);
		}

		MapBase areaProp = AreaDataConfig.getInstance().get(areaId);

		if (areaProp != null) {

			data.reqLevel = areaProp.reqLevel;
			data.reqUpLevel = areaProp.reqUpLevel;
		}
		MonsterRefreshCO refreshProp = null;
		List<MonsterRefreshExt> refreshProps = GameData.findMonsterRefreshs(t -> {
			return t.monsterID == monsterId && t.mapID == areaId;
		});
		if (refreshProps.size() > 0) {
			refreshProp = refreshProps.get(0);
			data.refreshPoint = refreshProp.refreshPoint;
		}

		return data;
	}

	/**
	 * 这个功能JS 未实现
	 * 
	 * @return
	 */
	public GetBossDamageRankResult getBossDamageRank(WNPlayer player, Area area) {

		GetBossDamageRankResult data = new GetBossDamageRankResult(new ArrayList<>(), 0, 0);

		return data;
	}

	public void addPrifit(int mapId) {
		Out.info("增加副本收益次数 mapId=", mapId);
		po.buys.put(mapId, po.buys.getOrDefault(mapId, 0) + 1);		
	};
}