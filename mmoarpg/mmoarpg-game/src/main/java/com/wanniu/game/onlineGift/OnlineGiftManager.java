package com.wanniu.game.onlineGift;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.wanniu.core.game.JobFactory;
import com.wanniu.game.attendance.PlayerAttendance.GiftState;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ONLINE_GIFT_TYPE;
import com.wanniu.game.data.ext.OlGiftExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.OnlineDataPO;

import pomelo.area.OnlineGiftHandler.GiftInfo;
import pomelo.area.OnlineGiftHandler.GiftInfoPush;
import pomelo.area.OnlineGiftHandler.OnlineGift;
import pomelo.area.PlayerHandler.SuperScriptType;

public class OnlineGiftManager {
	public WNPlayer player;
	public OnlineDataPO onlineData;
	public ONLINE_GIFT_TYPE giftType;
	public ScheduledFuture<?> timer = null;

	public OnlineGiftManager(WNPlayer player, OnlineDataPO data) {
		this.player = player;
		this.onlineData = data;
		this.initGiftType();
		if (this.onlineData == null) {
			this.onlineData = PlayerUtil.createOnlineData(player.getId(), giftType, player.player.upLevel, player.getLevel());
		}
	};

	/**
	 * 获取某个时间的下一天的特定时间
	 */
	public Calendar getNextDaySpecifiedTime(Date time, int hour) {
		Calendar nextFive = Calendar.getInstance();
		nextFive.setTime(time);
		nextFive.add(Calendar.DATE, 1);
		nextFive.set(Calendar.HOUR_OF_DAY, hour);
		nextFive.set(Calendar.MINUTE, 0);
		nextFive.set(Calendar.SECOND, 0);
		nextFive.set(Calendar.MILLISECOND, 0);

		return nextFive;
	}

	/**
	 * 是否创角第一天
	 * 
	 * @return
	 */
	public boolean isFirstDay() {
		Calendar now = Calendar.getInstance();
		Calendar nextFive = getNextDaySpecifiedTime(this.player.getPlayer().createTime, Const.REFRSH_NEW_DAY_TIME);
		return now.before(nextFive);
	}

	public void initGiftType() {
		this.giftType = Const.ONLINE_GIFT_TYPE.NORMAL;
		if (isFirstDay()) {
			this.giftType = ONLINE_GIFT_TYPE.FIRST_DAY;
		}
	};

	public void onLogin() {
		startTimer();
	}

	public void cancelTimer() {
		if (null != timer) {
			timer.cancel(true);
			timer = null;
		}
	}

	public boolean checkIsAllReceived() {
		int sum = 0;
		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(this.giftType.getValue(), this.player.getPlayer().upLevel, this.player.getLevel());
		for (Integer state : onlineData.rewardState.values()) {
			if (GiftState.RECEIVED.getValue() == state || GiftState.CAN_RECEIVE.getValue() == state) {
				sum += 1;
			}
		}

		return sum >= propList.size() ? true : false;
	}

	public void startTimer() {
		cancelTimer();
		if (!checkIsAllReceived()) {
			timer = JobFactory.addScheduleJob(new Runnable() {
				@Override
				public void run() {
					onlineData.sumTime++;
					updateState(onlineData.sumTime);
					if (checkIsAllReceived()) {
						cancelTimer();
					}
				}
			}, 1, Const.Time.Second.getValue());
		}
	}

	public void refreshNewDay() {
		this.onlineData.sumTime = 0;
		this.onlineData.rewardState.clear();
		startTimer();
		this.initGiftType();
		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(this.giftType.getValue(), this.player.getPlayer().upLevel, this.player.getLevel());
		for (int i = 0; i < propList.size(); i++) {
			this.onlineData.rewardState.put(propList.get(i).giftId, GiftState.NO_RECEIVE.getValue());
		}
		this.pushGiftInfoToClient();
	};

