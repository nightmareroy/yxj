package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area.AreaItem;
import com.wanniu.game.arena.ArenaArea;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.cross.CrossServerArea;
import com.wanniu.game.cross.CrossServerLocalArea;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.RandomBoxExt;
import com.wanniu.game.data.ext.RandomBoxExt.Point;
import com.wanniu.game.fightLevel.FightLevel;
import com.wanniu.game.five2Five.Five2FiveArea;
import com.wanniu.game.guild.guildBoss.GuildBossArea;
import com.wanniu.game.guild.guildDungeon.GuildDungeon;
import com.wanniu.game.guild.guildFort.GuildFortPveArea;
import com.wanniu.game.guild.guildFort.GuildFortPvpArea;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.solo.SoloArea;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

/**
 * @author agui
 */
public class AreaManager {

	private static AreaManager instance;

	public static final Map<String, Area> CrossServerAreas = new ConcurrentHashMap<>();
	public static final Map<String, ProxyArea> ProxyServerAreas = new ConcurrentHashMap<>();

	private AreaManager() {

	}

	public static AreaManager getInstance() {
		if (instance == null) {
			instance = new AreaManager();
		}
		return instance;
	}

	private AreaMap allAreas = new AreaMap();

	public AreaMap getAreaMap() {
		return this.allAreas;
	}

	public final void init() {
		int interval = 600000;
		// 定时回收场景,需要在所有的服务器都已经启动完毕后开始
		JobFactory.addScheduleJob(() -> {
			refreshAreas();
		}, interval, interval);

		JobFactory.addScheduleJob(() -> {
			refreshAreaItems();
		}, 2 * 1000, 2 * 1000);
	}

