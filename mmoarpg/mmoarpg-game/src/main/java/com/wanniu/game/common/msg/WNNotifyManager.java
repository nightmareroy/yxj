package com.wanniu.game.common.msg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.SoloDataPO.RankStatus;

import pomelo.Common.AttributeSimple;
import pomelo.Common.PropertyStruct;
import pomelo.Common.SkillKeyStruct;
import pomelo.area.AchievementHandler.Achievement;
import pomelo.area.AchievementHandler.OnAchievementPush;
import pomelo.area.BagHandler.BagGridFullPush;
import pomelo.area.BagHandler.BagItemUpdatePush;
import pomelo.area.BagHandler.BagNewEquipPush;
import pomelo.area.BagHandler.BagNewItemPush;
import pomelo.area.ConsignmentLineHandler.ConsignmentRemovePush;
import pomelo.area.EquipHandler.EquipmentSimplePush;
import pomelo.area.EquipHandler.StrengthPosPush;
import pomelo.area.FunctionHandler.FunctionGoToPush;
import pomelo.area.ItemHandler.FishItemPush;
import pomelo.area.ItemHandler.ItemDetailPush;
import pomelo.area.LimitTimeActivityHandler.ActivityInfo;
import pomelo.area.MailHandler.Mail;
import pomelo.area.MailHandler.OnGetMailPush;
import pomelo.area.PlayerHandler.PlayerBattleAttributePush;
import pomelo.area.PlayerHandler.PlayerDynamicPush;
import pomelo.area.PlayerHandler.PlayerRelivePush;
import pomelo.area.PlayerHandler.PlayerSaverRebirthPush;
import pomelo.area.RankHandler.OnAwardRankPush;
import pomelo.area.SkillHandler.SkillBasic;
import pomelo.area.SkillHandler.SkillUpdatePush;
import pomelo.area.SkillKeysHandler.SkillKeyUpdatePush;
import pomelo.area.SoloHandler.GameResult;
import pomelo.area.SoloHandler.OnFightPointPush;
import pomelo.area.SoloHandler.OnGameEndPush;
import pomelo.area.SoloHandler.OnNewRewardPush;
import pomelo.area.SoloHandler.OnRoundEndPush;
import pomelo.area.SoloHandler.RoundResult;
import pomelo.item.ItemOuterClass.EquipGridStrengthInfo;
import pomelo.item.ItemOuterClass.Grid;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 推送逻辑处理
 * 
 * @author Yangzz
 *
 */
public class WNNotifyManager {

	private static WNNotifyManager instance;

	public static WNNotifyManager getInstance() {
		if (instance == null) {
			instance = new WNNotifyManager();
		}
		return instance;
	}

	private WNNotifyManager() {

	}

	public void teamBagGridNumPush(WNPlayer player, int args) {
		// Out.debug(getClass(), "teamBagGridNumPush: " + player.name + " id:
		// " + player.id + " data: ", args);
		player.refreshPlayerRemainTeamBagCountData(args);
	};

	public void pushFunctionGoTo(WNPlayer player, FunctionGoToPush.Builder args) {
		Out.debug(getClass(), "pushFunctionGoTo: ", player.getName(), " id: ", player.getId(), " data: ", args);
		args.setS2CCode(PomeloRequest.OK);

		player.receive("area.functionPush.functionGoToPush", args.build());
	}

	public void pushFishItem(WNPlayer player, List<MiniItem> args) {
		Out.debug(getClass(), "pushFishItem: ", player.getName(), " id: ", player.getId(), " data: ", args);
		// var uid = {uid: player.uid, sid : player.serverId};
		FishItemPush.Builder data = FishItemPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.addAllS2CItem(args);
		player.receive("area.itemPush.fishItemPush", data.build());
	}

	//
	public final void pushSkillUpdate(WNPlayer player, List<Integer> skillIds) {
		if (skillIds.size() <= 0) {
			return;
		}
		SkillUpdatePush.Builder data = SkillUpdatePush.newBuilder();
		data.setS2CCode(PomeloRequest.OK);

		ArrayList<SkillBasic> skills = new ArrayList<>();

		for (int index : skillIds) {
			SkillBasic skillBasic = player.skillManager.getSkillBasicUpdate4PayLoad(index);
			if (skillBasic != null) {
				skills.add(skillBasic);
			}
		}
		data.addAllS2CData(skills);
		boolean isCanSetting = false;
		data.setHubLock((!isCanSetting) ? false : true);
		if (skills.size() > 0) {
			player.receive("area.skillPush.skillUpdatePush", data.build());
		}
	}

	public final void pushSkillKeysUpdate(WNPlayer player, List<SkillKeyStruct> data) {
		if (data.size() > 0) {
			SkillKeyUpdatePush.Builder build = SkillKeyUpdatePush.newBuilder();
			build.setS2CCode(PomeloRequest.OK);
			build.addAllS2CData(data);
			player.receive("area.skillKeysPush.skillKeyUpdatePush", build.build());
		}

	}

