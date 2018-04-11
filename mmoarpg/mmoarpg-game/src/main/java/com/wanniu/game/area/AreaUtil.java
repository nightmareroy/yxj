package com.wanniu.game.area;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType.ProxyMethod;
import com.wanniu.core.util.DateUtil;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.csharp.CSharpNode;
import com.wanniu.game.GWorld;
import com.wanniu.game.arena.ArenaService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.cross.CrossServerArea;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.PlayerHandler.ChangeAreaPush;

/**
 * @author agui
 */
public class AreaUtil {

	/** 最小切场景的时间间隔 */
	public static final long MIN_CHANGE_AREA_INTERVAL_MILL = 1000;
	private static AreaUtil instance;

	public static AreaUtil getInstance() {
		if (instance == null) {
			instance = new AreaUtil();
		}
		return instance;
	}

	/**
	 * 是否可以传送到目标场景
	 */
	public static final String canTransArea(MapBase sceneProp, WNPlayer player) {
		if (sceneProp == null) {
			return LangService.getValue("AREA_ID_NULL");
		}
		if (sceneProp.allowedTransfer == 0) {
			return LangService.getValue("AREA_NOT_TRANSFER");
		}
		// 检查是否能进入该场景
		return canEnterArea(sceneProp, player);
	}

	/**
	 * 扣除传送场景所需道具
	 */
	public static final boolean disCardItemByTransArea(MapBase sceneProp, WNPlayer player) {
		String transItemCode = sceneProp.costItem;
		int needTransItemNum = sceneProp.costItemNum;
		if (transItemCode.length() > 0) {
			int itemNum = player.getWnBag().findItemNumByCode(transItemCode);
			if (itemNum < needTransItemNum) {
				player.onFunctionGoTo(Const.FUNCTION_GOTO_TYPE.DIAMONDSHOP, transItemCode, null, null);
				return false;
			}
			player.getWnBag().discardItem(transItemCode, needTransItemNum, Const.GOODS_CHANGE_TYPE.transport, null, false, false);
		}
		return true;
	}

