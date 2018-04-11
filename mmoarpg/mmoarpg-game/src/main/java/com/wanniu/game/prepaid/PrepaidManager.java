package com.wanniu.game.prepaid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.activity.ActivityManager.RewardRecord;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.SUPERSCRIPT_TYPE;
import com.wanniu.game.common.Const.VipType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.CardCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.PayCO;
import com.wanniu.game.data.SuperPackageCO;
import com.wanniu.game.data.ext.DailyPayExt.AwardInfo;
import com.wanniu.game.data.ext.FirstPayExt;
import com.wanniu.game.data.ext.TotalPayExt;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.ActivityDataPO;
import com.wanniu.game.poes.BagsPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.SevenGoalPO;
import com.wanniu.game.prepaid.po.PrepaidPO;
import com.wanniu.game.prepaid.po.PrepaidRecord;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.KeyValueStruct;
import pomelo.area.ActivityFavorHandler.SuperPackageBuyPush;
import pomelo.area.PrepaidHandler.FeeItem;
import pomelo.area.PrepaidHandler.PrepaidFirstItem;
import pomelo.area.PrepaidHandler.PrepaidFirstResponse;

public class PrepaidManager extends ModuleManager {
	public final String playerId;
	public PrepaidPO po;

	public PrepaidManager(String playerId) {
		this.playerId = playerId;
		this.po = this.loadPO(playerId);
	}

	private PrepaidPO loadPO(String playerId2) {
		PrepaidPO po = PlayerPOManager.findPO(ConstsTR.prepaidNewTR, playerId, PrepaidPO.class);
		if (po == null) {
			po = new PrepaidPO();
			PlayerPOManager.put(ConstsTR.prepaidNewTR, playerId, po);
		}
		return po;
	}

	/**
	 * 充值
	 * 
	 * @param productId
	 * @param isCard
	 * @param isSuperPackage 是否是超值礼包购买
	 */
	public void onCharge(int productId, boolean isCard, boolean isSuperPackage, boolean logBI) {
		final int oldPayMoney = po.total_charge;

		if (po.dailyDate != null && !DateUtils.isSameDay(new Date(), po.dailyDate)) {
			po.dailyPayRmb = 0;// 上次充值不是今天，清0
		}

		if (!isSuperPackage) {
			if (isCard) {
				chargeCard(productId, logBI);
			} else {
				chargeDiamond(productId, logBI);
			}
		} else {
			chargeDiamondOfSuperPackage(productId, logBI);
		}

		po.dailyDate = new Date();
		final int payRmb = (po.total_charge - oldPayMoney);
		po.dailyPayRmb += payRmb;// 今天累计充值人民币

		// 首充奖励
		if (oldPayMoney == 0) {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			List<FirstPayExt> props = GameData.findFirstPays((t) -> t.Job == baseData.pro);
			MailSysData d = new MailSysData(SysMailConst.PAY_FIRSTPAY);
			ArrayList<Attachment> list = new ArrayList<>();
			for (AwardInfo a : props.get(0).awards) {
				Attachment attachment = new Attachment();
				attachment.itemCode = a.itemCode;
				attachment.itemNum = a.itemNum;
				attachment.isBind = 1;
				list.add(attachment);
			}
			d.attachments = list;
			MailUtil.getInstance().sendMailToOnePlayer(playerId, d, GOODS_CHANGE_TYPE.FIRST_CHARGE);
		}

		// 其他累充奖励
		for (TotalPayExt i : GameData.TotalPays.values()) {
			if (oldPayMoney < i.target && po.total_charge >= i.target) {
				MailSysData d = new MailSysData(SysMailConst.Pay_TotalPay);
				ArrayList<Attachment> list = new ArrayList<>();
				for (AwardInfo a : i.awards) {
					Attachment attachment = new Attachment();
					attachment.itemCode = a.itemCode;
					attachment.itemNum = a.itemNum;
					attachment.isBind = 1;
					list.add(attachment);
				}
				d.attachments = list;
				MailUtil.getInstance().sendMailToOnePlayer(playerId, d, GOODS_CHANGE_TYPE.CUMULATIVE_CHARGE);
			}
		}

		if (getFirstPayStatus() == 2) {// 所有首充完成后通知隐藏首充按钮
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null) {
				player.updateSuperScript(SUPERSCRIPT_TYPE.FIRSTPAY_GIFT, 0);
			}
		}

