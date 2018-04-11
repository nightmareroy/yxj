package com.wanniu.game.common.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.ShowTipUtil;
import com.wanniu.game.data.SocialMessageCO;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.MessageData.MessageData_Data;
import com.wanniu.game.message.SocialMessageConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.chat.ChatHandler.OnChatPush;

/**
 * 消息工具类
 */
public final class MessageUtil extends ShowTipUtil {

	public static void sendMessage(String playerId, String route, GeneratedMessage data) {
		WNPlayer player = GWorld.getInstance().getPlayer(playerId);
		if (player != null) {
			player.write(new MessagePush(route, data));
		}
	}

	/**
	 * 跑马灯消息 [客户端显示在下面的]
	 */
	public static void sendRollChat(int logicServerId, String content, Const.CHAT_SCOPE scope) {
		OnChatPush.Builder msg = OnChatPush.newBuilder();
		msg.setS2CPlayerId("");
		msg.setS2CUid("");
		msg.setS2CContent(StringUtil.isEmpty(content) ? "content is null" : content);
		msg.setS2CScope(scope != null ? scope.getValue() : CHAT_SCOPE.WORLD.getValue());
		msg.setS2CSys(TipsType.BLACK.getValue());
		msg.setS2CTime(DateUtil.getDateTime());
		msg.setS2CServerData("{}");

		GWorld.getInstance().broadcast(new MessagePush("chat.chatPush.onChatPush", msg.build()), logicServerId);
	}

		
	/**
	 * 聊天框消息
	 */
	public static void sendSysChat(WNPlayer player, String content) {
		sendSysChat(player, content, TipsType.NORMAL);
	}

	/**
	 * 跑马灯消息 [客户端显示在下面的]
	 */
	public static void sendSysChat(WNPlayer player, String content, TipsType scroll) {
		sendChatMsgAsyn(player, content, Const.CHAT_SCOPE.SYSTEM, scroll);
	}

	/**
	 * 异步发送跑马灯消息 [客户端显示在下面的]
	 */
	public static void sendChatMsgAsyn(WNPlayer player, String content, Const.CHAT_SCOPE scope, TipsType scroll) {
		GWorld.getInstance().ansycExec(() -> {
			OnChatPush.Builder msg = createChatMsg(player, content, scope, scroll);
			player.receive("chat.chatPush.onChatPush", msg.build());
		});
	}
	

	public static OnChatPush.Builder createChatMsg(WNPlayer player, String content, Const.CHAT_SCOPE scope, TipsType scroll) {
		OnChatPush.Builder msg = OnChatPush.newBuilder();
		Map<String, Object> serverData = new HashMap<>();
		serverData.put("acceptRoleId", "");
		serverData.put("s2c_level", player.getLevel());
		serverData.put("s2c_name", player.getPlayer().name);
		serverData.put("s2c_pro", player.getPro());
		serverData.put("s2c_vip", 0);
		serverData.put("s2c_zoneId", player.getLogicServerId()); // getZoneId(player.getLogicServerId())

		msg.setS2CPlayerId(player.getPlayer().id);

		msg.setS2CUid(player.getPlayer().uid);
		msg.setS2CContent(StringUtil.isEmpty(content) ? "content is null" : content);
		msg.setS2CScope(scope.getValue());
		msg.setS2CSys(scroll.getValue());
		msg.setS2CTime(DateUtil.getDateTime());
		msg.setS2CServerData(JSON.toJSONString(serverData));
		return msg;
	};

	
	
	/**
	 * 创建message messageType 调用consts.EventType
	 */
	public static MessageData createMessage(int messageType, String createPlayerId, MessageData_Data data) {
		return createMessage(messageType, createPlayerId, data, null);
	}

	public static MessageData createMessage(int messageType, String createPlayerId, MessageData_Data data, Map<String, String> strMsg) {
		MessageData opts = new MessageData();
		opts.messageType = messageType;
		opts.createPlayerId = createPlayerId;
		opts.strMsg = strMsg;
		opts.data = data;
		opts.init();
		return opts;
	}

	/**
	 * 获取发送数量上限
	 */
	public static int getSendLimit(int messageType) {
		SocialMessageCO socialMessageProp = SocialMessageConfig.getInstance().findMessageByMessageType(messageType);
		int limit = 0;
		if (socialMessageProp != null) {
			limit = socialMessageProp.sendLimite;
		}
		return limit;
	}

	/**
	 * 获取收取数量上限
	 */
	public static int getReceiveLimit(int messageType) {
		SocialMessageCO socialMessageProp = SocialMessageConfig.getInstance().findMessageByMessageType(messageType);
		int limit = 0;
		if (socialMessageProp != null) {
			limit = socialMessageProp.receiveLimite;
		}
		return limit;
	}

	/**
	 * 将消息发送给对应玩家 若玩家在本服务器，则直接推送，否则通过中心服务器对消息进行中转
	 */
	public static boolean sendMessageToPlayer(MessageData message, String playerId) {
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			return player.messageManager.addReceivedMessage(message);
		}
		return true;
	};

	//
	public static void deleteSendedPlayerMessage(MessageData message) {
		String playerId = message.createPlayerId;
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			player.messageManager.deleteSendedMessage(message.messageType, message.id);
		}
	};
}
