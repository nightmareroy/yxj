package com.wanniu.game.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.MailAttachments;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerMailDataPO;

import pomelo.area.MailHandler.Mail;
import pomelo.area.PlayerHandler.SuperScriptType;

public class MailManager extends ModuleManager {

	public static enum ERR_CODE {
		ERR_CODE_OK(0), ERR_CODE_BAG_FULL(1), ERR_CODE_NO_ATTACH(2), ERR_CODE_NO_SUCH_MAIL(3);
		private int value;

		private ERR_CODE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	};

	public Map<String, WNMail> mails;
	public String playerId;
	public PlayerMailDataPO data;

	public WNPlayer getPlayer() {
		return PlayerUtil.getOnlinePlayer(playerId);
	}

	public MailManager(String playerId, PlayerMailDataPO data) {
		this.data = data;
		this.playerId = playerId;
		this.mails = data.mails;
	}

	// public MailManager(String playerId, Map<String, WNMail> mails) {
	// this.playerId = playerId;
	// this.mails = mails;
	// }

	public void addServerMailRecord(String serverMailId) {
		data.serverMailRecord.put(serverMailId, serverMailId);
	}

	/**
	 * 获取未读邮件数量 用于hud脚标显示
	 * 
	 * @returns int[2]
	 */
	public final int[] getUnReadMailCount() {
		int[] retValue = new int[2];
		for (WNMail mail : mails.values()) {
			if (mail.status == Const.MailState.MAIL_STATE_NULL.getValue()) {
				retValue[Const.MailStaus.MAIL_UNREAD.getValue()]++;
			}
			if (this.hasAttachment(mail)) {
				retValue[Const.MailStaus.MAIL_UNRECEIVE.getValue()]++;
			}
		}
		return retValue;
	}

	/**
	 * 是否有附件
	 * 
	 * @param mail
	 * @returns {boolean}
	 */
	public final boolean hasAttachment(WNMail mail) {
		if (mail.attachment == null || mail.attachment.size() == 0) {
			return false;
		}
		return true;
	}

	public final int getMailCount() {
		return this.mails.size();
	}

	/**
	 * 获取排序的邮件列表
	 */
	private final ArrayList<WNMail> getSortMailList() {
		ArrayList<WNMail> list = new ArrayList<>();
		for (Map.Entry<String, WNMail> node : mails.entrySet()) {
			list.add(node.getValue());
		}
		Collections.sort(list, new Comparator<WNMail>() {
			@Override
			public int compare(WNMail arg0, WNMail arg1) {
				return arg0.compareTo(arg1);
			}
		});
		return list;
	}

	/**
	 * 添加一封新的邮件 如果超过上限就会依据 有附件 > 未读 > 已读 > 已领取 > 时间 权重规则删除老邮件
	 * 
	 * @param newMail
	 * @param player
	 * @param isUpdateScript
	 * @param force
	 * @returns {boolean}
	 */
	public final boolean addMail(WNMail mail, boolean isUpdateScript) {
		WNPlayer player = getPlayer();
		if (player != null && player.isRobot()) {
			return false;
		}
		if (this.mails.containsKey(mail.id)) {
			return false;
		}
		if (this.mails.size() >= Const.MailSysParam.MAIL_MAX_NUM.getValue()) {
			// 数量超出删除邮件
			ArrayList<WNMail> mailArray = this.getSortMailList();
			int delCount = this.mails.size() - Const.MailSysParam.MAIL_MAX_NUM.getValue() + 1;
			for (int i = 0; i < delCount; i++) {
				this.mailDelete(mailArray.get(mailArray.size() - 1).id, true);
			}

			// for (int i = 0; i < delCount; ++i) {
			// this.mailDelete(mailArray.get(i).id, true);
			// }
		}

		this.mails.put(mail.id, mail);
		Out.info("新增邮件 playerId=", mail.playerId, ",mailId=", mail.id, ",attachment=", JSON.toJSONString(mail.attachment));

		if (player == null) {
			return false;
		}

		Mail data = mail.toMailBuilder(player.getPlayerAttach()).build();
		ArrayList<Mail> list = new ArrayList<>();
		list.add(data);

		WNNotifyManager.getInstance().pushMails(player, list);

		if (isUpdateScript) {
			this.updateSuperScript();
			CommonUtil.sendIconMsgType(Const.MESSAGE_TYPE.mail_receive, player.getId());
		}

		// if (getPlayer() != null) {
		// // 发送通知消息 MessageUtil
		// MessageData message =
		// MessageUtils.getInstance().createMessage(Const.MESSAGE_TYPE.mail_receive.getValue(),
		// this.playerId, null, null);
		// if (getPlayer().messageManager.addSendedMessage(message)) {
		// MessageUtils.getInstance().sendMessageToPlayer(this.playerId, message,
		// this.playerId);
		// }
		// }

		// TODO
		// if(playerUtil.isOnline(player.id)){
		// Out.debug("mail player is online");
		// this.emit("update");
		// }else{
		// Out.debug("mail player is not online");
		// playerUtil.updatePlayer(player);
		// }

		Out.debug("mail count :", this.getMailCount());
		return true;
	}

