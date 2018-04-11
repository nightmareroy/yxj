package com.wanniu.game.consignmentShop;

import java.util.ArrayList;
import java.util.HashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.MESSAGE_TYPE;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SaleReviewTimeCO;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.MailCenter;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.item.ItemOuterClass.ConsignmentItem;

public class ConsignmentUtil {

	/**
	 * 托管费
	 * 
	 * @param price
	 * @returns {number}
	 */
	public static int depositPrice(int price) {
		int ratio = GlobalConfig.Consignment_Fee;
		int min = GlobalConfig.Consignment_MinFee;
		return Math.max(min, (int) Math.floor(price * ratio / 100));
	}

	/**
	 * 购买佣金
	 */
	public static int commissionPrice(int globalZone, int price) {
		int ratio = GlobalConfig.Consignment_Commission;
		if (globalZone == 1) {
			ratio = GlobalConfig.Consignment_Commission_serverEnter;
		}
		int min = GlobalConfig.Consignment_MinCommission;
		return Math.max(min, (int) Math.floor(price * ratio / 100));
	}

	/**
	 * 最低寄卖价格
	 */
	public static int consignmentMinPrice(int price) {
		int min = GlobalConfig.Consignment_MinPrice;
		return Math.max(price, min);
	}

	/**
	 * 最大寄卖价格
	 */
	public static int consignmentMaxPrice(int price) {
		int max = GlobalConfig.Consignment_MaxPrice;
		return Math.min(price, max);
	}

	/**
	 * 上架数目
	 */
	public static int sellNum(WNPlayer player) {
		int orgin = GlobalConfig.Consignment_SellNum;
		int vip = player.baseDataManager.getVip();
		int add = 0;
		if (vip > 0) {
			add = GameData.Cards.get(vip).prv4;
		}
		return orgin + add;
	}

	/**
	 * 寄卖时长
	 * 
	 * @returns {*}
	 */
	public static int sellTime() {
		return GlobalConfig.Consignment_SellTime * Const.Time.Hour.getValue();
	}

	public static int getConsignmentLevel() {
		return GlobalConfig.Consignment_Level;
	}

	public static String sysItemId = "0000000";

	/**
	 * 获取延迟上架时间
	 * 
	 * @return
	 */
	public static int getLateMinutes(int price) {
		int minutes = 0;
		for (SaleReviewTimeCO cfg : GameData.SaleReviewTimes.values()) {
			if (price >= cfg.minDiamond && price <= cfg.maxDiamond) {
				minutes = RandomUtil.getInt(cfg.minTime, cfg.maxTime);
				break;
			}
		}
		return minutes;
	}

	/**
	 * 寄卖超时邮件
	 */
	public static void timeOutMail(ConsignmentItemsPO v) {
		NormalItem actualItem = ItemUtil.createItemByDbOpts(v.db);
		String itemName = MessageUtil.itemColorName(actualItem.prop.qcolor, actualItem.prop.name);
		MailSysData mailData = new MailSysData(SysMailConst.CONSIGNMENT_SENDBACK);
		mailData.replace = new HashMap<>();
		mailData.replace.put("storeItem", itemName);
		mailData.entityItems = new ArrayList<>();
		mailData.entityItems.add(actualItem.itemDb);
		MailCenter.getInstance().sendOfflineMailToPlayers(new String[] { v.consignmentPlayerId }, mailData, Const.GOODS_CHANGE_TYPE.CONSIGNMENT_TIMEOUT);
	}

	/**
	 * 构建寄卖行协议对象
	 */
	public static ConsignmentItem buildConsignmentItem(WNPlayer player, ConsignmentItemsPO item) {
		NormalItem normalItem = ItemUtil.createItemByDbOpts(item.db);
		pomelo.item.ItemOuterClass.ConsignmentItem.Builder builder = pomelo.item.ItemOuterClass.ConsignmentItem.newBuilder();
		builder.setDetail(normalItem.getItemDetail(player.playerBasePO));
		builder.setGroupCount(item.groupCount);
		builder.setPublishTimes(item.publishTimes);
		builder.setConsignmentPlayerId(item.consignmentPlayerId);
//		builder.setConsignmentPlayerName(item.consignmentPlayerName);
		builder.setConsignmentPlayerName(GlobalConfig.Consignment_Anonymous_Show);
		builder.setConsignmentPrice(item.consignmentPrice);
		builder.setConsignmentPlayerPro(item.consignmentPlayerPro);

		long lateTime = item.lateMinutes * Const.Time.Minute.getValue();
		builder.setLateTime(lateTime);
		builder.setConsignmentTime(String.valueOf(item.consignmentTime));
		// builder.setLateTimes(item.consignmentTime + lateTime -
		// System.currentTimeMillis());
		// if (item.consignmentTime >= System.currentTimeMillis()) {
		// builder.setStatus(2);
		// } else if (item.consignmentTime < System.currentTimeMillis()) {
		// if (item.consignmentTime >= System.currentTimeMillis() -
		// ConsignmentUtil.sellTime() * Const.Time.Hour.getValue()) {
		// builder.setStatus(0);
		// } else {
		// builder.setStatus(1);
		// }
		// }
		return builder.build();
	}

	public static boolean onMessage(WNPlayer player, MESSAGE_TYPE msgType, int operate, MessageData message) {
		if (operate == Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue()) {
			Out.error(Utils.serialize(message));
		}
		return true;
	}
}