	/**
	 * 判断能否进入相应场景
	 */
	public static final String canEnterArea(MapBase sceneProp, WNPlayer player) {

		if (sceneProp == null) {
			return LangService.getValue("AREA_ID_NULL");
		}

		if (GWorld.APP_TIME - player.getLastChangeAreaTime() < MIN_CHANGE_AREA_INTERVAL_MILL) {
			Out.error(player.getName(), " changeArea too rapid error : ", player.area.getSceneName(), "-", player.area.instanceId, " to ", sceneProp.name, " use ", GWorld.APP_TIME - player.getLastChangeAreaTime(), "ms use ", JSON.toJSONString(player.playerTempData));
			return LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON");
		}

		if (!player.getArea().isNormal()) {
			return LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON");
		}

		if (sceneProp.openRule == Const.OpenRuleType.EVERY_DAY.getValue()) {
			long currTime = System.currentTimeMillis();
			Date beginTime = DateUtil.format(sceneProp.beginTime);
			Date endTime = DateUtil.format(sceneProp.endTime);
			if ((sceneProp.beginTime != null && currTime < beginTime.getTime()) || (sceneProp.endTime != null && currTime > endTime.getTime())) {
				return LangService.getValue("DUNGEON_TEAM_NOT_OPEN");
			}
		} else if (sceneProp.openRule == Const.OpenRuleType.EVERY_WEEK.getValue()) {
			int pos = sceneProp.OpenDate.indexOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
			if (-1 == pos) {
				return LangService.getValue("DUNGEON_TEAM_NOT_OPEN");
			}
			long currTime = System.currentTimeMillis();
			Date beginTime = DateUtil.format(sceneProp.beginTime);
			Date endTime = DateUtil.format(sceneProp.endTime);
			if ((sceneProp.beginTime != null && currTime < beginTime.getTime()) || (sceneProp.endTime != null && currTime > endTime.getTime())) {
				return LangService.getValue("DUNGEON_TEAM_NOT_OPEN");
			}
		}
		if (player.getLevel() < sceneProp.reqLevel) {
			return LangService.getValue("AREA_PLAYER_LEVEL_LIMIT").replace("{playerLevel}", LangService.getValue("RED").replace("{a}", "" + sceneProp.reqLevel));
		}

		// 判断组队情况队员等级
		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
		if (members != null && player.getTeamManager().isTeamLeader() && sceneProp.mapID != ArenaService.ARENA_MAP_ID // 竞技场内独立参战不需要拉队员进去
		) {
			for (TeamMemberData member : members.values()) {
				WNPlayer mp = member.getPlayer();
				if (member.isFollow() && member.isOnline() && mp != null) {
					if (mp.getLevel() < sceneProp.reqLevel) {
						return LangService.getValue("AREA_TEAM_LEVEL_NOT_ENOUGH").replace("{playername}", mp.getName());
					}
				}
			}
		}

		// if (player.getPlayer().upLevel < sceneProp.reqUpLevel) {
		// String temp = LangService.getValue("AREA_PLAYER_UPLEVEL_LIMIT");
		// String tmp = PlayerUtil.getUpLevelName(sceneProp.reqUpLevel,
		// player.getPlayer().pro);
		// temp = temp.replace("{playerUpLevel}",
		// LangService.getValue("RED").replace("{a}", tmp));
		// return temp;
		// }

		if (sceneProp.mapID != GlobalConfig.World_Boss_NewScene || player.getPlayerTasks().isCompleteTaskByID(GlobalConfig.World_Boss_NweScene_Quest)) {
			if (sceneProp.upLevellimit > 0) {
				if (player.getPlayer().upLevel > sceneProp.upLevellimit) {
					return LangService.getValue("MAP_OVER_UPLEVEL").replace("{uplevel}", "" + sceneProp.upLevellimit);
				}
			} else if (sceneProp.levellimit > 0) {
				if (player.getLevel() > sceneProp.levellimit) {
					return LangService.getValue("MAP_OVER_LEVEL").replace("{level}", "" + sceneProp.levellimit);
				}
			}

		}
		// if (sceneProp.reqVip > 0 && player.baseDataManager.getVip().value <
		// sceneProp.reqVip) {
		// return LangService.getValue("EXCHANGE_VIP_NOT_REACH");
		// }
		if (sceneProp.reqQuestId != 0) {
			if (!player.getPlayerTasks().isTaskDoingOrFinish(sceneProp.reqQuestId)) {
				return LangService.getValue("PLAYER_NOT_FINISH_OR_NOT_HAVE_TASK");
			}
		}
		String reqItemCode = sceneProp.reqItemCode;
		int reqItemCount = sceneProp.reqItemCount;
		if (reqItemCode.length() > 0) {
			if (reqItemCode.equals("gold")) {
				if (player.moneyManager.getGold() < reqItemCount) {
					return LangService.getValue("MAP_ITEM_NOT_ENOUGH").replace("{itemName}", ItemUtil.getUnEquipPropByCode(reqItemCode).name);
				}
			} else {
				int itemNum = player.getWnBag().findItemNumByCode(reqItemCode);
				if (itemNum < reqItemCount) {
					return LangService.getValue("MAP_ITEM_NOT_ENOUGH").replace("{itemName}", ItemUtil.getUnEquipPropByCode(reqItemCode).name);
				}
			}
		}
		return null;
	}

	/**
	 * 扣除进入场景所需道具
	 */
	public static final boolean disCardItemByEnterArea(MapBase sceneProp, WNPlayer player) {
		String reqItemCode = sceneProp.reqItemCode;
		if (reqItemCode.length() > 0) {
			int itemNum = player.getWnBag().findItemNumByCode(reqItemCode);
			if (itemNum < sceneProp.reqItemCount) {
				return false;
			}
			player.getWnBag().discardItem(reqItemCode, sceneProp.reduceItemCount, Const.GOODS_CHANGE_TYPE.transport, null, false, false);
		}
		return true;
	}

	/** 重载方法 */
	public static final Area enterArea(WNPlayer player, int areaId) {
		return enterArea(player, areaId, 0, 0);
	}

