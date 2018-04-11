package com.wanniu.game.request.chat;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.chat.GMChatUtil;
import com.wanniu.game.chat.GMChatUtil.GMChatResult;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.FUNCTION_GOTO_TYPE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daoyou.DaoYouCenter;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.ChatSettingCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DaoYouMemberPO;
import com.wanniu.game.recent.ChatMsg;
import com.wanniu.game.recent.RecentChatCenter;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.util.BlackWordUtil;
import com.wanniu.redis.PublishCenter;

import cn.qeng.common.gm.monitor.ChatMonitor;
import cn.qeng.common.gm.monitor.MonitorConst;
import io.netty.util.AttributeKey;
import pomelo.chat.ChatHandler.OnChatPush;
import pomelo.chat.ChatHandler.SendChatRequest;
import pomelo.chat.ChatHandler.SendChatResponse;

/**
 * @author agui
 */
@GClientEvent("chat.chatHandler.sendChatRequest")
public class SendChatHandler extends PomeloRequest {

	private static final AttributeKey<Long> INVITE_CALL = AttributeKey.valueOf("INVITE.CALL");

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SendChatRequest req = SendChatRequest.parseFrom(pak.getRemaingBytes());
		String text = req.getC2SContent();
		String serverData = req.getC2SServerData();
		String chatTime = DateUtil.getDateTime();
		int scope = req.getC2SScope();
		String target = req.getC2SAcceptRoleId();

		if (ChatBacklistManager.getInstance().inBacklist(player.getPlayer().ip)) {
			Out.warn("命中聊天黑名单. ip=", player.getPlayer().ip, ",playerId=", player.getId(), ",name=", player.getName());
			return new ErrorResponse("系统异常，请联系客服");
		}

