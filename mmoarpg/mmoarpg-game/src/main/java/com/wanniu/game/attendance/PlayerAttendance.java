package com.wanniu.game.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ext.AccumulateExt;
import com.wanniu.game.data.ext.LuxurySignExt;
import com.wanniu.game.data.ext.NormalSignExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AttendancePO;

import pomelo.area.AttendanceHandler.AttendanceInfo;
import pomelo.area.AttendanceHandler.CumulativeInfo;
import pomelo.area.AttendanceHandler.DailyInfo;
import pomelo.area.AttendanceHandler.GetAttendanceInfoResponse;
import pomelo.area.AttendanceHandler.GetCumulativeRewardResponse;
import pomelo.area.AttendanceHandler.GetDailyRewardResponse;
import pomelo.area.AttendanceHandler.GetLeftVipRewardResponse;
import pomelo.area.AttendanceHandler.GetLuxuryRewardResponse;
import pomelo.area.AttendanceHandler.LuxuryInfo;
import pomelo.area.AttendanceHandler.LuxuryRewardPush;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 签到（新版）
 * 
 * @author jjr
 *
 */
public class PlayerAttendance {

	public static enum GiftState {
		NO_RECEIVE(0), CAN_RECEIVE(1), RECEIVED(2), VIP_NOT_RECEIVE(3);

		private int value;

		private GiftState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public class DailySignInfo {
		public int id;
		public int boxIcon;
		public MiniItem.Builder[] items;
		public int vipDoubleLevel;
		public int state;

		public final MiniItem[] getMiniItems() {
			// 创建MiniItem对象
			if (this.items != null) {
				MiniItem[] itemList = new MiniItem[this.items.length];
				for (int i = 0; i < itemList.length; i++) {
					MiniItem.Builder miniItem = ItemUtil.getMiniItemData(items[i].getCode(), items[i].getGroupCount());
					if (null == miniItem) {
						Out.error(this.getClass(), "->[", items[i].getCode(), "] is not found");
						continue;
					}
					itemList[i] = miniItem.build();
				}
				return itemList;
			}
			return null;
		}

		public final DailyInfo getDailyInfo() {
			// 创建proto对象DailyInfo
			DailyInfo.Builder builder = DailyInfo.newBuilder();
			builder.setId(this.id);
			builder.setVipDoubleLevel(this.vipDoubleLevel);
			builder.setState(this.state);
			MiniItem[] itemList = this.getMiniItems();
			if (itemList != null && itemList.length > 0) {
				if (null != itemList[0]) {
					builder.setItemList(itemList[0]);
				}
			}
			return builder.build();
		}
	}

	public class CumulativeSignInfo {
		public int id;
		public String boxIcon;
		public MiniItem.Builder[] items;
		public int needCountDay;
		public int state;

		public final MiniItem[] getMiniItems() {
			// 创建MiniItem对象
			if (this.items != null) {
				MiniItem[] itemList = new MiniItem[this.items.length];
				for (int i = 0; i < itemList.length; i++) {
					MiniItem.Builder miniItem = ItemUtil.getMiniItemData(items[i].getCode(), items[i].getGroupCount());
					if (null == miniItem) {
						Out.error(this.getClass(), "->[", items[i].getCode(), "] is not found");
						continue;
					}
					itemList[i] = miniItem.build();
				}
				return itemList;
			}
			return null;
		}

		public final CumulativeInfo getCumulativeInfo() {
			// 创建proto对象CumulativeInfo
			CumulativeInfo.Builder builder = CumulativeInfo.newBuilder();
			builder.setId(this.id);
			builder.setBoxIcon(this.boxIcon == null ? "" : this.boxIcon);
			builder.setNeedCountDay(this.needCountDay);
			builder.setState(this.state);
			MiniItem[] itemList = this.getMiniItems();
			if (itemList != null) {
				for (int i = 0; i < itemList.length; i++) {
					MiniItem miniItem = itemList[i];
					if (null == miniItem) {
						continue;
					}

					builder.addItemList(miniItem);
				}
			}
			return builder.build();
		}
	}