	/**
	 * 进入相应场景，此操作会对进入场景相关条件进行判断以及扣除相关所需道具
	 */
	public static final Area enterArea(WNPlayer player, int areaId, float targetX, float targetY) {
		MapBase sceneProp = null;
		if (areaId == -999) {
			areaId = GlobalConfig.CROSS_SERVER_ENTER_SCENE;
		}
		sceneProp = AreaDataConfig.getInstance().get(areaId);
		if (sceneProp == null) {
			Out.error("no sceneProp areaId:", areaId);
			return null;
		}

		Out.debug("areaUtil enterArea areaId:", areaId);

		if (player.getAreaId() == areaId) {
			PlayerUtil.sendSysMessageToPlayer(LangService.getValue("MAP_IN"), player.getId(), Const.TipsType.BLACK);
			return null;
		}
		// 检查是否能进入该场景
		String result = canEnterArea(sceneProp, player);
		if (result != null) {
			PlayerUtil.sendSysMessageToPlayer(result, player.getId(), Const.TipsType.BLACK);
			return null;
		}

		disCardItemByEnterArea(sceneProp, player);

		if (sceneProp.type == Const.SCENE_TYPE.NORMAL.getValue() || sceneProp.type == Const.SCENE_TYPE.ILLUSION.getValue()) {
			return dispatchByAreaId(player, new AreaData(areaId, targetX, targetY), null);
		} else if (sceneProp.type == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() || sceneProp.type == Const.SCENE_TYPE.LOOP.getValue() || sceneProp.type == Const.SCENE_TYPE.DEMON_TOWER.getValue() || sceneProp.type == Const.SCENE_TYPE.RESOURCE_DUNGEON.getValue() || sceneProp.type == Const.SCENE_TYPE.ILLUSION_2.getValue()) {
			// player.getArea().onPlayerEntered(player);
			FightLevelManager fightLevelManager = player.fightLevelManager;
			String data = fightLevelManager.enterDungeonReq(player, areaId);
			if (data != null) {
				PlayerUtil.sendSysMessageToPlayer(data, player.getId(), TipsType.BLACK);
			}
			return null;
		} else if (sceneProp.type == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
			if (player.getSceneType() == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() || player.getSceneType() == Const.SCENE_TYPE.LOOP.getValue()) {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("CROSS_SERVER_AUTH_LIMIT_FIGHTLEVEL"), player.getId(), null);
				return null;
			}
			return dispatchByCrossServerId(player, new AreaData(areaId, targetX, targetY));
		} else if (sceneProp.type == Const.SCENE_TYPE.WORLD_BOSS.getValue()) {
			String instanceId = null;
			if (player.getTeamManager().isInTeam()) {
				Map<String, TeamMemberData> teamMembers = player.getTeamManager().getTeamMembers();
				for (TeamMemberData teamMember : teamMembers.values()) {
					WNPlayer member = teamMember.getPlayer();
					if (member != null && !teamMember.id.equals(player.getId()) && member.getAreaId() == areaId) {
						instanceId = member.getInstanceId();
						break;
					}
				}
			}
			if (instanceId != null) {
				return dispatchByInstanceId(player, new AreaData(areaId, instanceId));
			} else {
				return dispatchByAreaId(player, new AreaData(areaId, 0, 0), null);
			}
		}
		// else if (sceneProp.type == Const.SCENE_TYPE.ILLUSION_2.getValue()) {
		// return dispatchByAreaId(player, new AreaData(areaId, targetX, targetY));
		// }
		return null;
	}

	/**
	 * 是否可以使用坐骑
	 */
	public static final boolean canRideMount(int areaId) {
		MapBase prop = getAreaProp(areaId);
		return prop.rideMount == 1;
	}

	public static final MapBase getAreaProp(int areaId) {
		return AreaDataConfig.getInstance().get(areaId);
	}

	public static final int getAreaType(int areaId) {
		return getAreaProp(areaId).type;
	}

	/*
	 * 格式化HH-MM-SS的时间为当天该时段的Date
	 */
	public static Date formatToday(String stringTime) {
		String[] begins = stringTime.split("-");
		Calendar date = Calendar.getInstance();
		if (begins.length == 3) {
			date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(begins[0]));
			date.set(Calendar.MINUTE, Integer.parseInt(begins[1]));
			date.set(Calendar.SECOND, Integer.parseInt(begins[2]));
			date.set(Calendar.MILLISECOND, 0);
		}
		return date.getTime();
	};

	/**
	 * 判断是否需要创建新的场景
	 */
	public static final boolean needCreateArea(int areaId) {
		MapBase prop = getAreaProp(areaId);
		return prop == null || (prop.type != Const.SCENE_TYPE.NORMAL.getValue() && prop.type != Const.SCENE_TYPE.ILLUSION.getValue() && prop.type != Const.SCENE_TYPE.CROSS_SERVER.getValue());
	}

	/**
	 * 获取场景实例
	 */
	public static final Area getArea(String instanceId) {
		return AreaManager.getInstance().getArea(instanceId);
	}

	/**
	 * 关闭场景实例
	 */
	public final static void closeArea(String instanceId) {
		AreaManager.getInstance().closeArea(instanceId);
		Out.debug("closeArea:::", instanceId);
	}

	/**
	 * 关闭场景实例
	 */
	public static final void closeAreaNoPlayer(String instanceId) {
		AreaManager.getInstance().closeAreaNoPlayer(instanceId);
		Out.debug("closeAreaNoPlayer:::", instanceId);
	}

	/***
	 * 场景切换消息push到客户端
	 * 
	 * @param uid
	 * @param frontServerId
	 * @param areaId
	 */
	public static final void changeAreaPush(WNPlayer player, int areaId, String instanceId) {
		player.receive("area.playerPush.changeAreaPush", changeAreaPush(areaId, instanceId));

	}

	public static final ChangeAreaPush changeAreaPush(int areaId, String instanceId) {
		MapBase prop = AreaDataConfig.getInstance().get(areaId);
		ChangeAreaPush.Builder data = ChangeAreaPush.newBuilder();
		data.setS2CMapId(areaId);
		data.setS2CInstanceId(instanceId);
		data.setS2CSceneId(prop.templateID);
		data.setS2CSceneType(prop.type);
		data.setS2CSceneUseAgent(prop.useAgent);
		data.setS2CRideMount(prop.rideMount);
		data.setS2CChangePkType(prop.changePKtype);
		return data.build();
	}

	/***
	 * 实际切换地图
	 * 
	 * @param player
	 * @param areaData
	 */
	public static final Area changeArea(WNPlayer player, AreaData areaData) {

		Area area = player.getArea();
		if (area != null) {
			if (area.instanceId.equals(areaData.instanceId)) {
				Out.debug(player.getName(), "已在场景，无需切换：", area.prop.name);
				return area;
			}

			String matchScene = GlobalConfig.MATCH_SCENE; // 可以继续匹配的场景类型
			MapBase destMap = AreaDataConfig.getInstance().get(areaData.areaId);
			if (destMap != null && matchScene.indexOf(String.valueOf(destMap.type)) == -1) { // 不在可以继续匹配的场景类型中
				player.soloManager.quitMatching(false);
				player.five2FiveManager.cancelFive2FiveMatch(false);
			}
			area.syncPlayerHistoryData(player);
		}

		int areaId = areaData.areaId;
		float targetX = areaData.targetX;
		float targetY = areaData.targetY;

		player.syncBornData(targetX, targetY, areaId);
		player.setBornType(Const.BORN_TYPE.BORN);
		player.setEnterState(Const.ENTER_STATE.changeArea.value);
		// 进入新的area的服务器
		PlayerRemote.playerEnterAreaServer(player, areaData.instanceId);
		Out.info("enter scene:playerId=", player.getId(), "araid=", area == null ? areaId : area.areaId, ",instanceId=", area == null ? areaData.instanceId : area.instanceId);
		return area;
	}

	/**
	 * 根据场景实例id进入相应场景 params : player areaData {areaId, instanceId, targetX,
	 * targetY}
	 */
	public static final Area dispatchByInstanceId(WNPlayer player, AreaData areaData) {
		if (player.getInstanceId().equals(areaData.instanceId)) {
			return null;
		}
		// 实际切换地图
		changeTeamArea(player, areaData);
		return getArea(areaData.instanceId);
	}

	public static final Area dispatchByAreaId(WNPlayer player, int areaId, Map<String, Object> userData) {
		return dispatchByAreaId(player, new AreaData(areaId), userData);
	}

	/**
	 * add by wfy 便利方法，有待验证
	 * 
	 * @param player
	 * @param areaId
	 * @param targetX
	 * @param targetY
	 * @return
	 */
	public static final Area dispatchByAreaId(WNPlayer player, int areaId, float targetX, float targetY) {
		return dispatchByAreaId(player, new AreaData(areaId, targetX, targetY), null);
	}

	/**
	 * 野外、主城等场景进入 params : player areaData {areaId, targetX, targetY}
	 */
	public static final Area dispatchByAreaId(WNPlayer player, AreaData areaData, Map<String, Object> userData) {
		Out.debug("dispatchByAreaId areaData:", areaData.areaId, ",", areaData.instanceId);
		JSONObject json = Utils.toJSON("id", player.getId(), "logicServerId", player.getLogicServerId(), "areaId", areaData.areaId);
		if (userData != null) {
			json.putAll(userData);
		}
		Area area = AreaManager.getInstance().dispatchByAreaId(player, json);
		areaData.instanceId = area.instanceId;
		// 实际切换地图
		changeTeamArea(player, areaData);
		return area;
	}

	/**
	 * 跨服场景进入 params : player areaData {areaId, targetX, targetY}
	 */
	public static final Area dispatchByCrossServerId(WNPlayer player, AreaData areaData) {
		Out.debug("dispatchByCrossServerId areaData:", areaData);
		areaData.logicServerId = player.getAcrossServerId();
		JSONObject json = Utils.toJSON("id", player.getId(), "logicServerId", areaData.logicServerId, "areaId", areaData.areaId);
		Area area = null;
		if (GConfig.getInstance().isEnableProxy()) {
			json = ProxyClient.getInstance().request(ProxyMethod.M_DISPATCHER, json);
			if (json.containsKey("exists")) {
				area = getArea(json.getString("instanceId"));
				if (area == null) {
					return bindCrossServerArea(player, json, (crossArea) -> {
						areaData.instanceId = crossArea.instanceId;
						// 实际切换地图
						changeTeamArea(player, areaData);
					});
				}
			} else {
				area = AreaManager.getInstance().createArea(player, json);
				json.put("csNode", GWorld.__CS_NODE);
				json.put("fullCount", area.fullCount);
				json.put("maxCount", area.maxCount);
				json.put("lifeTime", area.lifeTime);
				json.put("sid", GWorld.__SERVER_ID);
				json = ProxyClient.getInstance().request(ProxyMethod.M_CREATE, json);
				area.lineIndex = json.getIntValue("lineIndex");
			}
			areaData.instanceId = area.instanceId;
			// 实际切换地图
			changeTeamArea(player, areaData);
		} else {
			area = dispatchByAreaId(player, areaData, null);
		}

		return area;
	}

	@FunctionalInterface
	public static interface AreaCB {
		void call(CrossServerArea crossArea);
	}

	public static Area bindCrossServerArea(WNPlayer player, JSONObject json, AreaCB cb) {
		CSharpNode node = json.getObject("csNode", CSharpNode.class);
		Area area = new CrossServerArea(json);
		AreaManager.CrossServerAreas.put(area.instanceId, area);
		String nodeId = node.getNodeId();
		CrossServerArea crossArea = (CrossServerArea) area;
		if (CSharpClient.getXmdsManager(nodeId) == null) {
			CSharpClient.getInstance().connectAsync(node, () -> {
				Out.debug(" bindCrossServerArea ", nodeId);
				player.setBattleServerId(nodeId);
				crossArea.bindBattleServer(player);
				cb.call(crossArea);
			});
			return area;
		} else {
			crossArea.bindBattleServer(player);
		}
		cb.call(crossArea);
		return area;
	}

	public static final void changeTeamArea(WNPlayer player, AreaData areaData) {
		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
		if (members != null && player.getTeamManager().isTeamLeader() && areaData.areaId != ArenaService.ARENA_MAP_ID// 竞技场/工会BOSS内独立参战不需要拉队员进去
		) {
			for (TeamMemberData member : members.values()) {
				if (member.isFollow() && member.isOnline()) {
					// 进入新的area服务器
					changeArea(member.getPlayer(), areaData);
				}
			}
		} else {
			changeArea(player, areaData);
		}
	}

	/**
	 * 副本、女武神、剧情等单场景需要创建 params : playerUids areaData {areaId,enterType, targetX,
	 * targetY}
	 * 
	 * @throws Exception
	 */
	public static Area createAreaAndDispatch(WNPlayer player, Collection<String> playerIds, int logicServerId, int areaId, Map<String, Object> userData) {
		JSONObject areaData = Utils.toJSON("logicServerId", logicServerId, "areaId", areaId);
		if (userData != null) {
			areaData.putAll(userData);
		}
		Area area = AreaManager.getInstance().createArea(player, areaData);
		AreaData data = new AreaData(areaId, area.instanceId);
		// 其他玩家拉入场景
		for (String rid : playerIds) {
			WNPlayer member = PlayerUtil.getOnlinePlayer(rid);
			if (member != null) {
				// 进入新的area服务器
				AreaUtil.changeArea(member, data);
			}
		}

		return area;
	}

	public static Area dispatch(WNPlayer player, Collection<String> playerIds, int logicServerId, int areaId, Map<String, Object> userData) {
		JSONObject areaData = Utils.toJSON("logicServerId", logicServerId, "areaId", areaId);
		if (userData != null) {
			areaData.putAll(userData);
		}
		Area area = AreaManager.getInstance().dispatchByAreaId(player, areaData);
		AreaData data = new AreaData(areaId, area.instanceId);
		// 其他玩家拉入场景
		for (String rid : playerIds) {
			WNPlayer member = PlayerUtil.getOnlinePlayer(rid);
			if (member != null) {
				// 进入新的area服务器
				AreaUtil.changeArea(member, data);
			}
		}

		return area;
	}


	/**
	 * 判断给定的sceneType是否为普通野外地图
	 * @param sceneType
	 * @return 
	 */
	private static boolean isNormalArea(int sceneType) {
		return sceneType == SCENE_TYPE.NORMAL.getValue() || sceneType == SCENE_TYPE.ILLUSION.getValue() || sceneType == SCENE_TYPE.CROSS_SERVER.getValue();
	}
	
	
	/**
	 * 角色bind的时候分配场景
	 */
	public static Area dispatch(WNPlayer player) {
		Const.BORN_TYPE bornType = Const.BORN_TYPE.NORMAL;
		Out.debug("dispatch try createAreaAndDispatch begin");
		Area area = player.getArea();
		if (area == null || area.isPlayerClose(player)) {
			int areaId = player.playerTempData.areaId;
			MapBase prop = AreaUtil.getAreaProp(areaId);
			if (prop.disConnToMapID != 0) {
				areaId = player.getPlayerTempData().bornAreaId;// 世界首领地图下线后在默认地图上线
				bornType = Const.BORN_TYPE.BORN;
				Out.debug("create area 2222: areaId:", areaId, "  bornType:", bornType);
			} else if (!isNormalArea(prop.type)) {
				areaId = player.getPlayerTempData().historyAreaId;// 副本已销毁的情况下进预备场景
				bornType = Const.BORN_TYPE.HISTORY;
				Out.debug("create area 1111: areaId:", areaId, "  bornType:", bornType);
			}
			if (Const.SCENE_TYPE.CROSS_SERVER.getValue() == AreaUtil.getAreaType(areaId)) {
				area = AreaUtil.dispatchByCrossServerId(player, new AreaData(areaId));
			} else {
				player.setBornType(bornType, areaId);//无论什么情况都要确保areaId和x，y是一致的，否则有卡坐标点的危险
				float x = player.playerTempData.x;
				float y = player.playerTempData.y;
				area = AreaUtil.dispatchByAreaId(player, new AreaData(areaId, x, y), null);
			}
		} else {//断线后用户很快上线，此时存档没有销毁
			if(!area.isNormal()) {//只要不是野外，就走默认出生点
				bornType = Const.BORN_TYPE.BORN;
			}
			player.setBornType(bornType, area.areaId);
			// 进入场景
			PlayerRemote.playerEnterAreaServerInner(player, area);
		}

		return area;
	};

	public static Area createArea(WNPlayer player, JSONObject json) {
		return AreaManager.getInstance().createArea(player, json);
	}

	public static boolean isCrossArea(int sceneType) {
		return sceneType == Const.SCENE_TYPE.CROSS_SERVER.getValue();
	}

}
