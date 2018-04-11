package com.wanniu.game.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.csharp.CSharpNode;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaEvent;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.ProxyArea;
import com.wanniu.game.common.Const.DailyType;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.petNew.PetNew;
import com.wanniu.game.poes.XianYuanPO;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.team.TeamData;

import pomelo.area.FightLevelHandler.MemberData;
import pomelo.area.FightLevelHandler.OnConfirmEnterFubenPush;
import pomelo.area.FightLevelHandler.OnMemberEnterFubenStateChangePush;
import pomelo.area.TeamHandler.AcrossPlayer;
import pomelo.area.TeamHandler.OnAcrossTeamInfoPush;

/**
 * @author agui
 */
public final class ProxyUtil {

	public static void onAcrossPlayerEvent(Packet pak) {
		String playerId = pak.getString();
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		int type = pak.getByte();
		Out.debug(playerId, " player proxy event -> ", type);
		if (player != null) {
			switch (type) {
			case 1: { // 触发事件
				player.onEvent(new TaskEvent(pak.getInt(), pak.getString()));
				break;
			}
			case 2: { // 新增物品
				int fromDes = pak.getInt();
				String item = pak.getString();
				PlayerItemPO playerItem = Utils.deserialize(item, PlayerItemPO.class);
				DItemEquipBase prop = ItemConfig.getInstance().getItemProp(playerItem.code);
				player.bag.addEntityItem(new NormalItem(playerItem, prop), GOODS_CHANGE_TYPE.getE(fromDes));
				break;
			}
			case 3: { // 杀怪
				int unitTemplateId = pak.getInt();
				AreaEvent.onTaskEvent(playerId, unitTemplateId);
				break;
			}
			case 4: { // 幻境奖励
				player.illusionManager.addAward(pak.getString(), pak.getInt());
				break;
			}
			case 5: { // 宠物经验
				PetNew pet = player.getFightingPet();
				if (pet != null) {
					pet.addExp(pak.getInt(), true);
				}
				break;
			}
			case 6: { // 首杀
				player.playerAttachPO.addFirstMonsterId(pak.getInt());
				break;
			}
			case 7: { // 通关副本
				player.finishFightLevel(pak.getInt(), pak.getInt());
				break;
			}
			case 8: { // 成就
				player.achievementManager.onPassedDungeon(pak.getInt());
				break;
			}
			case 9: { // 日常
				player.dailyActivityMgr.onEvent(DailyType.valueOf(pak.getString()), pak.getString(), pak.getInt());
				break;
			}
			case 10: { // 组队确认
				onAcrossEnterConfirm(player, pak);
				break;
			}
			case 11: { // 组队回复
				onAcrossEnterReply(player, pak);
				break;
			}
			case 12: { // 仙缘
				player.moneyManager.addXianYuan(pak.getInt(), pak.getInt());
				break;
			}
			case 13: { // 自由拾取道具
				Area area = player.getArea();
				if (!(area instanceof ProxyArea))
					return;
				PlayerItemPO item = Utils.deserialize(pak.getString(), PlayerItemPO.class);
				DItemEquipBase prop = ItemConfig.getInstance().getItemProp(item.code);
				area.onFreedomPickItem(player, new NormalItem(item, prop), pak.getBoolean());
				break;
			}
			case 14: { // 补充邮件类道具
				player.bag.addCodeItemMail(pak.getString(), pak.getInt(), ForceType.getE(pak.getInt()), GOODS_CHANGE_TYPE.getE(pak.getInt()), pak.getString());
				break;
			}
			}
		}
	}

	private static void onAcrossEnterConfirm(WNPlayer player, Packet pak) {
		int targetId = pak.getInt();
		int difficulty = pak.getByte();
		String leaderId = pak.getString();

		int count = pak.getByte();
		List<MemberData> memberData = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			MemberData.Builder md = MemberData.newBuilder();
			md.setId(pak.getString());
			md.setName(pak.getString());
			md.setPro(pak.getByte());
			md.setLevel(pak.getShort());
			memberData.add(md.build());
		}

		int overTime = GlobalConfig.TeamGoMapLeftTime;

		OnConfirmEnterFubenPush enterFuben = OnConfirmEnterFubenPush.newBuilder().setS2CMsg(LangService.getValue("CONFIRM_ENTRY")).setS2CFubenId(TeamData.getTargetMap(targetId, difficulty)).setS2COverTime(overTime)
				.setS2CLeaderId(leaderId == null ? "" : leaderId).addAllS2CMemberData(memberData).build();