	public class LuxurySignInfo {
		public int id;
		public MiniItem.Builder[] items;
		public int state;

		public final MiniItem[] getMiniItems() {
			// 创建MiniItem对象
			if (this.items != null) {
				MiniItem[] itemList = new MiniItem[this.items.length];
				for (int i = 0; i < itemList.length; i++) {
					if (null == itemList[i]) {
						continue;
					}
					MiniItem.Builder miniItem = ItemUtil.getMiniItemData(items[i].getCode(), items[i].getGroupCount());
					if (null == miniItem) {
						Out.error(this.getClass(), "->[", items[i].getCode(), "] is not found");
						continue;
					}
					itemList[i] = miniItem.build();
				}
				return itemList;
			}
			return null;
		}

		public final LuxuryInfo getLuxuryInfo() {
			// 创建proto对象LuxuryInfo
			LuxuryInfo.Builder builder = LuxuryInfo.newBuilder();
			builder.setState(this.state);
			MiniItem[] itemList = this.getMiniItems();
			if (itemList != null) {
				for (int i = 0; i < itemList.length; i++) {
					if (null == itemList[i]) {
						continue;
					}
					builder.addItemList(itemList[i]);
				}
			}
			return builder.build();
		}
	}

	public WNPlayer player;
	public AttendancePO attDb;

	public PlayerAttendance(WNPlayer player, AttendancePO attDb) {
		this.player = player;
		this.attDb = attDb;
	}

