package com.wanniu.game.guild.guildFort;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.GuildJob;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.chat.ChatHandler.OnChatPush;

public class GuildFortUtil {	
		
	/**
	 * Send mail to all guild managers(include president and vice president) when bid time bigen
	 */
	public static void mailToAllGuildManagerWhenBidBegin() {		
		List<String> playerIds = GuildUtil.getMemberIds(GuildJob.PRESIDENT,GuildJob.VICE_PRESIDENT);
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortNotifyBidBegin);
		mailData.replace = new HashMap<>();
		mailData.replace.put("begintime", GuildFortService.getInstance().getBattleBeginTimeString());
		mailData.replace.put("endtime", GuildFortService.getInstance().getBattleEndTimeString());

		MailUtil.getInstance().sendMailToSomePlayer(playerIds.toArray(new String[playerIds.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	
	/**
	 * Refresh red point of guild fort to all online players
	 */
	public static void pushRedPointToAll() {
		for (GPlayer gPlayer : PlayerUtil.getAllOnlinePlayer()) {
			((WNPlayer) gPlayer).guildFortManager.pushRedPoint();
		}
	}
	
	public static void pushRedPoint(Set<String> players) {
		for(String playerId : players) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if(player!=null) {
				player.guildFortManager.pushRedPoint();
			}
		}
	}
	
	
	/**
	 * 异步发送顶部跑马灯消息并且在同步在【世界频道】显示给所有在线玩家
	 * @param msg
	 */
	public static void sendRollTipsToAllAnsy(String msg) {
		sendRollTipsToAllAsyn(msg,Const.CHAT_SCOPE.WORLD);
	}
	
	/**
	 * 异步发送顶部跑马灯消息并且在同步在指定的scope频道范围里显示给所有在线玩家
	 * @param msg
	 */
	public static void sendRollTipsToAllAsyn(String msg,Const.CHAT_SCOPE scope) {
		GWorld.getInstance().ansycExec(() -> {
			for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
				WNPlayer wp = (WNPlayer) p;
				MessageUtil.sendSysTip(wp,msg,Const.TipsType.ROLL);
				sendChatMsg(wp,msg,scope,TipsType.BLACK);
			}	
		});
	}
	
	/**
	 * 异步发送msg消息给指定playerIds
	 * @param playerIds
	 * @param msg
	 * @param scope 只有系统频道和世界频道才会滚动播报消息
	 */
	public static void sendRollTipsAsyn(Set<String> playerIds,String msg,Const.CHAT_SCOPE scope) {
		GWorld.getInstance().ansycExec(() -> {
			for(String playerId : playerIds) {
				WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
				if(player!=null) {
					if(scope == CHAT_SCOPE.SYSTEM || scope==CHAT_SCOPE.WORLD) {
						MessageUtil.sendSysTip(player,msg,Const.TipsType.ROLL);
					}
					sendChatMsg(player,msg,scope,TipsType.BLACK);
				}
			}
		});
	}
	
	private static void sendChatMsg(WNPlayer player, String content, Const.CHAT_SCOPE scope, TipsType scroll) {
		OnChatPush.Builder msg = OnChatPush.newBuilder();
		msg.setS2CPlayerId(player.getPlayer().id);
		msg.setS2CUid(player.getPlayer().uid);
		msg.setS2CContent(StringUtil.isEmpty(content) ? "content is null" : content);
		msg.setS2CScope(scope.getValue());
		msg.setS2CSys(scroll.getValue());
		msg.setS2CTime(DateUtil.getDateTime());
		
//		Map<String, Object> serverData = new HashMap<>();
//		serverData.put("acceptRoleId", "");
//		serverData.put("s2c_level", player.getLevel());
//		serverData.put("s2c_name", player.getPlayer().name);
//		serverData.put("s2c_pro", player.getPro());
//		serverData.put("s2c_vip", 0);
//		serverData.put("s2c_zoneId", player.getLogicServerId());
//		msg.setS2CServerData(JSON.toJSONString(serverData));
//		The problem is rolling message at bottom on the screen
		
		msg.setS2CServerData("{}");
		
		player.receive("chat.chatPush.onChatPush", msg.build());
	}
}