	public void onCloseGame() {
		for (Area area : CrossServerAreas.values()) {
			try {
				area.dispose(true);
			} catch (Exception e) {
				Out.error(e);
			}
		}
		for (Area area : allAreas.values()) {
			try {
				area.dispose(true);
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

	/**
	 * 定时刷新场景信息
	 */
	public final void refreshAreas() {
		// Out.debug("--------------------------before clear
		// area----------------------------");
		// for (Map.Entry<String, Area> node : areas.entrySet()) {
		// Area area = node.getValue();
		// Out.debug("areaId:" + area.areaId + " instanceId:" + area.instanceId + "
		// playerCount:" + area.getPlayerNum());
		// }

		Out.debug("--------------------------start clear area----------------------------");
		for (Map.Entry<String, Area> node : allAreas.entrySet()) {
			refreshAreaStatu(node.getKey());
		}
		Out.debug("--------------------------after clear area----------------------------");

		// for (Map.Entry<String, Area> node : areas.entrySet()) {
		// Area area = node.getValue();
		// Out.debug("areaId:" + area.areaId + " instanceId:" + area.instanceId + "
		// playerCount:" + area.getPlayerNum());
		// }
		// Out.debug("--------------------------end clear
		// area----------------------------");
	}

	/**
	 * 定时刷新场景物品信息
	 */
	public final void refreshAreaItems() {
		long now = System.currentTimeMillis();
		int leftTime = GlobalConfig.itemdrop_lock_lifeTime;
		Area area = null;
		AreaItem areaItem = null;
		for (Map.Entry<String, Area> node : allAreas.entrySet()) {
			area = node.getValue();
			for (Map.Entry<String, AreaItem> entry : area.items.entrySet()) {
				areaItem = entry.getValue();
				if (now >= (areaItem.createTime + leftTime)) {
					if (area.onCleanItem(areaItem)) {
						area.items.remove(entry.getKey());
					}
				}
			}
		}
	}

	public final Area getArea(String instanceId) {
		Area area = allAreas.get(instanceId);
		if (area == null) {
			area = CrossServerAreas.get(instanceId);
		}
		if (area == null) {
			area = ProxyServerAreas.get(instanceId);
		}
		return area;
	}

	/**
	 * 刷新场景状态
	 */
	public final void refreshAreaStatu(String instanceId) {
		Area area = allAreas.get(instanceId);
		if (area != null) {
			boolean flag = area.isValid();
			if (!flag) {
				closeArea(instanceId);
			}
		}
	}

	/**
	 * 无人状态下关闭场景
	 */
	public final void closeAreaNoPlayer(String instanceId) {
		Area area = allAreas.get(instanceId);
		if (area != null) {
			if (area.canCloseNoPlayer()) {
				closeArea(instanceId);
			}
		}
	}

	/**
	 * 强制关闭场景
	 */
	public final void closeArea(String instanceId) {
		Area area = getArea(instanceId);
		if (area != null) {
			try {
				area.dispose();
			} catch (Exception e) {
				Out.error(e);
			} finally {
				if (allAreas.remove(instanceId) == null) {
					CrossServerAreas.remove(instanceId);
				}
			}
			Out.debug("closeArea instanceId:", area.prop.name, " :", instanceId);
		} else {
			Out.warn("more closeArea instanceId:", instanceId);
		}
	}

	public final Area dispatchByAreaId(WNPlayer player, JSONObject playerInfo) {
		int addCount = 1;
		TeamMemberData member = player.getTeamManager().getTeamMember();
		if (member != null && member.isLeader) {
			addCount = player.getTeamManager().followCount();
		}
		Area area = allAreas.dispactch(playerInfo.getIntValue("areaId"), playerInfo.getIntValue("logicServerId"), addCount);
		if (area == null) {
			return createArea(player, playerInfo);
		}
		return area;
	}


	/**
	 * 创建场景
	 * 
	 * @return {result: success, id: instanceId, fullCount: fullAllowedNum,
	 *         curCount: 0, maxCount: maxAllowedNum, createTime: Date.now()};
	 */
	public final Area createArea(WNPlayer player, JSONObject data) {
		Out.debug("createArea areaData:", data);
		// 该场景角色白名单
		int areaId = data.getIntValue("areaId");
		MapBase prop = AreaUtil.getAreaProp(areaId);
		if (prop != null) {
			String instanceId = UUID.randomUUID().toString();
			boolean usespaceDiv = false;
			if (prop.type == Const.SCENE_TYPE.NORMAL.getValue() || prop.type == Const.SCENE_TYPE.ILLUSION.getValue() || prop.type == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
				usespaceDiv = true;
			}
			JSONObject enterData = new JSONObject();
			enterData.put("monsterHard", prop.monsterHard);
			enterData.put("calPKValue", prop.ignorePkRule == 0);
			enterData.put("allowAutoGuard", prop.autoFight == 1);
			enterData.put("usespaceDiv", usespaceDiv);
			enterData.put("sceneType", prop.type);
			enterData.put("canRiding", prop.rideMount == 1);

			if (prop.type == SCENE_TYPE.LOOP.getValue()) {
				TeamData team = player.getTeamManager().getTeam();
				int averageLevel = TaskUtils.getAvgLevel(team);
				enterData.put("averageLevel", averageLevel);
				enterData.put("floorRatio", 1);
				enterData.put("propRatio", GlobalConfig.Loop_Hard_Rate);
			} else if (prop.type == Const.SCENE_TYPE.RESOURCE_DUNGEON.getValue()) {
				enterData.put("averageLevel", player.getLevel());
				enterData.put("floorRatio", 1);
				enterData.put("propRatio", GlobalConfig.Fate_Resource_Rate);
			} else if (prop.type == Const.SCENE_TYPE.DEMON_TOWER.getValue()) {
				enterData.put("averageLevel", 1);
				enterData.put("floorRatio", data.getIntValue("lv"));
				enterData.put("propRatio", GlobalConfig.PropRatio);
			} else if (prop.type == Const.SCENE_TYPE.GUILD_BOSS.getValue()) {
				enterData.put("averageLevel", data.getIntValue("lv"));
				enterData.put("floorRatio", 0);
				enterData.put("propRatio", 1);
			}else {
				enterData.put("averageLevel", 0);
			}
			
			Out.debug("createArea instanceId:", instanceId, " templateId:", prop.templateID, " enterData:", enterData);

			if (prop.type != Const.SCENE_TYPE.CROSS_SERVER.getValue() || !GConfig.getInstance().isEnableProxy()) {
				player.setBattleServerId(GWorld.__CS_NODE.getNodeId());
			}

			player.getZoneManager().createZoneRequest(player.getBattleServerId(), prop.templateID, instanceId, enterData.toJSONString());

			int fullAllowedNum = prop.allowedPlayers;
			int maxAllowedNum = prop.allowedPlayers;
			data.put("instanceId", instanceId);
			Area area = null;
			if (prop.type == Const.SCENE_TYPE.NORMAL.getValue()) {
				area = new Area(data);
				fullAllowedNum = prop.fullPlayers;
				maxAllowedNum = prop.maxPlayers;
			} else if (prop.type == Const.SCENE_TYPE.FIGHT_LEVEL.getValue()) {
				area = new FightLevel(data, Const.SCENE_TYPE.FIGHT_LEVEL);
			} else if (prop.type == Const.SCENE_TYPE.FIGHT_LEVEL_ULTRA.getValue()){
				area = new FightLevel(data, Const.SCENE_TYPE.FIGHT_LEVEL_ULTRA);
			} else if (prop.type == Const.SCENE_TYPE.LOOP.getValue()) {
				area = new FightLevel(data, Const.SCENE_TYPE.LOOP);
			} else if (prop.type == Const.SCENE_TYPE.RESOURCE_DUNGEON.getValue()) {
				area = new ResourceDungeon(data);
			} else if (prop.type == Const.SCENE_TYPE.DEMON_TOWER.getValue()) {
				area = new DemonTower(data);
			} else if (prop.type == Const.SCENE_TYPE.SIN_COM.getValue()) {// 单挑王
				area = new SoloArea(data);
			} else if (prop.type == Const.SCENE_TYPE.ARENA.getValue()) {
				area = new ArenaArea(data);
			} else if (prop.type == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
				area = GConfig.getInstance().isEnableProxy() ? new CrossServerArea(data) : new CrossServerLocalArea(data);
				fullAllowedNum = prop.fullPlayers;
				maxAllowedNum = prop.maxPlayers;
			} else if (prop.type == Const.SCENE_TYPE.GUILD_DUNGEON.getValue()) {
				area = new GuildDungeon(data);
			} else if (prop.type == Const.SCENE_TYPE.WORLD_BOSS.getValue()) {
				// TODO WORLD_BOSS
				// data.index = areaData.index;
				// area = new WorldBoss(data);
			} else if (prop.type == Const.SCENE_TYPE.GUILD_BOSS.getValue()) {
				GuildPO guildPo = player.guildManager.guild;
				area = new GuildBossArea(data, guildPo);
			} else if (prop.type == Const.SCENE_TYPE.ILLUSION.getValue()) {
				area = new IllusionArea(data);
				fullAllowedNum = prop.fullPlayers;
				maxAllowedNum = prop.maxPlayers;
			} else if (prop.type == Const.SCENE_TYPE.FIVE2FIVE.getValue()) {
				area = new Five2FiveArea(data);
			} else if (prop.type == Const.SCENE_TYPE.ILLUSION_2.getValue()) {
				area = new Illusion2Area(data);
				fullAllowedNum = prop.fullPlayers;
				maxAllowedNum = prop.maxPlayers;
			}else if (prop.type == Const.SCENE_TYPE.GUILD_FORT_PVE.getValue()) {
				area = new GuildFortPveArea(data);
			}else if (prop.type == Const.SCENE_TYPE.GUILD_FORT_PVP.getValue()) {
				area = new GuildFortPvpArea(data);
			}
			if (area != null) {
				Out.info("create area id :", areaId, " instanceId:", instanceId);
				if (prop.type != Const.SCENE_TYPE.CROSS_SERVER.getValue() || !GConfig.getInstance().isEnableProxy()) {
					allAreas.put(instanceId, area);
				} else {
					CrossServerAreas.put(instanceId, area);
				}
				area.bindBattleServer(player);
				area.fullCount = fullAllowedNum;
				area.maxCount = maxAllowedNum;
				return area;
			} else {
				Out.error("createZone:", areaId, " fail! there is no area type of this,type:", prop.type);
			}
		} else {
			Out.error("areaProp is null! mapId:", areaId);
		}
		return null;
	}
	
	
	private static void onPlayerEvent(String playerId, EventType type, Object... param) {
		WNPlayer player = GWorld.getInstance().getPlayer(playerId);
		if (player == null) {
			return;
		}
		player.onEvent(new TaskEvent(type, param));
	}
	

	/*****************************************
	 * area related battleServerEvent
	 *****************************************/
	public final void areaBattleServerEvent(JSONObject result) {
		Out.debug("areaBattleServerEvent:", result);
		String instanceId = result.getString("instanceId");
		Area area = AreaUtil.getArea(instanceId);
		if (area != null) {
			switch (result.getString("eventName")) {
				case "unitDead": {
					AreaEvent.unitDeadEventB2R(area, result);
					return;
				}
				case "message": {
					AreaEvent.messageEventB2R(area, result);
					return;
				}
				case "gameOver": {
					AreaEvent.gameOverEventB2R(area, result);
					return;
				}
				case "pickItem": {
					AreaEvent.pickItemEventB2R(area, result);
					return;
				}
				case "BattleReportEventB2R": {
					AreaEvent.battleReportEventB2R(area, result);
					return;
				}
				case "KillBossEventB2R": {
					AreaEvent.killBossEventB2R(area, result);
					return;
				}
				default: {
					Out.error("area event: ", result);
				}
			}
		}
	}


	/*****************************************
	 * player related battleServerEvent
	 *****************************************/
	public final void playerBattleServerEvent(JSONObject json) {
		Out.debug("playerBattleServerEvent msg:", json);
		String eventName = json.getString("eventName");
		switch (eventName) {
		case "ConsumeItemEventB2R": {
			int qty = json.getIntValue("Qty");
			if (qty > 0) {
				onPlayerEvent(json.getString("playerId"), EventType.consumeItem, json.getString("Type"), qty);
			}
			return;
		}
		case "interActiveItem": {
			if (json.containsKey("type")) {
				int type = json.getIntValue("type");
				if (type == 3) { // tc
					WNPlayer player = PlayerUtil.getOnlinePlayer(json.getString("playerId"));
					if (player != null && player.getArea() != null) {
						int objId = json.getIntValue("objId");
						int itemId = json.getIntValue("itemId");
						player.getArea().onInterActiveItem(player, objId, itemId);
					}
					return;
				}
			}
			onPlayerEvent(json.getString("playerId"), EventType.interActiveItem, json.getIntValue("itemId"), 1);
			return;
		}
		case "changeSceneProgress": {
			onPlayerEvent(json.getString("playerId"), EventType.changeSceneProgress, json.getString("key"), json.get("value"));
			return;
		}
		case "TransUnitEventB2R": {
			JobFactory.addDelayJob(() -> {
				onPlayerEvent(json.getString("playerId"), EventType.changeArea, json.getIntValue("SceneID"), json.getIntValue("targetX"), json.getIntValue("targetY"));
			}, 0);
			return;
		}
		case "SummonMountEventB2R": {
			boolean isUp = json.getBooleanValue("IsSummonMount");
			onPlayerEvent(json.getString("playerId"), EventType.summonMount, isUp, 1);
			return;
		}
		case "ShowRebirthDialogueB2R": {
			WNPlayer player = GWorld.getInstance().getPlayer(json.getString("DeadUnitUUID"));
			WNPlayer saverPlayer = GWorld.getInstance().getPlayer(json.getString("SaverUUID"));
			if (player != null && saverPlayer != null) {
				String saverName = MessageUtil.getPlayerNameColor(saverPlayer.getName(), saverPlayer.getPlayer().pro);
				player.onEvent(new TaskEvent(EventType.rebirth, 1, saverName, json.getIntValue("HP"), json.getIntValue("MP")));
			}
			return;
		}
		case "TriggerSceneEventB2R": {
			Out.debug("TriggerSceneEventB2R", json.get("playerId"));
			WNPlayer player = PlayerUtil.getOnlinePlayer(json.getString("playerId"));
			if (player != null) {
				player.onBatterServerSceneEvent(json.getString("EventID"));
			}
			return;
		}
		case "PlayerExceptionEventB2R": {
			WNPlayer player = GWorld.getInstance().getPlayer(json.getString("playerId"));
			if (player != null) {
				Out.warn(player.getName(), "使用外挂 :", json.getString("reason"));
				PlayerUtil.sendSysMessageToPlayer("你竟然使用外挂了！！！", json.getString("playerId"), null);
			}
			return;
		}
		}
	}

	/*****************************************
	 * task related battleServerEvent
	 *****************************************/
	public final void taskBattleServerEvent(JSONObject msg) {
		Out.debug("taskBattleServerEvent:", msg);
		String playerId = msg.getString("playerId");
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			String eventName = msg.getString("eventName");
			int templateId = Integer.parseInt(msg.getString("id"));
			String key = msg.getString("key");
			int value = msg.getIntValue("value");
			player.taskManager.onTaskRequestEvent(eventName, templateId, key, value);
		}
	}

}