	/**
	 * 读取邮件 邮件为阅后即删除此邮件
	 * 
	 * @param ids
	 */
	public final void readMail(String[] ids) {
		if (ids != null) {
			for (String key : ids) {
				WNMail mail = this.mails.get(key);
				if (mail != null) {
					if (mail.mailRead == Const.MailReadDeal.MAIL_READ_DEL.getValue()) {
						mail.status = Const.MailState.MAIL_STATE_READ.getValue();
						if (!this.hasAttachment(mail)) {
							this.mailDelete(mail.id, false);
						}
					} else if (mail.status == Const.MailState.MAIL_STATE_NULL.getValue()) {
						mail.status = Const.MailState.MAIL_STATE_READ.getValue();
						this.updateSuperScript();
					}

					// TODO 统计
					// getPlayer().biServerManager.mailOperation(1, {1:
					// mail.mailSender, 2: getPlayer().name}, {});
				}
			}
		}

	}

	/**
	 * 删除邮件 force 为false时 要已读(附件已领取)才能删除
	 * 
	 * @param id
	 * @param force true/false 是否强制删除
	 * @returns {boolean}
	 */
	public final boolean mailDelete(String id, boolean isForce) {
		WNMail mail = this.mails.get(id);
		boolean isDelete = false;
		if (mail != null) {
			if (isForce) {
				isDelete = true;
			} else {
				if (mail.status == Const.MailState.MAIL_STATE_ATTACH_RECEIVE.getValue()) {
					isDelete = true;
				}
				if (mail.status == Const.MailState.MAIL_STATE_READ.getValue() && !this.hasAttachment(mail)) {
					isDelete = true;
				}
			}
			if (isDelete) {
				this.mails.remove(id);
				Out.info("删除邮件 playerId=", mail.playerId, ",mailId=", mail.id, ",isForce=", isForce);
				this.updateSuperScript();
				return true;
			}
		}

		return false;
	}

	/**
	 * 一键删除邮件 一键删除的邮件为已读(附件已经领取)
	 * 
	 * @returns String[] 删除的id
	 */
	public final String[] mailDeleteOneKey() {
		List<String> deleteIds = new ArrayList<>();
		for (Map.Entry<String, WNMail> node : mails.entrySet()) {
			WNMail mail = node.getValue();
			String id = node.getKey();
			if (mail.status == Const.MailState.MAIL_STATE_ATTACH_RECEIVE.getValue()) {
				deleteIds.add(id);
			}

			if (mail.status == Const.MailState.MAIL_STATE_READ.getValue() && !this.hasAttachment(mail)) {
				deleteIds.add(id);
			}
		}
		String[] ids = new String[deleteIds.size()];
		int index = 0;
		for (String id : deleteIds) {
			mails.remove(id);
			ids[index++] = id;
		}
		deleteIds.clear();
		this.updateSuperScript();
		return ids;
	}

	/**
	 * 获取附件 附件为阅后即焚 要删除此邮件
	 * 
	 * @param id
	 * @param isUpdate
	 * @returns int
	 */
	public final int mailGetAttachment(String id, boolean isUpdate) {
		if (!this.mails.containsKey(id)) {
			return ERR_CODE.ERR_CODE_NO_SUCH_MAIL.getValue();
		}
		WNMail mail = mails.get(id);
		if (!this.hasAttachment(mail)) {
			return ERR_CODE.ERR_CODE_NO_ATTACH.getValue();
		}
		List<PlayerItemPO> attachment = mail.attachment;

		// 判断玩家背包是否已满
		if (!getPlayer().getWnBag().testAddEntityItemsPO(attachment, true)) {
			return ERR_CODE.ERR_CODE_BAG_FULL.getValue();
		}

		Out.info("领取邮件附件 playerId=", getPlayer().getId(), ",mailId=", mail.id, ",attachment=", JSON.toJSONString(attachment));
		// 添加物品
		GOODS_CHANGE_TYPE origin = GOODS_CHANGE_TYPE.getE(mail.origin);
		getPlayer().getWnBag().addEntityItemsPO(attachment, origin == null ? GOODS_CHANGE_TYPE.mail : origin);
		mail.status = Const.MailState.MAIL_STATE_ATTACH_RECEIVE.getValue();
		mail.attachment = new ArrayList<>();
		if (mail.mailRead == Const.MailReadDeal.MAIL_READ_DEL.getValue()) {
			this.mailDelete(id, false);
		}

		if (StringUtil.isNotEmpty(mail.mailSubType) && mail.mailSubType.equals(SysMailConst.CONSIGNMENT_SALE)) {
			for (PlayerItemPO item : attachment) {
				if (item.code.equals("diamond")) {
					// 成就管理
					getPlayer().achievementManager.onGetDiamondInConsignment(item.speData.worth);
				}
			}
		}

		for (PlayerItemPO item : attachment) {
			if (item.code.equals("diamond")) {
				if (item.speData.worth >= 6480) {
					PlayerUtil.bi(getClass(), Const.BiLogType.Mail, getPlayer(), id, mail.mailSenderId, item.speData.worth);
				}
			} else {
				DItemEquipBase prop = ItemConfig.getInstance().getItemProp(item.code);
				if (prop != null && prop.qcolor >= Const.ItemQuality.ORANGE.getValue() && ItemUtil.isEquipByItemType(prop.itemType)) {
					StringBuffer sb = new StringBuffer();
					if (item.speData != null) {
						ItemSpeData speData = item.speData;
						if (speData != null) {
							sb.append(speData.baseAtts.toString());
							if (speData.extAtts != null) {
								sb.append("|||").append(speData.extAtts.toString());
							}
							if (speData.legendAtts != null) {
								sb.append("|||").append(speData.legendAtts.toString());
							}
						}
					}
					PlayerUtil.bi(getClass(), Const.BiLogType.Mail, getPlayer(), id, mail.mailSenderId, item.id, item.code, sb.toString());
				}
			}
		}

		if (!isUpdate) {
			return ERR_CODE.ERR_CODE_OK.getValue();
		}
		// this.updateDB(mail);
		this.updateSuperScript();
		return ERR_CODE.ERR_CODE_OK.getValue();
	}