	public void updateState(long sumTime) {
		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(this.giftType.getValue(), this.player.getPlayer().upLevel, this.player.getLevel());
		boolean isNeedPushRedPoint = false;
		for (OlGiftExt prop : propList) {
			if (!onlineData.rewardState.containsKey(prop.giftId) || GiftState.RECEIVED.getValue() == onlineData.rewardState.get(prop.giftId)) {
				continue;
			}

			if (sumTime >= prop.time * 60 && GiftState.NO_RECEIVE.getValue() == onlineData.rewardState.get(prop.giftId)) {
				onlineData.rewardState.put(prop.giftId, GiftState.CAN_RECEIVE.getValue());
				isNeedPushRedPoint = true;
			}
		}

		if (isNeedPushRedPoint) {
			this.player.activityManager.updateSuperScriptList();
		}
	}

	public OnlineGift toJson4Payload() {
		OnlineGift.Builder data = OnlineGift.newBuilder();
		data.setOnlineTime((int) onlineData.sumTime);
		ArrayList<GiftInfo> giftList = new ArrayList<>();

		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(this.giftType.getValue(), this.player.getPlayer().upLevel, this.player.getLevel());

		for (OlGiftExt prop : propList) {
			GiftInfo.Builder tempInfo = GiftInfo.newBuilder();
			if (!onlineData.rewardState.containsKey(prop.giftId)) {
				continue;
			}

			tempInfo.setState(onlineData.rewardState.get(prop.giftId));
			tempInfo.setId(prop.giftId);
			tempInfo.setTime(prop.time);
			tempInfo.setName(prop.name);
			tempInfo.addAllItem(prop.getMiniItems());
			giftList.add(tempInfo.build());

		}
		data.addAllGiftList(giftList);

		return data.build();
	};

	/**
	 * 获取角标数据
	 */
	public SuperScriptType getSuperScript() {
		int number = 0;
		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(this.giftType.getValue(), this.player.getPlayer().upLevel, this.player.getLevel());

		for (OlGiftExt prop : propList) {
			if (!this.onlineData.rewardState.containsKey(prop.giftId) || GiftState.CAN_RECEIVE.getValue() != this.onlineData.rewardState.get(prop.giftId)) {
				continue;
			}

			number++;
		}

		SuperScriptType.Builder scriptNum = SuperScriptType.newBuilder();
		scriptNum.setType(Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_ONLINE_GIFT.getValue());
		scriptNum.setNumber(number);
		return scriptNum.build();
	}

	/**
	 * 玩家下线
	 */
	public void onPlayerOffline() {
		cancelTimer();
	}

	public int receiveGift(int giftId) {
		OlGiftExt prop = OnlineGiftUtil.getPropById(giftId);
		if (prop == null || !this.onlineData.rewardState.containsKey(giftId)) {
			return -6;
		}
		if (prop.type != this.giftType.getValue()) {
			return -7;
		}
		if (GiftState.RECEIVED.getValue() == this.onlineData.rewardState.get(giftId)) {
			return -1; // 已领取
		}
		if (this.player.getPlayer().upLevel != 0) {
			if (this.player.getPlayer().upLevel < prop.downOrder || this.player.getPlayer().upLevel > prop.upOrder) {
				return -2;
			}
		} else {
			if (this.player.getLevel() < prop.lvDown || this.player.getLevel() > prop.lvUp) {
				return -3;
			}
		}

		if (GiftState.NO_RECEIVE.getValue() == this.onlineData.rewardState.get(giftId)) {
			return -4;
		}

		List<SimpleItemInfo> items = prop.items;
		Map<String, Integer> rewards = new HashMap<String, Integer>();
		for (int i = 0; i < items.size(); i++) {
			SimpleItemInfo tmp = items.get(i);
			rewards.put(tmp.itemCode, tmp.itemNum);
		}

		List<NormalItem> list_items = ItemUtil.createItemsByItemCode(rewards);
		player.bag.addCodeItemMail(list_items, null, GOODS_CHANGE_TYPE.ONLINE_GIFT, SysMailConst.BAG_FULL_COMMON);

		this.onlineData.rewardState.put(giftId, GiftState.RECEIVED.getValue());

		this.player.activityManager.updateSuperScriptList();
		return 0;
	}

	public void pushGiftInfoToClient() {
		GiftInfoPush.Builder data = GiftInfoPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.setS2CGift(this.toJson4Payload());
		player.receive("area.onlineGiftPush.giftInfoPush", data.build());
	};

}