		// 超值礼包奖励
		if (isSuperPackage) {
			ActivityDataPO activityDataPO = PlayerPOManager.findPO(ConstsTR.activityTR, playerId, ActivityDataPO.class);
			if (!activityDataPO.superPackageRecorder.containsKey(productId)) {
				RewardRecord rewardRecord = new RewardRecord();
				rewardRecord.awardId = productId;
				rewardRecord.awardTime = new Date();
				activityDataPO.superPackageRecorder.put(productId, rewardRecord);
				MailSysData d = new MailSysData(SysMailConst.SuperPackage_Item);
				ArrayList<Attachment> list = new ArrayList<>();
				SuperPackageCO superPackageCO = GameData.SuperPackages.get(productId);
				Attachment attachment = new Attachment();
				attachment.itemCode = superPackageCO.packageCode;
				attachment.itemNum = superPackageCO.packageNum;
				attachment.isBind = 1;
				list.add(attachment);
				d.attachments = list;
				MailUtil.getInstance().sendMailToOnePlayer(playerId, d, GOODS_CHANGE_TYPE.SUPER_PACKAGE);

				WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
				if (player != null) {
					SuperPackageBuyPush.Builder spbpBuilder = SuperPackageBuyPush.newBuilder();
					spbpBuilder.setS2CCode(PomeloRequest.OK);
					spbpBuilder.setPackageId(productId);
				}
			}
		}

		// 充值活动
		RechargeActivityService.getInstance().onPayEvent(playerId, po.dailyPayRmb, payRmb);
		