	/**
	 * 装备信息推送
	 * 
	 * @param player
	 * @param args
	 */
	public void pushEquipmentDynamic(WNPlayer player, int[] grids, boolean refresh) {
		Out.debug(getClass(), "@#@pushEquipmentDynamic: ", player.getName(), " id: ", player.getId(), " data: ", grids, ",refresh:", refresh);
		EquipmentSimplePush.Builder data = EquipmentSimplePush.newBuilder();
		data.setS2CCode(PomeloRequest.OK);
		List<Grid> bagGrids = new ArrayList<>();

		ItemDetailPush.Builder equipDetails = ItemDetailPush.newBuilder();
		equipDetails.setS2CCode(PomeloRequest.OK);

		for (int pos : grids) {
			bagGrids.add(player.equipManager.getEquip4PayLoad(pos));

			NormalItem equip = player.equipManager.getEquipment(pos);
			if (equip != null && refresh) {
				equipDetails.addS2CData(equip.getItemDetail(player.playerBasePO));
			}
		}

		if (equipDetails.getS2CDataCount() > 0) {
			player.receive("area.itemPush.itemDetailPush", equipDetails.build());
		}

		data.addAllS2CData(bagGrids);
		if (bagGrids.size() > 0) {
			player.receive("area.equipPush.equipmentSimplePush", data.build());
		}
	}

	/**
	 * 装备位置信息推送 TODO 暂未实现
	 */
	public void pushEquipmentPOS(WNPlayer player, int[] poses, boolean refresh) {
		Out.debug(getClass(), "@#@pushEquipmentPOS: ", player.getName(), " id: ", player.getId(), " data: ", poses, ",refresh:", refresh);
		StrengthPosPush.Builder data = StrengthPosPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		List<EquipGridStrengthInfo> grids = new ArrayList<>();
		for (int pos : poses) {
			grids.add(player.equipManager.getStrenghInfo(pos));
		}

		data.addAllStrengthInfos(grids);
		if (grids.size() > 0) {
			player.receive("area.equipPush.equipStrengthPosPush", data.build());
		}
	}

	/**
	 * @param flag 没有改变
	 */
	public void pushBagItemDynamic(WNPlayer player, List<Integer> grid, boolean flag, GOODS_CHANGE_TYPE source) {
		BagItemUpdatePush.Builder data = BagItemUpdatePush.newBuilder();
		List<Grid> bagGrids = new ArrayList<>();

		BagNewEquipPush.Builder newEquip = BagNewEquipPush.newBuilder();

		ItemDetailPush.Builder itemDetails = ItemDetailPush.newBuilder();
		itemDetails.setS2CCode(PomeloRequest.OK);

		for (int pos : grid) {
			Grid.Builder gb = player.getWnBag().getGrid4PayLoad(pos);
			if (source != null) {
				gb.setSource(source.getValue());
			}
			bagGrids.add(gb.build());
			NormalItem item = player.getWnBag().getItem(pos);

			if (flag && item != null) {
				itemDetails.addS2CData(item.getItemDetail(player.playerBasePO));
				if (item.itemDb.isNew == 1) {
					newEquip.addS2CData(item.getId());
				}
			}
		}

		if (itemDetails.getS2CDataCount() > 0) {
			player.receive("area.itemPush.itemDetailPush", itemDetails.build());
		}

		data.setS2CType(Const.BAG_TYPE.BAG.getValue());
		data.addAllS2CData(bagGrids);
		if (bagGrids.size() > 0) {
			player.receive("area.bagPush.bagItemUpdatePush", data.build());
		}

		if (newEquip.getS2CDataCount() > 0) {
			player.receive("area.bagPush.bagNewEquipPush", newEquip.build());
		}
	}

	/**
	 * @param flag 没有改变
	 */
	public void pushBagItemDynamic(WNPlayer player, List<Integer> grid, boolean flag) {
		pushBagItemDynamic(player, grid, flag, null);
	}