	/**
	 * 刷新
	 */
	public void refreshNewDay() {
		if (DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.attDb.lastLuxuryTime)) {
			this.attDb.luxuryState = GiftState.NO_RECEIVE.getValue();
		}
		if ((DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.attDb.lastSignTime)) && this.getSignedCount() >= 30) {
			int lastId = 30;
			NormalSignExt prop = AttendanceConfig.getInstance().findDnormalSignWithIDAndRound(this.attDb.stage, lastId);
			NormalSignExt propNext = AttendanceConfig.getInstance().findDnormalSignWithIDAndRound(this.attDb.stage + 1, 1);

			if (prop != null && prop.nextRound > 0) {
				this.attDb.stage = prop.nextRound;
			} else if (propNext != null) {
				this.attDb.stage += 1;
			} else {
				this.attDb.stage = 1;
			}
			this.attDb.signMap = new HashMap<>();
			this.attDb.cumulativeMap = new HashMap<>();
		}
	}

	/**
	 * 重置今日签到状态
	 */
	public final void testRefresh() {
		this.attDb.lastSignTime = DateUtil.getZeroDate();
		this.refreshNewDay();
	};

	/**
	 * 获取当前签到轮中所有签到信息
	 */
	public final DailySignInfo[] getDailyList() {
		ArrayList<DailySignInfo> infoList = new ArrayList<>();
		ArrayList<NormalSignExt> list = AttendanceConfig.getInstance().getDnormalSignsWithRound(this.attDb.stage);
		for (NormalSignExt prop : list) {
			DailySignInfo tempInfo = new DailySignInfo();
			tempInfo.id = prop.id;
			tempInfo.items = prop.getMiniItems();
			tempInfo.vipDoubleLevel = prop.vip;
			if (this.attDb.signMap.containsKey(prop.id)) {
				tempInfo.state = this.attDb.signMap.get(prop.id) > 0 ? this.attDb.signMap.get(prop.id) : GiftState.CAN_RECEIVE.getValue();
			} else {
				this.attDb.signMap.put(prop.id, GiftState.NO_RECEIVE.getValue());
				tempInfo.state = GiftState.NO_RECEIVE.getValue();
			}
			infoList.add(tempInfo);
		}
		DailySignInfo[] infos = new DailySignInfo[infoList.size()];
		int index = 0;
		for (DailySignInfo info : infoList) {
			infos[index++] = info;
		}
		return infos;
	}

	/**
	 * 获取累积签到达到一定次数时的奖励等信息
	 */
	public final CumulativeSignInfo[] getCumulativeList() {
		ArrayList<CumulativeSignInfo> infoList = new ArrayList<>();
		ArrayList<AccumulateExt> list = AttendanceConfig.getInstance().getDaccumulateWithRound(this.attDb.stage);
		for (AccumulateExt prop : list) {
			CumulativeSignInfo tempInfo = new CumulativeSignInfo();
			tempInfo.id = prop.id;
			tempInfo.items = prop.getMiniItems();
			tempInfo.needCountDay = prop.days;
			tempInfo.boxIcon = prop.iconcode;
			if (this.attDb.cumulativeMap.containsKey(prop.id)) {
				int receive = this.attDb.cumulativeMap.get(prop.id);
				if (receive > 0) {
					tempInfo.state = receive;
				} else if (this.getSignedCount() >= prop.days) {
					tempInfo.state = GiftState.CAN_RECEIVE.getValue();
				} else {
					tempInfo.state = GiftState.NO_RECEIVE.getValue();
				}
			} else {
				tempInfo.state = GiftState.NO_RECEIVE.getValue();
				this.attDb.cumulativeMap.put(prop.id, tempInfo.state);
			}
			infoList.add(tempInfo);
		}
		CumulativeSignInfo[] infos = new CumulativeSignInfo[infoList.size()];
		int index = 0;
		for (CumulativeSignInfo info : infoList) {
			infos[index++] = info;
		}
		return infos;
	}

	/**
	 * 获取豪华签到信息
	 */
	public final LuxurySignInfo getLuxuryList() {
		LuxurySignInfo info = new LuxurySignInfo();
		LuxurySignExt tempInfo = AttendanceConfig.getInstance().findDluxurySignWithID(1);
		info.id = tempInfo.id;
		info.items = tempInfo.getMiniItems();
		info.state = this.attDb.luxuryState;
		return info;
	}

	/** 获得奖励 */
	public final int getDailyReward() {
		Date nowDate = new Date();
		if (!DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.attDb.lastSignTime)) {
			return -1; // 今日已领取
		}
		int nextId = this.getSignedCount() + 1;
		NormalSignExt prop = AttendanceConfig.getInstance().findDnormalSignWithIDAndRound(this.attDb.stage, nextId);
		if (prop == null) {
			return -2; // 配置错误
		}
		if (!this.attDb.signMap.containsKey(nextId)) {
			this.attDb.signMap.put(nextId, GiftState.NO_RECEIVE.getValue());
		}
		// 背包检测
		if (!this.player.getWnBag().testAddCodeItems(prop.items)) {
			return -3;// 背包空间不足
		}
		// 添加物品
		this.player.getWnBag().addCodeItems(prop.items, Const.GOODS_CHANGE_TYPE.sign);
		this.attDb.lastSignTime = nowDate;
		this.attDb.signMap.put(nextId, GiftState.RECEIVED.getValue());
		// TODO 统计
		// this.player.biServerManager.signRec(1, items);
		this.player.activityManager.updateSuperScriptList();

		return 0;
	}

	/** 获取累计奖励 */
	public final int getCumulativeReward(int id) {
		int receive = 0;
		if (this.attDb.cumulativeMap.containsKey(id)) {
			receive = this.attDb.cumulativeMap.get(id);
		} else {
			this.attDb.cumulativeMap.put(id, GiftState.NO_RECEIVE.getValue());
			receive = GiftState.NO_RECEIVE.getValue();
		}
		if (receive == GiftState.RECEIVED.getValue()) {
			return -1;// 已领取
		}
		AccumulateExt prop = AttendanceConfig.getInstance().findDaccumulateWithIDAndRound(this.attDb.stage, id);
		if (prop == null) {
			return -2;
		}
		if (this.getSignedCount() < prop.days) {
			return -3;// 签到天数不够
		}
		// 背包判断
		if (!this.player.getWnBag().testAddCodeItems(prop.items)) {
			return -4;// 背包空间不足
		}
		// 添加物品
		this.player.getWnBag().addCodeItems(prop.items, Const.GOODS_CHANGE_TYPE.sign, null, false, false);
		this.attDb.cumulativeMap.put(id, GiftState.RECEIVED.getValue());
		this.player.activityManager.updateSuperScriptList();
		return 0;
	}

	/**
	 * 获取豪华签到奖励
	 */
	public final int getLuxuryReward() {
		Date now = new Date();
		if (this.attDb.luxuryState == GiftState.RECEIVED.getValue()) {
			return -1;// 今日已领取
		}
		if (this.attDb.luxuryState == GiftState.NO_RECEIVE.getValue()) {
			return -2;// 今日未充值
		}

		LuxurySignExt prop = AttendanceConfig.getInstance().findDluxurySignWithID(1);
		// 背包判断
		if (!this.player.getWnBag().testAddCodeItems(prop.items)) {
			return -3;// 背包空间不足
		}

		// 添加物品
		this.player.getWnBag().addCodeItems(prop.items, Const.GOODS_CHANGE_TYPE.sign, null, false, false);
		this.attDb.lastLuxuryTime = now;
		this.attDb.luxuryState = GiftState.RECEIVED.getValue();

		this.updateSuperScript();
		// TODO 统计
		// this.player.biServerManager.signRec(2, prop.items);
		return 0;
	}

	/**
	 * Vip奖励
	 */
	public final int getLeftVipReward(int id) {
		int sign = 0;
		if (this.attDb.signMap.containsKey(id)) {
			sign = this.attDb.signMap.get(id);
		} else {
			sign = GiftState.NO_RECEIVE.getValue();
			this.attDb.signMap.put(id, sign);
		}
		sign = this.attDb.signMap.get(id);
		int receive = sign;
		if (receive == GiftState.RECEIVED.getValue()) {
			return -1;// 已领取
		}
		if (receive != GiftState.VIP_NOT_RECEIVE.getValue()) {
			return -2;// 还未签到
		}
		NormalSignExt prop = AttendanceConfig.getInstance().findDnormalSignWithIDAndRound(this.attDb.stage, id);
		if (prop.vip == 0) {
			return -3;// 没有vip双倍
		}
		// 判断玩家VIP等级不足
		// if (this.player.baseDataManager.getVip() < prop.vip) {
		// return -4;
		// }
		// 判断背包
		if (!this.player.getWnBag().testAddCodeItems(prop.items)) {
			return -5;// 背包空间不足
		}
		// 添加物品
		this.player.getWnBag().addCodeItems(prop.items, Const.GOODS_CHANGE_TYPE.sign, null, false, false);
		sign = GiftState.RECEIVED.getValue();
		this.attDb.signMap.put(id, sign);
		this.updateSuperScript();

		return 0;
	}

	public final void onRecharge() {
		if (this.attDb.luxuryState == GiftState.NO_RECEIVE.getValue()) {
			this.attDb.luxuryState = GiftState.CAN_RECEIVE.getValue();
			this.pushToClientLuxuryReward();
			this.updateSuperScript();
		}
	}

	public final void pushToClientLuxuryReward() {
		// 发送消息
		LuxuryRewardPush.Builder builder = LuxuryRewardPush.newBuilder();
		builder.setS2CCode(PomeloRequest.OK);
		LuxurySignInfo info = this.getLuxuryList();
		builder.setS2CLuxury(info.getLuxuryInfo());
		player.receive("area.attendancePush.luxuryRewardPush", builder.build());
	}

	/**
	 * 获取已签到次数
	 */
	public final int getSignedCount() {
		int count = 0;
		for (Map.Entry<Integer, Integer> node : this.attDb.signMap.entrySet()) {
			int state = node.getValue();
			if (state >= GiftState.RECEIVED.getValue()) {
				count++;
			}
		}
		return count;
	}

	private final AttendanceInfo getAttendanceInfo() {
		AttendanceInfo.Builder infoBuilder = AttendanceInfo.newBuilder();
		if (!DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.attDb.lastSignTime)) {
			infoBuilder.setTodayState(GiftState.RECEIVED.getValue());
		} else {
			infoBuilder.setTodayState(GiftState.CAN_RECEIVE.getValue());
		}
		infoBuilder.setSignedCount(this.getSignedCount());
		DailySignInfo[] dailyList = this.getDailyList();
		CumulativeSignInfo[] cumulativeList = this.getCumulativeList();
		for (int i = 0; i < dailyList.length; i++) {
			infoBuilder.addDailyList(dailyList[i].getDailyInfo());
		}
		for (int i = 0; i < cumulativeList.length; i++) {
			infoBuilder.addCumulativeList(cumulativeList[i].getCumulativeInfo());
		}
		return infoBuilder.build();
	}

	/***/
	public final GetAttendanceInfoResponse.Builder createGetAttendanceInfoResponse() {
		GetAttendanceInfoResponse.Builder builder = GetAttendanceInfoResponse.newBuilder();
		AttendanceInfo info = getAttendanceInfo();
		LuxurySignInfo luxury = getLuxuryList();
		builder.setS2CLuxury(luxury.getLuxuryInfo());
		builder.setS2CAttendance(info);
		return builder;
	}

	public final GetDailyRewardResponse.Builder createGetDailyRewardResponse() {
		GetDailyRewardResponse.Builder builder = GetDailyRewardResponse.newBuilder();
		AttendanceInfo info = getAttendanceInfo();
		builder.setS2CAttendance(info);
		return builder;
	}

	public final GetCumulativeRewardResponse.Builder createGetCumulativeRewardResponse() {
		GetCumulativeRewardResponse.Builder builder = GetCumulativeRewardResponse.newBuilder();
		AttendanceInfo info = getAttendanceInfo();
		builder.setS2CAttendance(info);
		return builder;
	}

	public final GetLuxuryRewardResponse.Builder createGetLuxuryRewardResponse() {
		GetLuxuryRewardResponse.Builder builder = GetLuxuryRewardResponse.newBuilder();
		LuxurySignInfo luxury = getLuxuryList();
		builder.setS2CLuxury(luxury.getLuxuryInfo());
		return builder;
	}

	public final GetLeftVipRewardResponse.Builder createGetLeftVipRewardResponse() {
		GetLeftVipRewardResponse.Builder builder = GetLeftVipRewardResponse.newBuilder();
		AttendanceInfo info = getAttendanceInfo();
		builder.setS2CAttendance(info);
		return builder;
	}

	/**
	 * 更新角标
	 */
	public final void updateSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		list.add(this.getSuperScript());
		this.player.updateSuperScriptList(list);
	}

	/**
	 * 获取角标数据
	 */
	public SuperScriptType getSuperScript() {
		int number = 0;
		// 签到功能是否开启 功能开启模块
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.SIGN.getValue())) {
			SuperScriptType.Builder builder = SuperScriptType.newBuilder();
			builder.setType(Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_SIGN.getValue());
			builder.setNumber(number);
			return builder.build();
		}
		int signedCount = 0; // 签到次数
		for (Map.Entry<Integer, Integer> node : this.attDb.signMap.entrySet()) {
			int state = node.getValue();
			if (state >= GiftState.RECEIVED.getValue()) {
				signedCount += 1;
			}
		}
		if (DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.attDb.lastSignTime)) {
			number += 1; // 今日签到奖励未领取
		}
		// 累计奖励判断
		ArrayList<AccumulateExt> cumulativeList = AttendanceConfig.getInstance().getDaccumulateWithRound(this.attDb.stage);
		for (int i = 0; i < cumulativeList.size(); ++i) {
			AccumulateExt prop = cumulativeList.get(i);
			int receive = 0;
			if (this.attDb.cumulativeMap.containsKey(prop.id)) {
				receive = this.attDb.cumulativeMap.get(prop.id);
			} else {
				this.attDb.cumulativeMap.put(prop.id, GiftState.NO_RECEIVE.getValue());
				receive = GiftState.NO_RECEIVE.getValue();
			}
			if (!(receive > 0) && signedCount >= prop.days) {
				number += 1; // 累积奖励可领取
			}
		}
		if (this.attDb.luxuryState == GiftState.CAN_RECEIVE.getValue()) {
			number += 1; // 豪华签到可领取
		}
		SuperScriptType.Builder scriptNum = SuperScriptType.newBuilder();
		scriptNum.setType(Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_SIGN.getValue());
		scriptNum.setNumber(number);

		return scriptNum.build();
	}
}