		player.receive("area.fightLevelPush.onConfirmEnterFubenPush", enterFuben);
	}

	private static void onAcrossEnterReply(WNPlayer player, Packet pak) {
		OnMemberEnterFubenStateChangePush msgData = OnMemberEnterFubenStateChangePush.newBuilder().setS2CPlayerId(pak.getString()).setS2CIsReady(pak.getByte()).build();
		player.receive("area.fightLevelPush.onMemberEnterFubenStateChangePush", msgData);
	}

	public static void onAcrossChangeArea(Packet pak) {
		String node = pak.getString();
		CSharpNode csNode = JSON.parseObject(node, CSharpNode.class);
		String playerId = pak.getString();
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		int areaId = pak.getInt();
		String instanceId = pak.getString();
		if (player == null) {
			Out.warn("proxy receive change area:", areaId, " isntanceId:", instanceId);
			return;
		}
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(playerId);
				body.writeBoolean(player.isRobot());
				body.writeFloat(player.vipManager.getVipExpRatio());
				body.writeInt(player.getGuildExdExp());
				body.writeInt(player.getGuildExdGold());
				body.writeInt(player.getBtlExdGold());
				PetNew petnew = player.petNewManager.getFightingPet();
				body.writeString(petnew == null ? "" : Utils.serialize(petnew.po));
				body.writeString(Utils.serialize(player.illusionManager.illusionPO));
				body.writeString(Utils.serialize(player.vipManager.po));
				XianYuanPO xianYuan = player.allBlobData.xianYuan;
				body.writeString(xianYuan == null ? "" : Utils.serialize(xianYuan));
				List<Integer> firstKillMonsterIds = player.playerAttachPO.firstKillMonsterIds;
				body.writeShort(firstKillMonsterIds.size());
				for (Integer id : firstKillMonsterIds) {
					body.writeInt(id);
				}
			}

			@Override
			public short getType() {
				return ProxyType.PLAYER_DATA;
			}
		});
		Out.info(node, " areaId:", areaId, " isntanceId:", instanceId);
		CSharpClient.getInstance().connect(csNode, () -> {
			if (AreaUtil.getArea(instanceId) == null) {
				ProxyArea area = new ProxyArea(Utils.toJSON("areaId", areaId, "instanceId", instanceId));
				area.setBattleServerId(csNode.getNodeId());
				AreaManager.ProxyServerAreas.put(instanceId, area);
			}
			AreaData areaData = new AreaData(areaId, instanceId);
			AreaUtil.changeArea(player, areaData);
		});
	}

	/** 处理跨服队伍消息 */
	public static void onAcrossTeamEvent(Packet packet) {
		int secType = packet.getByte();
		if (secType == 2) { // 创建实例
			createInstance(packet);
			return;
		}
		String playerId = packet.getString();
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player == null) {
			Out.warn("proxy client quit : ", playerId);
			return;
		}
		switch (secType) {
		case 1: { // 队伍信息
			int second = packet.getShort();
			int count = packet.getByte();
			Out.debug("across team info sceond ", second, " count ", count);
			OnAcrossTeamInfoPush.Builder push = OnAcrossTeamInfoPush.newBuilder().setTargetId(player.teamManager.acrossTargetId).setDifficulty(player.teamManager.acrossDifficulty);
			push.setSecond(second);
			push.addPlayers(AcrossPlayer.newBuilder().setName(player.getName()).setLevel(player.getLevel()).setPro(player.getPro()));
			for (int i = 1; i < count; i++) {
				push.addPlayers(AcrossPlayer.newBuilder().setName(packet.getString()).setLevel(packet.getShort()).setPro(packet.getByte()));
			}
			player.receive("area.teamPush.onAcrossTeamInfoPush", push.build());
			break;
		}
		case 3: {
			player.teamManager.quitAcrossMatch(true);
			break;
		}
		case 4: {
			WNProxy proxy = GWorld.getProxy(playerId);
			if (proxy != null)
				proxy.free(packet.getBoolean());
			break;
		}
		default: {
			Out.warn("team onAcrossPacket secType : ", secType);
		}
		}
	}

	private static void createInstance(Packet packet) {
		int targetId = packet.getInt();
		int difficulty = packet.getByte();
		int count = packet.getByte();
		WNPlayer leader = null;
		Map<String, WNPlayer> players = new HashMap<>();
		for (int i = 0; i < count; i++) {
			int sid = packet.getInt();
			String playerId = packet.getString();
			String name = packet.getString();
			int level = packet.getInt();
			int pro = packet.getInt();
			if (sid != GWorld.__SERVER_ID) {
				WNProxy proxy = new WNProxy(playerId, sid);
				proxy.name = name;
				proxy.level = level;
				proxy.pro = pro;
				GWorld.Proxys.put(playerId, proxy);
				players.put(playerId, proxy);
				proxy.teamMembers = players;
			} else {
				WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
				if (leader == null || player.getTeamManager().isTeamLeader()) {
					leader = player;
				}
				players.put(playerId, player);
				player.teamMembers = players;
			}
		}

		int mapId = TeamData.getTargetMap(targetId, difficulty);
		if (mapId != 0) {
			Area area = AreaUtil.createArea(leader, Utils.toJSON("logicServerId", leader.getLogicServerId(), "areaId", mapId));
			Out.info("onAcrossPacket : ", area);
			AreaData areaData = new AreaData(area.areaId, area.instanceId);
			for (WNPlayer player : players.values()) {
				player.changeArea(areaData);
				if (player.isProxy()) {
					player.setArea(area);
					area.putActor(player.getId());
				}
			}
		} else {
			Out.warn("onAcrossPacket : ", targetId, " - ", difficulty);
		}
	}
}