		if (scope == CHAT_SCOPE.TEAM_CALL.getValue()) {
			if (!player.getTeamManager().isInTeam()) {
				return new ErrorResponse(LangService.getValue("CHAT_NOT_IN_TEAM"));
			}
			// 队伍喊话
			String msg = teamCall(player, text, serverData, chatTime, scope);
			if (msg != null) {
				return new ErrorResponse(msg);
			}
		} else {
			// GM 命令
			if ((GWorld.DEBUG || GWorld.ROBOT) && text.toLowerCase().startsWith("@gm")) {
				GMChatResult gmRes = GMChatUtil.checkContent(player, text);
				text = gmRes.content;
				return new ErrorResponse(text);
			}

			Date forbidTalkTime = player.getPlayer().forbidTalkTime;
			// 被禁言了
			if (forbidTalkTime != null && forbidTalkTime.getTime() > System.currentTimeMillis()) {
				return new ErrorResponse(LangService.getValue("CHAT_PLAYER_SHUTUP"));
			}
			int sys = 0;
			if (scope == CHAT_SCOPE.SYSTEM.getValue()) {
				return new ErrorResponse(LangService.getValue("CHAT_SYS_SCOPE_ERR"));
			} else if (scope == CHAT_SCOPE.DAOYOU.getValue()) {
				DaoYouMemberPO dymp = DaoYouCenter.getInstance().getDaoYouMember(player.getId());
				if (dymp == null) {
					return new ErrorResponse(LangService.getValue("CHAT_DAOYOU_ERR"));
				}
			} else if (scope == CHAT_SCOPE.GUILD.getValue()) {
				if (player.guildManager.guild == null) {
					return new ErrorResponse(LangService.getValue("CHAT_GUILD_ERR"));
				}
			} else if (scope == CHAT_SCOPE.TEAM.getValue()) {
				if (!player.getTeamManager().isInTeam()) {
					return new ErrorResponse(LangService.getValue("CHAT_NOT_IN_TEAM"));
				}
			} else if (scope == CHAT_SCOPE.HORM.getValue()) {
				sys = 2;
				String itemCode = GlobalConfig.HornCode;
				int itemNum = GlobalConfig.HornCount;
				if (player.bag.findItemNumByCode(itemCode) < itemNum) {
					player.onFunctionGoTo(FUNCTION_GOTO_TYPE.LOUD_NOT_ENOUGH, itemCode, null, null);
					return new ErrorResponse(LangService.getValue("ITEM_LOUD_NOT_ENOUGH"));
				}
				scope = CHAT_SCOPE.WORLD.getValue();
			}

			// 调试状态不判定等级与聊天CD
			if (!GWorld.DEBUG) {
				ChatSettingCO setting = GameData.ChatSettings.get(scope);
				if (setting != null) {
					if (setting.openLv > player.getLevel()) {
						return new ErrorResponse(LangService.format("CHAT_LV_LESS", setting.openLv));
					}
					if (player.chatTime == null) {
						player.chatTime = new HashMap<>();
					}
					Long lasttime = player.chatTime.get(scope);
					long currTime = System.currentTimeMillis();
					if (lasttime != null) {
						long second = setting.coolDown - (currTime - lasttime) / GGlobal.TIME_SECOND;
						if (second > 0) {
							return new ErrorResponse(LangService.format("CHAT_WORLD_COOL", second));
						}
					}
					player.chatTime.put(scope, currTime);
				}
			}

			JSONObject json = JSONObject.parseObject(serverData);
			if ("toushaizi".equals(text)) {
				if (json.containsKey("s2c_funtype") && json.getIntValue("s2c_funtype") == 1) {
					sys = 3;
					text = LangService.format("CHAT_DICE", player.getName(), RandomUtil.getInt(100));
				}
			} else {
				boolean ignoreCode = ChannelUtil.extractChatItem(player, text);
				text = BlackWordUtil.replaceBlackString(text, ignoreCode);
			}

			// 简单校验一下ServerData，如果发现篡改就修正回来
			serverData = this.checkServerData(player, serverData, json);

			OnChatPush.Builder push = OnChatPush.newBuilder();
			push.setS2CContent(text);
			push.setS2CPlayerId(player.getId());
			push.setS2CServerData(serverData);
			push.setS2CScope(scope);
			push.setS2CUid(pak.getUid());
			push.setS2CTime(chatTime);
			push.setS2CIndex(0);
			push.setS2CSys(sys);

			if (scope == CHAT_SCOPE.PRIVATE.getValue()) {
				push.setS2CAcceptRid(req.getC2SAcceptRoleId());
			}
			PomeloPush chatPush = new PomeloPush() {
				@Override
				protected void write() throws IOException {
					body.writeBytes(push.build().toByteArray());
				}

				@Override
				public String getRoute() {
					return "chat.chatPush.onChatPush";
				}
			};

			if (scope == CHAT_SCOPE.PRIVATE.getValue()) {
				String acceptRid = req.getC2SAcceptRoleId();
				WNPlayer receiver = GWorld.getInstance().getPlayer(acceptRid);
				if (receiver != null) {
					receiver.receive(chatPush);
				}
				player.receive(chatPush);

				ChatMsg msg = new ChatMsg();
				msg.playerId = player.getId();
				msg.acceptRid = acceptRid;
				msg.content = text;
				msg.time = chatTime;
				// 更新好友聊天记录
				RecentChatCenter.getInstance().getRecentChatMgr(acceptRid).addRecentMsg(player.getId(), msg);

				// 更新自己的聊天记录
				player.getRecentChatMgr().addRecentMsg(acceptRid, msg);

				target = (receiver == null ? acceptRid : receiver.getName());
				Out.info("[私聊]【", player.getName(), "】对【", target, "】说：", req.getC2SContent());
			} else if (scope == CHAT_SCOPE.TEAM.getValue()) {
				Out.info("[队伍]【", player.getName(), "】说：", req.getC2SContent());
				sendTeam(player, chatPush);
			} else if (scope == CHAT_SCOPE.DAOYOU.getValue()) {
				DaoYouService.getInstance().sendDaoYou(player, chatPush);
			} else if (scope == CHAT_SCOPE.GUILD.getValue()) {
				Out.info("[公会]【", player.getName(), "】说：", req.getC2SContent());
				GuildUtil.broadcast(player.guildManager.getGuildId(), chatPush);
			} else {
				if (req.getC2SScope() == CHAT_SCOPE.HORM.getValue()) {
					Out.info("[喇叭]【", player.getName(), "】说：", req.getC2SContent());
					player.bag.discardItem(GlobalConfig.HornCode, GlobalConfig.HornCount, GOODS_CHANGE_TYPE.chat);
				} else {
					Out.info("[世界]【", player.getName(), "】说：", req.getC2SContent());
				}
				GWorld.getInstance().broadcast(chatPush, player.getLogicServerId());
				// 成就
				player.achievementManager.onWorldSpeakTimes();
			}
		}

