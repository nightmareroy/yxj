package com.wanniu.game.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sysSet.SysSetFlag;

import pomelo.area.MessageHandler.OnMessageAddPush;

public class PlayerMessages {

	private WNPlayer player;

	public Map<Integer, Map<String, MessageData>> sendedMessages;
	public Map<Integer, Map<String, MessageData>> receivedMessages;

	public PlayerMessages(WNPlayer player) {
		this.player = player;
		this.sendedMessages = new ConcurrentHashMap<>();
		this.receivedMessages = new ConcurrentHashMap<>();
	}

	/**
	 * 获取消息 params : type 类型 messageId 消息id
	 */
	public final MessageData getReceivedMessage(int type, String messageId) {
		Map<String, MessageData> messages = this.receivedMessages.get(type);
		if (messages != null) {
			MessageData message = messages.get(messageId);
			if (message != null && !message.isPastDue()) {
				return message;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public final boolean addSendedMessage(MessageData message) {
		if (message != null && !message.isPastDue()) {
			int count = this.clearPastDueMessages(false, message.messageType);

			int sendLimit = MessageUtil.getSendLimit(message.getMessageType());
			if (sendLimit > 0 && count >= sendLimit) {
				return false;
			}

			Map<String, MessageData> messages = this.sendedMessages.get(message.messageType);
			if (messages == null) {
				messages = new ConcurrentHashMap<>();
				this.sendedMessages.put(message.messageType, messages);
			}
			messages.put(message.id, message);
		}
		return true;
	}

	public final int getSendedMessageLengthByType(int type) {
		Map<String, MessageData> messages = this.getSendedMessageByType(type);
		int count = 0;
		if (messages != null) {
			count = count + messages.size();
		}
		return count;
	}

	public final Map<String, MessageData> getSendedMessageByType(int type) {
		this.clearPastDueMessages(false, type);
		Map<String, MessageData> messages = this.sendedMessages.get(type);
		if (messages != null) {
			return messages;
		}
		return new ConcurrentHashMap<>();
	}

	public final boolean addReceivedMessage(MessageData message) {
		if (message != null && !message.isPastDue()) {
			Out.debug("message data:", message);
			if (message.messageType == Const.MESSAGE_TYPE.team_invite.getValue() && !this.player.sysSetManager.isPermission(SysSetFlag.teamInviteSet)) {
				String msg = LangService.format("TEAM_REFUSE", this.player.getName());
				PlayerUtil.sendSysMessageToPlayer(msg, message.createPlayerId, null);
				MessageUtil.deleteSendedPlayerMessage(message);
			} else if (message.messageType == Const.MESSAGE_TYPE.team_invite.getValue() && this.player.getTeamManager().isAcceptAutoTeam() || message.messageType == Const.MESSAGE_TYPE.team_apply.getValue() && this.player.getTeamManager().isAutoTeam()) {
				return this.player.getTeamManager().onMessage(message.messageType, Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue(), message.id);
			} else {
				int count = this.clearPastDueMessages(true, message.messageType);
				if (count < Const.MESSAGE.MAX_COUNT.getValue()) {
					if (message.messageType != Const.MESSAGE_TYPE.team_invite.getValue() && message.messageType != Const.MESSAGE_TYPE.team_invite.getValue()) {
						Map<String, MessageData> messages = this.receivedMessages.get(message.messageType);
						if (messages == null) {
							messages = new ConcurrentHashMap<>();
							this.receivedMessages.put(message.messageType, messages);
						}
						messages.put(message.id, message);
					}
					OnMessageAddPush.Builder builder = OnMessageAddPush.newBuilder();
					builder.setS2CCode(PomeloRequest.OK);
					builder.setS2CData(message.toJson4PayLoad());
					player.receive("area.messagePush.onMessageAddPush", builder.build());
					return true;
				}
			}
		}
		return false;
	}

	public final void deleteReceivedMessage(int type, String id) {
		Map<String, MessageData> messages = this.receivedMessages.get(type);
		if (messages != null) {
			messages.remove(id);
			this.receivedMessages.put(type, messages);
		}
	}

	public final void deleteSendedMessage(int type, String id) {
		Map<String, MessageData> messages = this.sendedMessages.get(type);
		if (messages != null) {
			messages.remove(id);
			this.sendedMessages.put(type, messages);
		}
	}

	/**
	 * 清除过期消息 params : isReceived 已发送或已接收 type 消息类型 return : 经过清理后对应的消息类型数据数量是否已达到最大
	 */
	public final int clearPastDueMessages(boolean isReceived, int type) {
		Map<Integer, Map<String, MessageData>> messageAll = isReceived ? receivedMessages : sendedMessages;
		if (messageAll == null)
			return 0;

		Map<String, MessageData> messages = messageAll.get(type);
		int count = 0;
		if (messages != null) {
			for (Map.Entry<String, MessageData> node : messages.entrySet()) {
				MessageData message = node.getValue();
				if (message.isPastDue()) {
					messages.remove(node.getKey());
				} else {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 凌晨清理过期消息
	 */
	public final void refreshNewDay() {
		this._clearNewDayPastDueMessages(true);
		this._clearNewDayPastDueMessages(false);
	}

	/**
	 * 清除过期消息 params : isReceived 已发送或已接收
	 */
	public final void _clearNewDayPastDueMessages(boolean isReceived) {
		Map<Integer, Map<String, MessageData>> messageAll = isReceived ? receivedMessages : sendedMessages;
		if (messageAll == null)
			return;
		for (Map.Entry<Integer, Map<String, MessageData>> node : messageAll.entrySet()) {
			Map<String, MessageData> messages = node.getValue();
			for (Map.Entry<String, MessageData> message : messages.entrySet()) {
				if (message.getValue().isClearByNewDay()) {
					messages.remove(message.getKey());
				}
			}
			messageAll.put(node.getKey(), messages);
		}
		if (isReceived) {
			this.receivedMessages = messageAll;
		} else {
			this.sendedMessages = messageAll;
		}
	}

}