	/**
	 * 获得新物品推送
	 */
	public void pushBagNewItem(WNPlayer player, String code, int num, GOODS_CHANGE_TYPE from) {
		Out.debug(getClass(), "bag pushBagNewItem: ", player.getPlayer().name, " id: ", player.getPlayer().id, " ::: ", code, ":", num);

		BagNewItemPush.Builder data = BagNewItemPush.newBuilder();

		MiniItem.Builder item = ItemUtil.getMiniItemData(code, num, null);
		if (item != null) {
			// if (!StringUtil.isEmpty(ownName)) {
			// item.setName(item.getName().replace("$n", ownName));
			// }
			data.addS2CData(item);
			player.receive("area.bagPush.bagNewItemPush", data.build());
			player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.ITEM, item.getName(), item.getQColor(), String.valueOf(num), from);
		} else {
			Out.error("pushBagNewItem item:::", code, ":", num, ":", "is null");
		}
	}

	public void gridNotEnough(WNPlayer player) {
		BagGridFullPush.Builder data = BagGridFullPush.newBuilder();
		data.setS2CCode(PomeloRequest.OK);
		player.receive("area.bagPush.bagGridFullPush", data.build());
	}

	public void pushBagItemDynamicWareHouse(WNPlayer player, List<Integer> grid, boolean flag) {
		Out.debug("wareHouse pushBagItemDynamic: ", player.getName(), " id: ", player.getId());
		BagItemUpdatePush.Builder data = BagItemUpdatePush.newBuilder();
		List<Grid> bagGrids = new ArrayList<>();

		WNBag wareHouse = player.wareHouse;

		for (int pos : grid) {
			bagGrids.add(wareHouse.getGrid4PayLoad(pos).build());
		}

		data.setS2CType(Const.BAG_TYPE.WAREHOUSE.getValue());
		data.addAllS2CData(bagGrids);
		if (bagGrids.size() > 0) {
			player.receive("area.bagPush.bagItemUpdatePush", data.build());
		}
	}

	public void pushBagItemDynamicRecycle(WNPlayer player, List<Integer> grid, boolean flag) {
		Out.debug("recycle pushBagItemDynamic: ", player.getName(), " id: ", player.getId());
		BagItemUpdatePush.Builder data = BagItemUpdatePush.newBuilder();
		List<Grid> bagGrids = new ArrayList<>();

		WNBag recycle = player.recycle;

		for (int pos : grid) {
			bagGrids.add(recycle.getGrid4PayLoad(pos).build());
		}

		data.setS2CType(Const.BAG_TYPE.RECYCLE.getValue());
		data.addAllS2CData(bagGrids);
		if (bagGrids.size() > 0) {
			player.receive("area.bagPush.bagItemUpdatePush", data.build());
		}
	}

	/**
	 * @param args[k,v]
	 */
	public void pushEffectData(WNPlayer player, Map<String, Object> args) {
		Out.debug(getClass(), "pushEffectData: ", player.getName(), " id: ", player.getId(), " data: ", args);
		PlayerDynamicPush.Builder data = PlayerDynamicPush.newBuilder();
		List<PropertyStruct> playerData = new ArrayList<PropertyStruct>();
		Iterator<String> keys = args.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			playerData.add(CommonUtil.transferDataType(key, args.get(key)).build());
		}
		data.addAllS2CData(playerData);
		if (playerData.size() > 0) {
			player.receive("area.playerPush.playerDynamicPush", data.build());
		}
	}

	/**
	 * 需要来源的玩家货币数据推送
	 */
	public void pushPlayerDynamic(WNPlayer player, PropertyStruct prop) {
		Out.debug("pushPlayerDynamic: ", player.getName(), " id: ", player.getId(), " data: ", prop);
		PlayerDynamicPush.Builder data = PlayerDynamicPush.newBuilder();
		data.addS2CData(prop);
		if (data.getS2CDataCount() > 0) {
			player.receive("area.playerPush.playerDynamicPush", data.build());
		}
	}

	/**
	 * 玩家数据推送
	 */
	public void pushPlayerDynamic(WNPlayer player, Map<String, Object> args) {
		Out.debug("pushPlayerDynamic: ", player.getName(), " id: ", player.getId(), " data: ", args);
		PlayerDynamicPush.Builder data = PlayerDynamicPush.newBuilder();

		for (String key : args.keySet()) {
			Object value = args.get(key);
			if (value == null) {
				Out.error("player prop ", key, " is null");
				continue;
			}
			data.addS2CData(CommonUtil.transferDataType(key, value));
		}

		if (data.getS2CDataCount() > 0) {
			player.receive("area.playerPush.playerDynamicPush", data.build());
		}
	}

	public void pushRelive(WNPlayer player, PlayerRelivePush data) {
		Out.debug(getClass(), " pushRelive: ", player.getName(), " id: ", player.getId());
		player.receive("area.playerPush.playerRelivePush", data);
	}

	/**
	 * 升级通知
	 */
	public void levelChange(WNPlayer player, int level) {

		player.getPlayerTasks().onLevelChange(level);

		// player.playerSkillManager.onLevelChange();
	}

	public void pushMails(WNPlayer player, ArrayList<Mail> mails) {
		OnGetMailPush.Builder build = OnGetMailPush.newBuilder();
		build.setS2CCode(PomeloRequest.OK);
		build.addAllMails(mails);
		player.receive("area.mailPush.onGetMailPush", build.build());
	}

	public void pushAchievements(WNPlayer player, List<Achievement> achievements) {
		Out.debug(getClass(), "pushAchievements : ", player.getName(), "id: ", player.getId(), "data: ", achievements);
		OnAchievementPush.Builder data = OnAchievementPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.addAllS2CAchievements(achievements);
		player.receive("area.achievementPush.onAchievementPush", data.build());
	};

	public void pushAwardRank(WNPlayer player, int rankId) {
		OnAwardRankPush.Builder build = OnAwardRankPush.newBuilder();
		build.setS2CCode(PomeloRequest.OK);
		build.setS2CAwardRankId(rankId);
		player.receive("area.rankPush.onAwardRankPush", build.build());

	}

	public void pushNewReward(WNPlayer player) {

		OnNewRewardPush.Builder data = OnNewRewardPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);

		Out.debug(getClass(), "pushNewReward : ", player.getName(), "id: ", player.getId(), "data: ", data);
		player.receive("area.soloPush.onNewRewardPush", data.build());
	};

	public void pushFightPoint(WNPlayer player, int fightPoint) {
		OnFightPointPush.Builder data = OnFightPointPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.setS2CFightPoint(fightPoint);
		Out.debug(getClass(), "pushFightPoint : ", player.getName(), "id: ", player.getId(), "data: ", data);
		player.receive("area.soloPush.onFightPointPush", data.build());
	};

	public void pushRoundEnd(WNPlayer player, RoundResult roundResult) {
		OnRoundEndPush.Builder data = OnRoundEndPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.setS2CRoundResult(roundResult);
		Out.debug(getClass(), "pushRoundEnd : ", player.getName(), "id: ", player.getId(), "data: ", data);
		player.receive("area.soloPush.onRoundEndPush", data.build());
	};

	public void pushGameEnd(WNPlayer player, GameResult gameResult, int gameOverTime, boolean isAddBox) {
		OnGameEndPush.Builder data = OnGameEndPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.setS2CGameOverTime(gameOverTime);
		data.setS2CGameResult(gameResult);

		boolean finish = true;
		for (RankStatus s : player.soloManager.soloData.dailyRewards) {
			if (s.getStatus() == 0) {
				finish = false;
			}
		}
		if (isAddBox) {
			data.setDailyBattleTimes(GlobalConfig.Solo_PKForChest);
		} else if (!finish) {
			data.setDailyBattleTimes(player.soloManager.soloData.dailyBattleTimes);
		}
		Out.debug(getClass(), "pushGameEnd : ", player.getName(), "id: ", player.getId(), "data: ", data);
		player.receive("area.soloPush.onGameEndPush", data.build());
	};

	public void pushRebirth(WNPlayer player, String name) {
		Out.debug(WNNotifyManager.class, "pushRebirth: ", player.getName(), "id: ", player.getId(), "data:", name);
		// var uid = {uid: player.uid, sid: player.serverId};
		PlayerSaverRebirthPush.Builder data = PlayerSaverRebirthPush.newBuilder();
		data.setSaverName(name);
		player.receive("area.playerPush.playerSaverRebirthPush", data.build());
	}

	public final void consignmentRemovePush(WNPlayer player, String id) {
		ConsignmentRemovePush.Builder push = ConsignmentRemovePush.newBuilder();
		push.setS2CCode(PomeloRequest.OK);
		push.setS2CId(id);
		player.receive("area.consignmentLinePush.consignmentRemovePush", push.build());
	}

	public void pushActivityInfo(WNPlayer player, ArrayList<ActivityInfo> activityInfo) {
		// TODO
		// var uid = {uid: player.uid, sid: player.serverId};
		// var data = {s2c_code: Const.CODE.OK, s2c_ltActivity:
		// args.activityInfo};
		// player.receive(player.getUid(),
		// player.getServerId(),
		// "area.limitTimeActivityPush.ltActivityInfoPush",
		// data);
		// });
	}

	/////////////////////// playerScriptSync.js//////////////////////////////////
	public void updateScript(WNPlayer player) {
		player.updateSuperScriptList(player.getItemChangeScript());
	}

	public void pushPlayerBattleData(WNPlayer player) {
		if (player.btlDataManager == null)
			return;
		PlayerBattleAttributePush.Builder data = PlayerBattleAttributePush.newBuilder();
		Map<PlayerBtlData, Integer> finalInflus = player.btlDataManager.finalInflus;
		for (PlayerBtlData pbd : finalInflus.keySet()) {
			if (pbd == null)
				continue;
			AttributeSimple.Builder as = AttributeSimple.newBuilder();
			as.setId(pbd.id);
			as.setValue(finalInflus.get(pbd));
			data.addDatas(as);
		}
		player.receive("area.playerPush.playerBattleAttributePush", data.build());
	}
}