	/**
	 * 一键领取 逐个领取,直到背包已满
	 * 
	 * @returns MailAttachments
	 */
	public final MailAttachments mailGetAttachmentOneKey() {
		MailAttachments mailAttachments = new MailAttachments();
		List<String> ids = new ArrayList<>();
		String[] idstr = new String[mails.size()];
		idstr = mails.keySet().toArray(idstr);
		for (String id : idstr) {
			int code = this.mailGetAttachment(id, false);
			if (code == ERR_CODE.ERR_CODE_OK.getValue()) {
				ids.add(id);
			} else if (code == ERR_CODE.ERR_CODE_BAG_FULL.getValue()) {
				mailAttachments.code = code;
				break;
			} else {
				mailAttachments.code = code;
			}
		}
		// for (Map.Entry<String, WNMail> node : mails.entrySet()) {
		// String id = node.getKey();
		//
		// }
		mailAttachments.mailIds = ids;
		this.updateSuperScript();
		return mailAttachments;
	}

	public final boolean onMessage(int operate, MessageData message) {
		if (message.getMessageType() == Const.MESSAGE_TYPE.mail_receive.getValue()) {
			return false;
		}
		return true;
	}

	/** 查找邮件 */
	public final WNMail getMailByID(String id) {
		if (this.mails.containsKey(id)) {
			return this.mails.get(id);
		}
		return null;
	}

	/** 更新角标 */
	public final void updateSuperScript() {
		if (getPlayer() != null)
			getPlayer().updateSuperScriptList(getSuperScript());
	}

	public final List<SuperScriptType> getSuperScript() {
		int[] countInfo = this.getUnReadMailCount();
		int number = 0;
		if (countInfo[Const.MailStaus.MAIL_UNREAD.getValue()] > 0) {
			number = countInfo[Const.MailStaus.MAIL_UNREAD.getValue()];
		} else if (countInfo[Const.MailStaus.MAIL_UNRECEIVE.getValue()] > 0) {
			number = countInfo[Const.MailStaus.MAIL_UNRECEIVE.getValue()];
		}

		List<SuperScriptType> list = new ArrayList<>();
		SuperScriptType.Builder script = SuperScriptType.newBuilder();
		script.setType(Const.SUPERSCRIPT_TYPE.MAIL.getValue());
		script.setNumber(number);
		list.add(script.build());
		return list;
	}

	/**
	 * 序列化数据给玩家
	 * 
	 * @returns {Array}
	 */
	public final Mail[] getAllMails(WNPlayer player) {
		Mail[] mailArray = new Mail[this.mails.size()];
		ArrayList<WNMail> list_mailArray = this.getSortMailList();
		int index = 0;
		for (WNMail mail : list_mailArray) {
			mailArray[index++] = mail.toMailBuilder(player.getPlayerAttach()).build();
		}
		// int index = 0;
		// for (Map.Entry<String, WNMail> node : this.mails.entrySet()) {
		// WNMail wMail = node.getValue();
		// mailArray[index++] = wMail.toMailBuilder().build();
		// }
		return mailArray;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case AFTER_LOGIN:
			MailCenter.getInstance().checkServerMail(this);
			break;

		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.MAIL;
	}

}