		//七日目标
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if(player!=null) {
			player.sevenGoalManager.processGoal(SevenGoalTaskType.PAY_COUNT);
		}
		else {
			SevenGoalPO sevenGoalPO=PlayerPOManager.findPO(ConstsTR.SevenGoal, playerId, SevenGoalPO.class);
			sevenGoalPO.processPayCount();
		}
		
	}

	public void onCharge(int productId, boolean isCard) {
		onCharge(productId, isCard, false, false);
	}

	/**
	 * 获取首充进度，0没有充值 1已经首冲 2已经完成所有首冲
	 * 
	 * @return
	 */
	public int getFirstPayStatus() {
		if (po.total_charge == 0) {
			return 0;
		} else {
			boolean totalFinished = true;
			for (TotalPayExt i : GameData.TotalPays.values()) {
				if (po.total_charge < i.target) {
					totalFinished = false;
					break;
				}
			}
			return totalFinished ? 2 : 1;
		}

	}

	/**
	 * 购买vip卡
	 */
	private void chargeCard(int productId, boolean logBI) {
		CardCO cardProp = GameData.Cards.get(productId);
		if (cardProp == null)
			return;
		int amount = cardProp.payMoneyAmount;
		po.total_charge += amount;
		po.dailyChargeDiamond += cardProp.payDiamond;
		PrepaidRecord record = new PrepaidRecord();
		record.money = amount;
		record.date = new Date();
		record.isCard = true;

		po.chargeRecord.add(record);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, cardProp.lastTime);
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			player.baseDataManager.modifyVip(cardProp.iD, cardProp.lastTime);
			player.moneyManager.addDiamond(cardProp.payDiamond, GOODS_CHANGE_TYPE.VIPBUY);
			player.pushDynamicData(Utils.ofMap("vip", player.player.vip));
			// 背包、仓库格子增加
			if (cardProp.iD == Const.VipType.forever.value) {
				player.bag.addBagGridCount(cardProp.prv6);
				player.wareHouse.addBagGridCount(cardProp.prv5);
			}
			player.onPay();

			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(player.getPlayer(), cardProp.payDiamond, cardProp.payMoneyAmount, cardProp.name);
			}
		} else {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			modifyVip(cardProp.iD, cardProp.lastTime, baseData);
			int before = baseData.diamond;
			baseData.diamond += cardProp.payDiamond;
			int after = baseData.diamond;

			// 背包、仓库格子增加
			if (cardProp.iD == Const.VipType.forever.value) {
				BagsPO bagsPO = PlayerPOManager.findPO(ConstsTR.bagTR, playerId, BagsPO.class);
				bagsPO.bagData.bagGridCount += cardProp.prv6;
				bagsPO.wareHouseData.bagGridCount += cardProp.prv5;
			}
			PlayerPOManager.sync(playerId);

			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(baseData, cardProp.payDiamond, cardProp.payMoneyAmount, cardProp.name);
			}

			// 不在线，需要补一条元宝获得上报
			LogReportService.getInstance().ansycReportMoneyFlow(baseData, VirtualItemType.DIAMOND, before, LogReportService.OPERATE_ADD, cardProp.payDiamond, after, GOODS_CHANGE_TYPE.VIPBUY.value);
		}
		if (StringUtil.isNotEmpty(cardProp.prv9)) {
			MailSysData d = new MailSysData(SysMailConst.PAY_TITLE);
			ArrayList<Attachment> list = new ArrayList<>();
			Attachment attachment = new Attachment();
			attachment.itemCode = cardProp.prv9;
			attachment.itemNum = 1;
			list.add(attachment);
			d.attachments = list;
			d.replace = new HashMap<>();
			d.replace.put("card", cardProp.name);
			MailUtil.getInstance().sendMailToOnePlayer(playerId, d, GOODS_CHANGE_TYPE.VIPBUY);
		}
		// 发送称号物品

		if (po.firstCharge == 0) {
			po.firstCharge = amount;
		}

		// 更新红点
		if (player != null) {
			player.vipManager.updateSuperScript();
		}
	}

	/**
	 * 购买钻石
	 */
	private void chargeDiamond(int productId, boolean logBI) {
		PayCO payProp = GameData.Pays.get(productId);
		if (payProp == null)
			return;
		int amount = payProp.payMoneyAmount;
		po.total_charge += amount;
		po.dailyChargeDiamond += payProp.payDiamond;
		PrepaidRecord record = new PrepaidRecord();
		record.money = amount;
		record.date = new Date();
		po.chargeRecord.add(record);
		int chargeDiamond = payProp.payDiamond;
		// 第一次购买某件产品有额外奖励
		if (!po.first_buy_record.containsKey(productId)) {
			po.first_buy_record.put(productId, productId);
			chargeDiamond += payProp.firstDiamond;
		} else {
			chargeDiamond += payProp.nonFirstDiamond;
		}
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			player.moneyManager.addDiamond(chargeDiamond, GOODS_CHANGE_TYPE.CHARGE);
			player.onPay();
			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(player.getPlayer(), chargeDiamond, payProp.payMoneyAmount, payProp.packageName);
			}
		} else {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			int before = baseData.diamond;
			baseData.diamond += chargeDiamond;
			int after = baseData.diamond;
			PlayerPOManager.sync(playerId);

			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(baseData, chargeDiamond, payProp.payMoneyAmount, payProp.packageName);
			}

			// 不在线，需要补一条元宝获得上报
			LogReportService.getInstance().ansycReportMoneyFlow(baseData, VirtualItemType.DIAMOND, before, LogReportService.OPERATE_ADD, chargeDiamond, after, GOODS_CHANGE_TYPE.CHARGE.value);
		}
		if (po.firstCharge == 0) {
			po.firstCharge = payProp.payMoneyAmount;
		}
	}

	/**
	 * 购买超值礼包额外附赠元宝
	 */
	private void chargeDiamondOfSuperPackage(int productId, boolean logBI) {
		SuperPackageCO superPackageCO = GameData.SuperPackages.get(productId);
		if (superPackageCO == null)
			return;
		int amount = superPackageCO.packagePrice;
		po.total_charge += amount;
		po.dailyChargeDiamond += superPackageCO.diamondNum;
		PrepaidRecord record = new PrepaidRecord();
		record.money = amount;
		record.date = new Date();
		po.chargeRecord.add(record);
		int chargeDiamond = superPackageCO.diamondNum;

		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			player.moneyManager.addDiamond(chargeDiamond, GOODS_CHANGE_TYPE.SUPER_PACKAGE);
			player.onPay();
			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(player.getPlayer(), chargeDiamond, superPackageCO.packagePrice, superPackageCO.packageName);
			}
		} else {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			int before = baseData.diamond;
			baseData.diamond += chargeDiamond;
			int after = baseData.diamond;
			PlayerPOManager.sync(playerId);

			if (logBI) {// 上报BI
				BILogService.getInstance().ansycReportRechargeSuccess(baseData, chargeDiamond, superPackageCO.packagePrice, superPackageCO.packageName);
			}

			// 不在线，需要补一条元宝获得上报
			LogReportService.getInstance().ansycReportMoneyFlow(baseData, VirtualItemType.DIAMOND, before, LogReportService.OPERATE_ADD, chargeDiamond, after, GOODS_CHANGE_TYPE.SUPER_PACKAGE.value);
		}
		if (po.firstCharge == 0) {
			po.firstCharge = superPackageCO.packagePrice;
		}
	}

	public void onResume(int consumeDiamond) {
		po.total_consume += consumeDiamond;
	}

	/**
	 * 更改vip类型，特别要注意的是SB策划和运营竟然允许既有月卡又有终身卡，所以有新怪胎产物：双卡，真的tmd一群脑残
	 * 
	 * @param vip
	 * @param lastTime
	 */
	public void modifyVip(int vip, int lastTime, PlayerPO baseData) {
		// 如果同事拥有月卡和终身卡，就变异成为怪胎双卡,SB策划运营
		VipType vt = VipType.getE(vip);

		// 清除过期
		if (baseData.vip == VipType.month.value || baseData.vip == VipType.sb_double.value) {
			if (baseData.vipEndTime.before(Calendar.getInstance().getTime())) {
				baseData.vipEndTime = null;
				if (baseData.vip == VipType.month.value)
					baseData.vip = Const.VipType.none.value;
				else
					baseData.vip = Const.VipType.forever.value;
			}
		}

		if (vt == null)
			return;
		if (vt == Const.VipType.month) {
			if (baseData.vip == Const.VipType.forever.value || baseData.vip == Const.VipType.sb_double.value) {
				baseData.vip = Const.VipType.sb_double.value;
			} else {
				baseData.vip = vt.value;
			}
		}
		if (vt == Const.VipType.forever) {
			if (baseData.vip == Const.VipType.month.value || baseData.vip == Const.VipType.sb_double.value) {
				baseData.vip = Const.VipType.sb_double.value;
			} else {
				baseData.vip = vt.value;
			}
		}
		if (vt == Const.VipType.month) {
			CardCO cardProp = GameData.Cards.get(vip);
			Calendar c = Calendar.getInstance();
			if (baseData.vipEndTime != null && baseData.vipEndTime.after(Calendar.getInstance().getTime())) {
				c.setTime(baseData.vipEndTime);
				c.add(Calendar.DAY_OF_MONTH, cardProp.lastTime);
				baseData.vipEndTime = c.getTime();
			} else {
				c.add(Calendar.DAY_OF_MONTH, cardProp.lastTime);
				baseData.vipEndTime = c.getTime();
			}
		}
	}

	public int getDailyCharge() {
		return po.dailyChargeDiamond;
	}

	public int getPayedTimes() {
		return po.chargeRecord.size();
	}

	/**
	 * 续充？？麻痹的我也不是很清楚
	 * 
	 * @param times
	 * @param money
	 * @return
	 */
	public boolean isEachPayMoneyEnough(int times, int money) {
		// TODO
		return false;
	}

	/**
	 * GM命令调用的
	 */
	public void onPrepaidChargeByMoney(final int money) {
		this.onCharge(GameData.Pays.values().stream().filter(v -> v.nonFirstDiamond == money).findFirst().get().iD, false);
	}

	public Date getDailyDate() {
		return this.po.dailyDate;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.PREPAID;
	}

	public List<FeeItem> getPrepaidList() {
		List<FeeItem> list = new ArrayList<>();
		for (PayCO prop : GameData.Pays.values()) {
			FeeItem.Builder fi = FeeItem.newBuilder();
			fi.setId(prop.iD);
			fi.setPackageIcon(prop.packageIcon);
			fi.setAppProductId(prop.appProductId);
			fi.setPackageName(prop.packageName);
			fi.setPackageDesc(prop.packageDesc);
			fi.setPackageDescFirst(prop.packageDescFirst);
			fi.setPayMoneyType(prop.payMoneyType);
			fi.setPayMoneyAmount(prop.payMoneyAmount);
			fi.setPayDiamond(prop.payDiamond);
			fi.setFirstDiamond(prop.firstDiamond);
			fi.setNonFirstDiamond(prop.nonFirstDiamond);
			fi.setPayCashFirst(prop.payCashFirst);
			fi.setPayTag(prop.payTag);
			if (po.first_buy_record.containsKey(prop.iD)) {
				fi.setVirgin(0);
			} else {
				fi.setVirgin(1);
			}
			list.add(fi.build());
		}
		return list;
	}

	/**
	 * 获取首充奖励信息
	 * 
	 * @return
	 */
	public PrepaidFirstResponse getPrepaidFirstAward() {
		PrepaidFirstResponse.Builder res = PrepaidFirstResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		res.setTotalPay(po.total_charge);
		for (TotalPayExt p : GameData.TotalPays.values()) {
			PrepaidFirstItem.Builder item = PrepaidFirstItem.newBuilder();
			item.setIsFinish(po.total_charge >= p.target ? 1 : 0);
			item.setPayMoney(p.target);
			for (AwardInfo a : p.awards) {
				KeyValueStruct.Builder i = KeyValueStruct.newBuilder();
				i.setKey(a.itemCode);
				i.setValue(String.valueOf(a.itemNum));
				item.addItems(i);
			}
			res.addAwards(item);
		}
		return res.build();
	}

	// GM命令开启月卡功能..
	// @gm open 月卡
	// @gm open 尊享卡
	public void onChargeCardByOpen(Object type) {
		this.onCharge(GameData.Cards.values().stream().filter(v -> v.name.equals(type)).findFirst().get().iD, true);
	}

	public int getTodayPayValue() {
		if (po.dailyDate != null && !DateUtils.isSameDay(new Date(), po.dailyDate)) {
			return 0;// 上次充值不是今天，清0
		}
		return po.dailyPayRmb;
	}
}