		// 异步发布给全局Redis.
		ChatMonitor monitor = new ChatMonitor();
		monitor.setSid(GWorld.__SERVER_ID);
		monitor.setDate(new Date());
		monitor.setId(player.getId());
		monitor.setName(player.getName());
		monitor.setLevel(player.getLevel());
		monitor.setPro(player.getPro());
		monitor.setScope(req.getC2SScope());
		monitor.setText(req.getC2SContent());
		monitor.setTarget(target);
		monitor.setIp(pak.getIp());
		GWorld.getInstance().ansycExec(() -> {
			PublishCenter.publish(MonitorConst.REDIS_PUBLISH_CHAT_MONITOR, JSON.toJSONString(monitor));
		});

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SendChatResponse.Builder res = SendChatResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	private String checkServerData(WNPlayer player, String serverData, JSONObject json) {
		// {"acceptRoleId":"","s2c_level":1,"s2c_isAtAll":0,"s2c_titleMsg":"","s2c_name":"星冼墨","s2c_pro":3,"s2c_vip":0,"s2c_zoneId":"256","s2c_color":3723689983}
		int level = player.getLevel();
		String name = player.getName();
		int pro = player.getPro();
		int vip = player.baseDataManager.getVip();
		json.put("s2c_level", level);
		json.put("s2c_name", name);
		json.put("s2c_pro", pro);
		json.put("s2c_vip", vip);
		json.put("s2c_zoneId", player.getLogicServerId());
		return json.toJSONString();
	}

	private void sendTeam(WNPlayer player, PomeloPush chatPush) {
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			team.receive(chatPush);
		}
	}

	private String teamCall(WNPlayer player, String text, String serverData, String chatTime, int scope) {
		if (!player.getTeamManager().getTeamMember().isLeader)
			return LangService.format("TEAM_NO_AUTHORITY");
		Long callTime = pak.getAttr(INVITE_CALL);
		long currTime = System.currentTimeMillis();
		if (callTime != null) {
			long second = 60 - (currTime - callTime) / GGlobal.TIME_SECOND;
			if (second > 0) {
				return LangService.format("TEAM_CALL_COOL", second);
			}
		}
		// int ps = text.indexOf(":[") + 1;
		// int pe = text.indexOf("]", ps) + 1;
		// String pro = text.substring(ps, pe);
		// JSONArray arr = JSON.parseArray(pro);
		pak.setAttr(INVITE_CALL, currTime);
		Collection<GPlayer> players = GWorld.getInstance().getOnlinePlayers().values();
		OnChatPush.Builder push = OnChatPush.newBuilder();
		push.setS2CContent(text);
		push.setS2CPlayerId(player.getId());
		push.setS2CServerData(serverData);
		push.setS2CScope(CHAT_SCOPE.TEAM.getValue());
		push.setS2CUid(pak.getUid());
		push.setS2CTime(chatTime);
		push.setS2CIndex(0);
		push.setS2CSys(scope);
		PomeloPush chatPush = new PomeloPush() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(push.build().toByteArray());
			}

			@Override
			public String getRoute() {
				return "chat.chatPush.onChatPush";
			}
		};
		for (GPlayer freer : players) {
			WNPlayer receiver = (WNPlayer) freer;
			if (receiver.getLevel() < GlobalConfig.Team_Min_Level)
				continue;
			if (receiver.getTeamManager().getTeam() == player.getTeamManager().getTeam() || !receiver.getTeamManager().isInTeam()) {
				freer.receive(chatPush);
			}
		}
		Out.info("[队伍]【", player.getName(), "】说：", text);
		// player.receive(chatPush);
		return null;
	}

	public short getType() {
		return 0x201;
	}

}
