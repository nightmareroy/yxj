package com.wanniu.game.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.po.LuckyAward;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ActivityRewardType;
import com.wanniu.game.common.Const.EVENT_GIFT_STATE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.data.ActivityCO;
import com.wanniu.game.data.ActivityConfigCO;
import com.wanniu.game.data.AdventureItemAddCO;
import com.wanniu.game.data.AdventureItemCO;
import com.wanniu.game.data.DrawCO;
import com.wanniu.game.data.ForgedRandomAddCO;
import com.wanniu.game.data.ForgedRandomCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GroupRandomAddCO;
import com.wanniu.game.data.GroupRandomCO;
import com.wanniu.game.data.LimitTimeGiftCO;
import com.wanniu.game.data.SevenLoginCO;
import com.wanniu.game.data.SuperPackageCO;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.data.ext.DrawExt;
import com.wanniu.game.data.ext.DrawExt.DrawItem;
import com.wanniu.game.data.ext.RecoveryExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ActivityDataPO;

import pomelo.Common.KeyValueStruct;
import pomelo.area.ActivityFavorHandler.LimitTimeGiftInfo;
import pomelo.area.ActivityFavorHandler.LimitTimeGiftInfoPush;
import pomelo.area.ActivityFavorHandler.RecoveredInfo;
import pomelo.area.ActivityFavorHandler.RecoveredInfoResponse;
import pomelo.area.ActivityFavorHandler.RecoveredItem;
import pomelo.area.ActivityFavorHandler.RecoveredResponse;
import pomelo.area.ActivityFavorHandler.RecoveredSourceInfo;
import pomelo.area.ActivityFavorHandler.SevenDayPackageAwardInfo;
import pomelo.area.ActivityFavorHandler.SevenDayPackageAwardResponse;
import pomelo.area.ActivityFavorHandler.SevenDayPackageGetInfoResponse;
import pomelo.area.ActivityFavorHandler.SevenDayPackageInfo;
import pomelo.area.ActivityFavorHandler.SuperPackageAwardInfo;
import pomelo.area.ActivityFavorHandler.SuperPackageInfo;
import pomelo.area.ActivityHandler.ActivityAwardResponse;
import pomelo.area.ActivityHandler.ActivityLevelOrSwordResponse;
import pomelo.area.ActivityHandler.ActivityLs;
import pomelo.area.ActivityHandler.ActivityLuckyAwardViewResponse;
import pomelo.area.ActivityHandler.ActivityOpenFundsRes;
import pomelo.area.ActivityHandler.OpenChangeResponse;
import pomelo.area.ActivityHandler.PayFirstResponse;
import pomelo.area.ActivityHandler.ReSetluckyAwardResponse;
import pomelo.area.ActivityHandler.awardInfo;
import pomelo.area.ActivityHandler.awardPreview;
import pomelo.area.ActivityHandler.awardState;
import pomelo.area.ActivityHandler.changeItem;
import pomelo.area.ActivityHandler.openFundsAward;
import pomelo.area.ActivityHandler.totalInfo;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.MiniItem;

public class ActivityManager extends ModuleManager {

	private WNPlayer player;
	private ActivityDataPO opts;
	private int luckyAwardTotalRate;

	private Stack<Integer> cachedLimitTimeGiftStack;

	// private static Comparator<ActivityConfigCO> comparator;
	private static void SortActivityConfigExt(List<ActivityConfigExt> awardProps) {
		Collections.sort(awardProps, (o1, o2) -> o1.parameter1 - o2.parameter1);
	}

	public ActivityManager(WNPlayer player, ActivityDataPO opts) {
		this.player = player;
		this.opts = opts;
		this._init();
		cachedLimitTimeGiftStack = new Stack<>();
	}

	private void _init() {
		if (this.opts.drawedContainer.size() == 0 && this.opts.luckyAwardContainer.size() == 0) { // 初始化奖池
			this.refreshLuckyAwardContainer();
		}
	}

	public static class ResultSuperScript {
		public Const.SUPERSCRIPT_TYPE type;
		public int number;
	}

	public ActivityDataPO toJson4Serialize() {
		return this.opts;
	}

	public ActivityExt findActivityByType(int type) {
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityTab == type);
		for (ActivityCO _prop : props) {
			ActivityExt prop = (ActivityExt) _prop;
			if (null != prop) {
				return prop;
			}
		}
		return null;
	}

	// public List<ActivityExt> findActivitiesByType(Const.ActivityRewardType
	// activityRewardType) {
	// return GameData.findActivitys((t) -> t.activityTab ==
	// activityRewardType.getValue());
	// }

	public List<ActivityConfigExt> findActivitieConfigsByRewardType(Const.ActivityRewardType activityRewardType) {
		ActivityExt activityExt = findActivityByType(activityRewardType.getValue());
		return GameData.findActivityConfigs((t) -> t.type == activityExt.activityID);
	}

	public ActivityExt findActivityById(int id) {
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityID == id);
		if (props.size() > 0) {
			ActivityExt prop = (ActivityExt) props.get(0);
			if (null != prop) {
				return prop;
			}
		}
		return null;
	}

	public boolean hasFirstPayReward() {
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.FIRST_PAY.getValue());
		if (prop == null) {
			return false;
		}
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		if (props.size() > 0) {
			return this.isReward(props.get(0).id);
		} else {
			return false;
		}
	}

	public boolean isSecondPayVaild() {
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.SECOND_PAY.getValue());
		if (prop == null)
			return false;
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		for (ActivityConfigCO awardProp : awardProps) {
			if (!this.isReward(awardProp.id))
				return true;
		}
		return false;
	}

	public PayFirstResponse.Builder payFirst() {
		PayFirstResponse.Builder data = PayFirstResponse.newBuilder();
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.FIRST_PAY.getValue());
		if (prop == null) {
			return data;
		}
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		if (awardProps.size() > 0) {
			ActivityConfigExt awardProp = (ActivityConfigExt) awardProps.get(0);
			if (!this.isReward(awardProp.id)) {
				data.setS2CAwardId(awardProp.id);
				ArrayList<SimpleItemInfo> reward = this.getRankReward(awardProp.RankReward);
				ArrayList<MiniItem> list = new ArrayList<>();
				for (SimpleItemInfo item : reward) {
					MiniItem.Builder bi = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
					list.add(bi.build());
				}
				data.addAllS2CAwardItems(list);
				if (this.player.prepaidManager.getPayedTimes() > 0) {

					data.setS2CState(1); // 可领取
				}
			}
		}
		return data;
	}

	public PayFirstResponse.Builder paySecond() {
		PayFirstResponse.Builder data = PayFirstResponse.newBuilder();
		if (this.player.prepaidManager.getPayedTimes() < 1) {
			return data;
		}
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.SECOND_PAY.getValue());
		if (prop == null) {
			return data;
		}
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		SortActivityConfigExt(awardProps);
		int i = 0;
		for (ActivityConfigCO _awardProp : awardProps) {
			ActivityConfigExt awardProp = (ActivityConfigExt) _awardProp;
			if (!this.isReward(awardProp.id)) {
				data.setS2CAwardId(awardProp.id);
				ArrayList<MiniItem> list = new ArrayList<>();
				ArrayList<SimpleItemInfo> reward = this.getRankReward(awardProp.RankReward);
				for (SimpleItemInfo item : reward) {
					MiniItem.Builder bi = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
					list.add(bi.build());
				}
				data.addAllS2CAwardItems(list);
				if (this.player.prepaidManager.isEachPayMoneyEnough(i + 1, awardProp.parameter1)) {

					data.setS2CState(1); // 可领取
				}

				break;
			}
			i++;
		}
		return data;
	}

	public totalInfo.Builder payTotal() {
		totalInfo.Builder data = totalInfo.newBuilder();
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.TOTAL_PAY.getValue());
		if (prop == null) {
			data.setBeginTime("");
			data.setEndTime("");
			data.setDescribe("");
			return data;
		}
		data.setBeginTime(prop.openTime);
		data.setEndTime(prop.closeTime);
		data.setDescribe(prop.activityRule);

		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		ArrayList<awardPreview> list = new ArrayList<>();
		for (ActivityConfigCO _awardProp : awardProps) {
			ActivityConfigExt awardProp = (ActivityConfigExt) _awardProp;
			awardPreview.Builder info = awardPreview.newBuilder();
			info.setAwardId(awardProp.id);
			ArrayList<MiniItem> list_item = new ArrayList<>();
			info.setState(0);
			ArrayList<SimpleItemInfo> reward = getRankReward(awardProp.RankReward);
			for (SimpleItemInfo item : reward) {
				MiniItem.Builder _item = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
				list_item.add(_item.build());
			}
			info.addAllAwardItems(list_item);
			int allPrepaidMoney = RechargeActivityService.getInstance().getTotalPayValue(player.getId());
			info.setCurrNum(allPrepaidMoney);
			info.setNeedNum(awardProp.parameter1);
			if (isReward(awardProp.id)) {
				info.setState(2);
			} else if (allPrepaidMoney >= awardProp.parameter1) {
				info.setState(1);
			}
			list.add(info.build());
		}
		data.addAllAwards(list);
		return data;
	}

	public totalInfo.Builder consumeTotal() {
		totalInfo.Builder data = totalInfo.newBuilder();
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.TOTAL_CONSUME.getValue());
		if (prop == null) {
			data.setBeginTime("");
			data.setEndTime("");
			data.setDescribe("");
			return data;
		}
		data.setBeginTime(prop.openTime);
		data.setEndTime(prop.closeTime);
		data.setDescribe(prop.activityRule);
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		ArrayList<awardPreview> list = new ArrayList<>();
		for (ActivityConfigCO _awardProp : awardProps) {
			ActivityConfigExt awardProp = (ActivityConfigExt) _awardProp;
			awardPreview.Builder info = awardPreview.newBuilder();
			info.setAwardId(awardProp.id);
			ArrayList<MiniItem> list_item = new ArrayList<>();
			info.setState(0);
			ArrayList<SimpleItemInfo> reward = getRankReward(awardProp.RankReward);
			for (SimpleItemInfo item : reward) {
				MiniItem.Builder _item = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
				list_item.add(_item.build());
			}
			info.addAllAwardItems(list_item);
			int allConsumeMoney = RechargeActivityService.getInstance().getTotalConsumeValue(player.getId());
			info.setCurrNum(allConsumeMoney);
			info.setNeedNum(awardProp.parameter1);
			if (isReward(awardProp.id)) {
				info.setState(2);
			} else if (allConsumeMoney >= awardProp.parameter1) {
				info.setState(1);
			}
			list.add(info.build());
		}
		data.addAllAwards(list);
		return data;
	}

	public ActivityLevelOrSwordResponse.Builder levelOrSword(int activityId) {
		ActivityLevelOrSwordResponse.Builder datas = ActivityLevelOrSwordResponse.newBuilder();
		ActivityExt activity = this.findActivityById(activityId);
		if (activity == null)
			return datas;
		datas.setS2CBeginTime(activity.openTime);
		datas.setS2CBeginTime(activity.closeTime);
		datas.setS2CContent(activity.activityRule);
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == activity.activityID);
		ArrayList<awardState> list = new ArrayList<>();
		for (ActivityConfigCO _prop : props) {
			ActivityConfigExt prop = (ActivityConfigExt) _prop;
			awardState.Builder info = awardState.newBuilder();
			info.setAwardId(prop.id);
			ArrayList<MiniItem> list_item = new ArrayList<>();
			info.setState(EVENT_GIFT_STATE.NOT_RECEIVE.getValue());
			info.setNeedValue(prop.parameter1);
			ArrayList<SimpleItemInfo> reward = getRankReward(prop.RankReward);
			for (SimpleItemInfo item : reward) {
				MiniItem.Builder _item = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
				list_item.add(_item.build());
			}
			info.addAllAwardItems(list_item);
			int currNum = 0;
			if (activity.activityTab == Const.ActivityRewardType.LEVEL.getValue()) {
				currNum = player.getLevel();
			} else {
				currNum = player.getPlayer().fightPower;
			}

			if (isReward(prop.id)) {
				info.setState(EVENT_GIFT_STATE.RECEIVED.getValue());
			} else if (currNum >= prop.parameter1) {
				info.setState(EVENT_GIFT_STATE.CAN_RECEIVE.getValue());
			}
			list.add(info.build());
		}
		datas.addAllS2CData(list);
		// Out.debug("ActivityLevelOrSwordResponse->>>>", datas);
		return datas;
	}

	public ActivityOpenFundsRes.Builder openFunds() {
		ActivityOpenFundsRes.Builder datas = ActivityOpenFundsRes.newBuilder();
		datas.setS2CBuyState(1);
		ArrayList<openFundsAward> list = new ArrayList<>();
		ActivityExt activity = this.findActivityByType(Const.ActivityRewardType.FOUNDATION.getValue());
		if (activity == null) {
			datas.addAllS2CData(new ArrayList<openFundsAward>());
			return datas;
		}
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == activity.activityID);
		Object fund = this.getActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue());
		if (fund != null)
			datas.setS2CBuyState(2);
		for (ActivityConfigCO _prop : props) {
			openFundsAward.Builder _openFundsAward = openFundsAward.newBuilder();
			ActivityConfigExt prop = (ActivityConfigExt) _prop;
			awardState.Builder info = awardState.newBuilder();
			info.setAwardId(prop.id);
			info.setState(0);
			info.setNeedValue(0);
			ArrayList<MiniItem> list_item = new ArrayList<>();
			int currNum = 0;
			ArrayList<SimpleItemInfo> reward = getRankReward(prop.RankReward);
			int parameter1 = prop.parameter1;
			for (SimpleItemInfo item : reward) {
				_openFundsAward.setDiamond(item.itemNum);
				list_item.add(ItemUtil.getMiniItemData(item.itemCode, item.itemNum).build());
			}
			info.addAllAwardItems(list_item);
			if ("Activity_Fund".equals(prop.notes1)) {
				currNum = player.getLevel();
				_openFundsAward.setType(1);
			} else if ("Activity_Fund_UpLevel".equals(prop.notes1)) {
				if (parameter1 > 10000) {
					parameter1 = parameter1 % 10000;
				}
				currNum = player.player.upLevel;
				_openFundsAward.setType(1);
			} else {
				currNum = ActivityCenterManager.getIntance().getFundsNum(GWorld.__SERVER_ID);
				_openFundsAward.setType(2);
			}
			if (isReward(prop.id)) {
				info.setState(2);
			} else if (currNum >= parameter1) {
				info.setState(1);
			} else {
				info.setState(0);
			}
			_openFundsAward.setAward(info.build());
			_openFundsAward.setValue(prop.parameter1);
			list.add(_openFundsAward.build());
		}
		datas.addAllS2CData(list);
		return datas;
	}

	public HashMap<Integer, Integer> getActivityInfo(int value) {
		return this.opts.activityInfo.get(value);
	}

	public void addActivityInfo(int id, HashMap<Integer, Integer> data) {
		this.opts.activityInfo.put(id, data);
	}

	public static class RewardRecord {
		public int awardId;
		public Date awardTime;
	}

	public ActivityAwardResponse.Builder activityAward(int awardId, int activityId) {
		ActivityAwardResponse.Builder data = ActivityAwardResponse.newBuilder();
		data.setS2CCode(PomeloRequest.OK);

		ActivityExt propCenter = this.findActivityById(activityId);
		if (propCenter == null) {
			data.setS2CCode(PomeloRequest.FAIL);
			data.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			return data;
		}
		if (propCenter.isOpen == 0) {
			data.setS2CCode(PomeloRequest.FAIL);
			data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_OPEN"));
			return data;
		}
		// 公告读取
		if (propCenter.activityTab == Const.ActivityRewardType.GAME_NOTICE.getValue()) {
			if (!this.isReward(awardId)) {
				RewardRecord rr = new RewardRecord();
				rr.awardId = awardId;
				rr.awardTime = new Date();
				this.opts.activityRewardRecorder.put(rr.awardId, rr);
			}
			return data;
		}
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.id == awardId);
		if (props.size() <= 0 || props.get(0).type != activityId) {
			data.setS2CCode(PomeloRequest.FAIL);
			data.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			return data;
		}
		ActivityConfigExt prop = (ActivityConfigExt) props.get(0);
		Const.GOODS_CHANGE_TYPE origin = getActivityDetailOrigin(propCenter.activityTab);

		if (propCenter.activityTab != Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
			if (this.isReward(awardId)) {// 已经领取过了
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_RECEIVE"));
				return data;
			}
		}
		if (propCenter.activityTab == Const.ActivityRewardType.FIRST_PAY.getValue()) {
			if (this.player.prepaidManager.getPayedTimes() == 0) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_REQUIRMENT"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.SECOND_PAY.getValue()) {
			if (!this.isSecondAwardValid(awardId)) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_REQUIRMENT"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.TOTAL_PAY.getValue()) {
			int allPrepaidMoney = RechargeActivityService.getInstance().getTotalPayValue(player.getId());
			if (allPrepaidMoney < prop.parameter1) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_REQUIRMENT"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.TOTAL_CONSUME.getValue()) {
			int allConsumeMoney = RechargeActivityService.getInstance().getTotalConsumeValue(player.getId());
			if (allConsumeMoney < prop.parameter1) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_REQUIRMENT"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.LEVEL.getValue()) {
			if (player.getLevel() < prop.parameter1) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_LEVEL"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.FIGHT_POEWR.getValue()) {
			int power = this.player.getPlayer().fightPower;
			if (power < prop.parameter1) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_BATTLE"));
				return data;
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
			if (this.getHaoliChangeState(prop) != Const.HAOLI_CHANGE_STATE.CAN_RECEIVE.getValue()) {
				data.setS2CCode(PomeloRequest.FAIL);
				data.setS2CMsg(LangService.getValue("ACTIVITY_CHANGE_ITEM_NOT_ENOUGH"));
				return data;
			}
			ArrayList<SimpleItemInfo> costItem = this.getCostItem(prop.costItems);
			List<SimpleItemInfo> reward = this.getRankReward(prop.RankReward);
			// 变化道具
			List<KeyValueStruct> changeItems = new ArrayList<>();
			for (SimpleItemInfo i : reward) {
				KeyValueStruct.Builder items = KeyValueStruct.newBuilder();
				items.setKey(i.itemCode);
				items.setValue(String.valueOf(i.itemNum));
				changeItems.add(items.build());
			}

			for (int i = 0; i < costItem.size(); i++) {
				if (costItem.get(i).itemCode.equals("diamond")) {
					this.player.moneyManager.costDiamond(costItem.get(i).itemNum, origin, changeItems);
				} else if (costItem.get(i).itemCode.equals("cash")) {
					this.player.moneyManager.costTicket(costItem.get(i).itemNum, origin, changeItems);
				} else if (costItem.get(i).itemCode.equals("gold")) {
					this.player.moneyManager.costGold(costItem.get(i).itemNum, origin);
				} else {
					this.player.bag.discardItem(costItem.get(i).itemCode, costItem.get(i).itemNum, origin);
				}
			}
			HashMap<Integer, Integer> actInfo = this.getActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue());

			if (actInfo == null) {
				HashMap<Integer, Integer> actData = new HashMap<>();
				actData.put(prop.id, 1);
				this.addActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue(), actData);
			} else {
				if (actInfo.containsKey(prop.id)) {
					actInfo.put(prop.id, actInfo.get(prop.id) + 1);
					this.addActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue(), actInfo);
				} else {
					actInfo.put(prop.id, 1);
					this.addActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue(), actInfo);
				}
			}
		} else if (propCenter.activityTab == Const.ActivityRewardType.FOUNDATION.getValue()) {
			if (prop.notes1.equals("Activity_Fund") || prop.notes1.equals("Activity_Fund_UpLevel")) {
				HashMap<Integer, Integer> actInfo = getActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue());
				if (actInfo == null) {
					data.setS2CCode(PomeloRequest.FAIL);
					data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_BUYFUND"));
					return data;
				}
				if (prop.notes1.equals("Activity_Fund")) {
					int level = this.player.getLevel();
					if (level < prop.parameter1) {
						data.setS2CCode(PomeloRequest.FAIL);
						data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_LEVEL"));
						return data;
					}
				} else {
					int level = this.player.player.upLevel;
					int para = prop.parameter1 % 10000;
					if (level < para) {
						data.setS2CCode(PomeloRequest.FAIL);
						data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_LEVEL"));
						return data;
					}
				}
			} else {
				int buyCount = ActivityCenterManager.getIntance().getFundsNum(GWorld.__SERVER_ID);
				if (buyCount < prop.parameter1) {
					data.setS2CCode(PomeloRequest.FAIL);
					data.setS2CMsg(LangService.getValue("ACTIVITY_NOT_CONDITION"));
					return data;
				}
			}
		}
		// int MailID = Const.MailID.ACTIVITY_REWARD.getValue();
		// if (prop.mailID != 0) {
		// MailID = prop.mailID;
		// }

		List<SimpleItemInfo> reward = this.getRankReward(prop.RankReward);

		for (SimpleItemInfo item : reward) {
			item.forceType = Const.ForceType.BIND;
		}

		if (this.player.bag.testAddCodeItems(reward, Const.ForceType.BIND, false)) {
			this.player.bag.addCodeItems(reward, origin);
		} else {
			data.setS2CCode(PomeloRequest.FAIL);
			return data;
			// mailUtil.sendMailToOnePlayer(this.player.getId(), {mailType:
			// consts.MAIL_SYS_PARAM.MAIL_TYPE.MAIL_SYSTEM_TYPE, mailId:
			// MailID,replace:{Activity:
			// propCenter.Activity},attachments:reward});
		}
		if (propCenter.activityTab != Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
			RewardRecord rr = new RewardRecord();
			rr.awardId = prop.id;
			rr.awardTime = new Date();
			this.opts.activityRewardRecorder.put(prop.id, rr);
		}

		return data;
	}

	/**
	 * 生成自定义来源，格式：800+三位活动ID+三位活动奖励ID
	 * 
	 * @param configExt
	 * @return
	 */
	public Const.GOODS_CHANGE_TYPE getActivityDetailOrigin(int activityTab) {
		switch (ActivityRewardType.valueOf(activityTab)) {
		// 等级礼包...
		case LEVEL:
			return Const.GOODS_CHANGE_TYPE.ACTIVITY_LEVEL;
		// 战斗力礼包...
		case FIGHT_POEWR:
			return Const.GOODS_CHANGE_TYPE.ACTIVITY_FIGHT_POEWR;
		// 开服基金...
		case FOUNDATION:
			return Const.GOODS_CHANGE_TYPE.RECEIVE_FUNDS;
		// 累计充值
		case TOTAL_PAY:
			return Const.GOODS_CHANGE_TYPE.ACTIVITY_TOTAL_PAY;
		// 累计消费
		case TOTAL_CONSUME:
			return Const.GOODS_CHANGE_TYPE.ACTIVITY_TOTAL_CONSUME;
		// 好礼兑换
		case HAOLI_CHANGE:
			return Const.GOODS_CHANGE_TYPE.exchange;
		// 签到（独立的入口）
		case SIGN:
			return Const.GOODS_CHANGE_TYPE.sign;
		// 幸运抽奖
		case LUCK_DRAW:
			return Const.GOODS_CHANGE_TYPE.ActivityDraw;

		// 超值礼包
		case SUPER_PACKAGE:
			return Const.GOODS_CHANGE_TYPE.SUPER_PACKAGE;

		// 20 资源找回
		case RECOVERY:
			return Const.GOODS_CHANGE_TYPE.recovered;
		// 21 每日充值
		case DAILY_RECHARGE:
			return Const.GOODS_CHANGE_TYPE.DailyRecharge;
		// 22 节日礼包
		// 23 单笔充值
		// 24 消耗返还

		// 25 新春抽奖
		case SPRING_DRAW:
			return Const.GOODS_CHANGE_TYPE.ActivityDrawSpring;

		default:
			Out.error("福利活动里的产出类型未配置. activityTab=", activityTab);
			return Const.GOODS_CHANGE_TYPE.def;
		}
	}

	public boolean isSecondAwardValid(int awardId) {
		ActivityExt prop = findActivityByType(Const.ActivityRewardType.SECOND_PAY.getValue());
		if (prop == null) {
			return false;
		}
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		// Collections.sort(awardProps, (o1, o2)->o1.parameter1 - o2.parameter1);
		SortActivityConfigExt(awardProps);
		for (int i = 0; i < awardProps.size(); i++) {
			ActivityConfigCO awardProp = awardProps.get(i);
			if (awardProp.id == awardId) {
				return this.player.prepaidManager.isEachPayMoneyEnough(i + 1, awardProp.parameter1);
			}
		}
		return false;
	}

	public ActivityLuckyAwardViewResponse.Builder activityLuckyAwardView() {
		ActivityLuckyAwardViewResponse.Builder data = ActivityLuckyAwardViewResponse.newBuilder();
		List<DrawExt> props = GameData.findDraws((t) -> t.iD == Const.LuckyDrawType.RECOMMEND_ITEM.getValue());
		if (props.size() > 0) {
			DrawExt prop = (DrawExt) props.get(0);
			ArrayList<MiniItem> list = new ArrayList<>();
			for (DrawItem item : prop.items) {
				list.add(ItemUtil.getMiniItemData(item.itemCode, item.itemNum).build());
			}
			data.addAllS2CAwards(list);
		}
		Date now = new Date();
		int refreshInterval = GlobalConfig.Activity_LuckDraw;
		Calendar tmpTime = Calendar.getInstance();
		tmpTime.setTime(opts.refreshTime);
		tmpTime.set(Calendar.HOUR_OF_DAY, 0);
		tmpTime.set(Calendar.MINUTE, 0);
		tmpTime.set(Calendar.SECOND, 0);
		long diff = now.getTime() - tmpTime.getTimeInMillis();
		long intervalSecond = refreshInterval * Const.Time.Day.getValue();
		if (diff > intervalSecond) {
			diff %= intervalSecond;
		}
		data.setS2CLeftRefreshTime((int) Math.floor((intervalSecond - diff) / Const.Time.Second.getValue()));
		data.addAllS2CAwards(this.checkAwardDetail());
		// FIXME 回头再来确认一下这个功能，不要就删除了.
		ArrayList<awardInfo> list = new ArrayList<>();
		for (Integer pos : this.opts.drawedContainer.keySet()) {
			SimpleItemInfo drawedAward = this.opts.drawedContainer.get(pos);
			awardInfo.Builder ab = awardInfo.newBuilder();
			ab.setPos(pos);
			ab.setItemInfo(ItemUtil.getMiniItemData(drawedAward.itemCode, drawedAward.itemNum).build());
		}
		return data;
	}

	public ArrayList<MiniItem> checkAwardDetail() {
		ArrayList<MiniItem> data = new ArrayList<>();
		for (LuckyAward luckyAward : this.opts.luckyAwardContainer) {
			data.add(ItemUtil.getMiniItemData(luckyAward.itemCode, luckyAward.itemNum).build());
		}
		for (SimpleItemInfo drawedAward : this.opts.drawedContainer.values()) {
			data.add(ItemUtil.getMiniItemData(drawedAward.itemCode, drawedAward.itemNum).build());
		}
		return data;
	}

	public ArrayList<MiniItem> luckyAwardView() {
		ArrayList<MiniItem> data = new ArrayList<>();
		List<DrawExt> props = GameData.findDraws((t) -> t.iD == Const.LuckyDrawType.VIEW_ITEM.getValue());
		if (props.size() > 0) {
			DrawExt prop = (DrawExt) props.get(0);
			if (prop.items != null) {
				for (DrawItem item : prop.items) {
					data.add(ItemUtil.getMiniItemData(item.itemCode, item.itemNum).build());
				}
			}
		}
		return data;
	}

	public void refreshNewDay() {
		this.refreshLuckyAwardContainer();
		this.refreshSevendayLogin();
		this.refreshRecovered();

		// 这个位置不能移动,必需在刷新找回下面...
		this.opts.refreshTime = player.player.refreshTime;
		// this.opts.activityInfo.remove(Const.ActivityRewardType.HAOLI_CHANGE.getValue());

		this.player.prepaidManager.po.dailyChargeDiamond = 0;
		this.opts.daily_recharge_have_entered = false;
		this.opts.super_pakage_have_enterd = false;
		this.opts.daily_draw_free_time = 1;
		this.opts.daily_draw_free_time_add = 1;
		this.opts.dailyRechargeRecorder.clear();
		this.opts.superPackageRecorder.clear();
		for (int key : this.opts.timeLimitGiftPushMap.keySet()) {
			this.opts.timeLimitGiftPushMap.put(key, 0);
		}

	}

	public ReSetluckyAwardResponse.Builder reSetluckyAward() {
		ReSetluckyAwardResponse.Builder data = ReSetluckyAwardResponse.newBuilder();
		int resetCost = GlobalConfig.Activity_LuckDrwa_Reset;
		if (!this.player.moneyManager.enoughDiamond(resetCost)) {
			data.setS2CCode(PomeloRequest.FAIL);
			return data;
		}
		this.refreshLuckyAwardContainer();
		this.player.moneyManager.costDiamond(resetCost, Const.GOODS_CHANGE_TYPE.ActivityDraw);
		// this.player.pushDynamicData("diamond", player.player.diamond);
		data.addAllS2CAwards(this.checkAwardDetail());
		return data;
	}

	public SimpleItemInfo rateLuckyAward() {
		if (this.luckyAwardTotalRate <= 0) {
			return null;
		}
		int randRate = Utils.random(0, this.luckyAwardTotalRate - 1);
		int totalRate = 0;
		for (int i = 0; i < this.opts.luckyAwardContainer.size(); i++) {
			LuckyAward luckyAward = this.opts.luckyAwardContainer.get(i);
			if (this.opts.drawedContainer.size() + 1 < luckyAward.round) { // 轮数筛选
				continue;
			}
			totalRate += luckyAward.itemRate;
			if (totalRate > randRate) {
				SimpleItemInfo item = new SimpleItemInfo();
				item.index = i;
				item.itemCode = luckyAward.itemCode;
				item.itemNum = luckyAward.itemNum;
				item.type = luckyAward.id;
				return item;
			}
		}
		return null;
	}

	public ArrayList<SimpleItemInfo> getRankReward(HashMap<Integer, ArrayList<SimpleItemInfo>> data) {
		if (data.containsKey(0)) {
			return data.get(0);
		}
		return data.get(this.player.getPro());
	}

	/*
	 * ActivityManager.prototype.getRankReward = function(data){
	 * 
	 * if( data[0] ) {
	 * 
	 * return data[0]; }
	 * 
	 * return data[this.player.pro]; };
	 */

	public boolean isReward(int id) {
		return this.opts.activityRewardRecorder.containsKey(id);
	}

	/*
	 * ActivityManager.prototype.isReward = function(id) {
	 * 
	 * return this.activityRewardRecorder[id]; };
	 */

	/**
	 * 获取活动总红点数
	 * 
	 * @param ls
	 * @return
	 */
	public int getSumRedPointNum(List<SuperScriptType> ls) {
		int sum = 0;
		for (int i = 0; i < ls.size(); i++) {
			SuperScriptType elem = ls.get(i);
			if (null == elem) {
				continue;
			}
			sum += elem.getNumber();
		}
		return sum;
	}

	public void onLogin() {
		// 推送主界面红点
		updateDeskRedPoint();
	}

	/**
	 * 主界面福利红点
	 */
	public void updateDeskRedPoint() {
		List<SuperScriptType> ls = getSuperScriptList();
		int sum = 0;
		for (int i = 0; i < ls.size(); i++) {
			sum += ls.get(i).getNumber();
		}

		this.player.updateSuperScript(Const.SUPERSCRIPT_TYPE.FLAG_WELFARE, sum);
	}

	/**
	 * 更新活动红点
	 */
	public void updateSuperScriptList() {
		List<SuperScriptType> ls = getSuperScriptList();
		updateDeskRedPoint();
		this.player.updateSuperScriptList(ls); // 红点推送给玩家
	}

	/**
	 * 生成活动红点
	 * 
	 * @param type
	 * @param num
	 * @return
	 */
	private SuperScriptType createSuperScriptType(int type, int num) {
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(type);
		data.setNumber(num);
		return data.build();
	}

	/**
	 * 获取所有活动红点列表
	 * 
	 * @return
	 */
	public List<SuperScriptType> getSuperScriptList() {
		List<SuperScriptType> ls = new ArrayList<SuperScriptType>();
		ls.add(this.player.playerAttendance.getSuperScript()); // 添加签到红点
		ls.add(this.player.onlineGiftManager.getSuperScript()); // 添加在线礼包红点

		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.ACTIVITY.getValue())) {
			return ls;
		}
		long now = System.currentTimeMillis();

		for (ActivityExt propCenter : GameData.Activitys.values()) {
			if (propCenter.isOpen == 0) {
				continue;
			}
			int scriptType = getRedpointValue(propCenter);
			boolean hasRedpoint = false;
			if (propCenter.beginTime == 0 || propCenter.endTime == 0 || propCenter.beginTime > now || now > propCenter.endTime) {
				continue;
			}
			if (propCenter.activityTab == Const.ActivityRewardType.SECOND_PAY.getValue() && !hasFirstPayReward()) {
				continue;
			}
			if (propCenter.activityTab == Const.ActivityRewardType.LUCKY_REWARD.getValue()) {
				if (this.opts.drawedContainer.size() == 0) {}
				continue;
			}
			// 公告
			if (propCenter.activityTab == Const.ActivityRewardType.GAME_NOTICE.getValue()) {
				for (String key : ActivityNoticeService.getInstance().getNoticeKey()) {
					if (!isReward(Integer.parseInt(key))) {
						hasRedpoint = true;
						break;
					}
				}
			}

			// 七日登录礼包
			if (propCenter.activityTab == Const.ActivityRewardType.OPEN_SEVEN_DAY.getValue()) {
				boolean canGet = false;
				for (int state : opts.sevendayList) {
					if (state == 1) {
						canGet = true;
						break;
					}
				}
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.OPEN_SERVER_DAY.getValue(), canGet ? 1 : 0));
				continue;
			}

			// 资源找回
			if (propCenter.activityTab == Const.ActivityRewardType.RECOVERY.getValue()) {
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.RECOVERY.getValue(), hasRecoveryCount() ? 1 : 0));
				continue;
			}

			// 超值礼包
			if (propCenter.activityTab == Const.ActivityRewardType.SUPER_PACKAGE.getValue()) {
				boolean enable = false;
				if (!opts.super_pakage_have_enterd) {

					for (SuperPackageCO superPackageCO : GameData.SuperPackages.values()) {
						if (!opts.superPackageRecorder.containsKey(superPackageCO.iD)) {
							enable = true;
						}
					}

				}
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.SUPER_PACKAGE.getValue(), enable ? 1 : 0));
				continue;
			}

			// 幸运抽奖
			if (propCenter.activityTab == Const.ActivityRewardType.LUCK_DRAW.getValue()) {
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.LUCKY_DRAW.getValue(), opts.daily_draw_free_time > 0 ? 1 : 0));
				continue;
			}

			// 新春抽奖
			if (propCenter.activityTab == Const.ActivityRewardType.SPRING_DRAW.getValue()) {
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.SPRING_DRAW.getValue(), opts.daily_draw_free_time_add > 0 ? 1 : 0));
				continue;
			}

			// 单笔充值
			if (propCenter.activityTab == Const.ActivityRewardType.SINGLE_RECHARGE.getValue()) {
				int value = RechargeActivityService.getInstance().getSingleRechargeRedPoint(player.getId());
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.SINGLE_RECHARGE.getValue(), value));
				continue;
			}

			// 冲榜累计
			if (propCenter.activityTab == Const.ActivityRewardType.REVELRY_RECHARGE.getValue()) {
				int value = RechargeActivityService.getInstance().getRevelryRechargeRedPoint(player.getId());
				ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.REVELRY_RECHARGE.getValue(), value));
				continue;
			}

			List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == propCenter.activityID);
			for (int awardIndex = 0; awardIndex < props.size(); ++awardIndex) {
				ActivityConfigExt prop = (ActivityConfigExt) props.get(awardIndex);
				if (propCenter.activityTab == Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
					if (this.getHaoliChangeState(prop) == Const.HAOLI_CHANGE_STATE.CAN_RECEIVE.getValue()) {
						hasRedpoint = true;
						break;
					}
					continue;
				}

				if (propCenter.activityTab == Const.ActivityRewardType.DAILY_RECHARGE.getValue()) {

					if (!opts.daily_recharge_have_entered) {
						hasRedpoint = true;
					}
					if (player.prepaidManager.po.dailyChargeDiamond > prop.parameter1) {
						if (!opts.dailyRechargeRecorder.containsKey(prop.id)) {
							hasRedpoint = true;
						}
					}
					continue;
				}

				// 已经领取过了
				if (this.isReward(prop.id)) {
					continue;
				}

				if (propCenter.activityTab == Const.ActivityRewardType.FIRST_PAY.getValue()) {
					if (this.player.prepaidManager.getPayedTimes() == 0) {
						continue;
					}
					// TODO
					// scriptType = ;
				} else if (propCenter.activityTab == Const.ActivityRewardType.SECOND_PAY.getValue()) {
					if (!hasFirstPayReward()) {
						break;
					}
					if (!this.isSecondAwardValid(prop.id)) {
						continue;
					}
				} else if (propCenter.activityTab == Const.ActivityRewardType.TOTAL_PAY.getValue()) {
					int allPrepaidMoney = RechargeActivityService.getInstance().getTotalPayValue(player.getId());
					if (allPrepaidMoney < prop.parameter1) {
						break;
					}
				} else if (propCenter.activityTab == Const.ActivityRewardType.TOTAL_CONSUME.getValue()) {
					int allConsumeMoney = RechargeActivityService.getInstance().getTotalConsumeValue(player.getId());
					if (allConsumeMoney < prop.parameter1) {
						break;
					}
				} else if (propCenter.activityTab == Const.ActivityRewardType.LEVEL.getValue()) {
					if (this.player.getLevel() < prop.parameter1) {
						continue;
					}
				} else if (propCenter.activityTab == Const.ActivityRewardType.FIGHT_POEWR.getValue()) {
					int power = this.player.getPlayer().fightPower;
					if (power < prop.parameter1) {
						break;
					}
					scriptType = Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_ROLE_FC_GIFT.getValue();
				} else if (propCenter.activityTab == Const.ActivityRewardType.FOUNDATION.getValue()) {
					if (prop.notes1.equals("Activity_Fund") || prop.notes1.equals("Activity_Fund_UpLevel")) {
						HashMap<Integer, Integer> actInfo = getActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue());
						if (actInfo == null) {
							continue;
						}
						if (prop.notes1.equals("Activity_Fund")) {
							int level = this.player.getLevel();
							if (level < prop.parameter1) {
								continue;
							}
						} else {
							int level = this.player.player.upLevel;
							int para = prop.parameter1 % 10000;
							if (level < para) {
								break;
							}
						}
					} else {
						int buyCount = ActivityCenterManager.getIntance().getFundsNum(GWorld.__SERVER_ID);
						if (buyCount < prop.parameter1) {
							break;
						}
					}
				} else {
					break;
				}
				hasRedpoint = true;
				break;
			}
			if (scriptType > Const.SUPERSCRIPT_TYPE.MIN.getValue() && hasRedpoint) {
				ls.add(createSuperScriptType(scriptType, 1));
			} else {
				ls.add(createSuperScriptType(scriptType, 0));
			}
		}

		// ls.addAll(getLimitTimeSuperScript());

		return ls;
	}

	private List<SuperScriptType> getLimitTimeSuperScript() {
		List<SuperScriptType> ls = new ArrayList<SuperScriptType>();
		int minCount = 0;

		for (Map.Entry<Integer, Date> entry : opts.timeLimitGiftTriggeredTimeMap.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}

			int triggeredId = opts.timeLimitGiftTriggeredIdMap.get(entry.getKey());

			if (triggeredId == -1) {
				continue;
			}

			LimitTimeGiftCO limitTimeGiftCO = GameData.LimitTimeGifts.get(triggeredId);

			// 已经购买过了
			if (opts.timeLimitGiftBuyMap.get(limitTimeGiftCO.id) > 0) {
				continue;
			}

			long offset = entry.getValue().getTime() + 1000L * 60L * limitTimeGiftCO.limitTime - System.currentTimeMillis();
			int count = 0;
			if (offset > 0) {
				count = (int) (offset / 1000);
			}

			if (count > 0) {
				if (minCount == 0) {
					minCount = count;
				} else if (minCount > count) {
					minCount = count;
				}
			}

		}

		ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.LIMIT_TIME_GIFT.getValue(), minCount));

		// Out.debug(Const.SUPERSCRIPT_TYPE.LIMIT_TIME_GIFT.getValue(),
		// "++++++++++++++", minCount/60,"---",minCount);

		return ls;
	}

	/**
	 * 获取活动红点类型
	 * 
	 * @param ac
	 * @return
	 */
	public int getRedpointValue(ActivityExt ac) {
		if (ac.activityTab == Const.ActivityRewardType.TOTAL_PAY.getValue()) {
			return Const.SUPERSCRIPT_TYPE.TOTAL_PAY.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.TOTAL_CONSUME.getValue()) {
			return Const.SUPERSCRIPT_TYPE.TOTAL_CONSUME.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.LEVEL.getValue()) {
			return Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_ROLE_LV_GIFT.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.FIGHT_POEWR.getValue()) {
			return Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_ROLE_FC_GIFT.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.FOUNDATION.getValue()) {
			return Const.SUPERSCRIPT_TYPE.FUNDS.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
			return Const.SUPERSCRIPT_TYPE.FLAG_WELFARE_EXCHANGE.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.GAME_NOTICE.getValue()) {
			return Const.SUPERSCRIPT_TYPE.GAME_NOTICE.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.DAILY_RECHARGE.getValue()) {
			return Const.SUPERSCRIPT_TYPE.DAILY_RECHARGE.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.SUPER_PACKAGE.getValue()) {
			return Const.SUPERSCRIPT_TYPE.SUPER_PACKAGE.getValue();
		} else if (ac.activityTab == Const.ActivityRewardType.LUCK_DRAW.getValue()) {
			return Const.SUPERSCRIPT_TYPE.LUCKY_DRAW.getValue();
		}

		return 0;
	}

	public int getActivityHud(int activityId, int ActivityTab) {
		int hintNum = 0;
		List<ActivityConfigExt> awards = GameData.findActivityConfigs((t) -> t.type == activityId);
		if (ActivityTab == Const.ActivityRewardType.LEVEL.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {
					int para = player.getLevel();
					if (item.parameter1 > 10000) {
						int upLevel = item.parameter1 % 10000;
						if (player.player.upLevel >= upLevel) {
							hintNum++;
						}
					} else if (para >= item.parameter1) {
						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.FIGHT_POEWR.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {
					int para = player.getPlayer().fightPower;
					if (para >= item.parameter1) {
						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.FOUNDATION.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {
					int para = 0;
					int needPara = item.parameter1;
					if ("Activity_Fund".equals(item.notes1) || "Activity_Fund_UpLevel".equals(item.notes1)) {
						HashMap<Integer, Integer> actInfo = getActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue());
						if (actInfo != null) {
							if ("Activity_Fund" == item.notes1) {
								para = player.getLevel();
							} else {
								para = player.player.upLevel;
								needPara = needPara % 10000;
							}
						}
					} else {
						para = ActivityCenterManager.getIntance().getFundsNum(GWorld.__SERVER_ID);
					}
					if (para >= needPara) {
						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.FIRST_PAY.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {
					if (player.prepaidManager.getPayedTimes() > 0) {
						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.SECOND_PAY.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {

					if (hasFirstPayReward()) {

						if (isSecondAwardValid(item.id)) {
							hintNum++;
						}
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.TOTAL_PAY.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {
					int allPrepaidMoney = RechargeActivityService.getInstance().getTotalPayValue(player.getId());

					if (allPrepaidMoney >= item.parameter1) {

						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.TOTAL_CONSUME.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (!isReward(item.id)) {

					int allConsumeMoney = RechargeActivityService.getInstance().getTotalConsumeValue(player.getId());

					if (allConsumeMoney >= item.parameter1) {

						hintNum++;
					}
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.HAOLI_CHANGE.getValue()) {
			for (ActivityConfigCO item : awards) {
				if (getHaoliChangeState((ActivityConfigExt) item) == Const.HAOLI_CHANGE_STATE.CAN_RECEIVE.getValue()) {
					hintNum++;
				}
			}
		} else if (ActivityTab == Const.ActivityRewardType.LUCKY_REWARD.getValue()) {
			if (this.opts.drawedContainer.size() == 0) {
				hintNum++;
			}
		}
		return hintNum;
	}

	public void refreshLuckyAwardContainer() {
		this.opts.luckyAwardContainer = new ArrayList<>();
		Map<Integer, DrawExt> props = GameData.Draws;
		for (DrawCO _prop : props.values()) {
			DrawExt prop = (DrawExt) _prop;
			if (prop.iD == Const.LuckyDrawType.EXCELLENT_ITEM.getValue() || prop.iD == Const.LuckyDrawType.NORMAL_ITEM.getValue() || prop.iD == Const.LuckyDrawType.BUFF_ITEM.getValue()) {
				int tempTotalRate = prop.totalRate;
				ArrayList<DrawItem> tempItems = new ArrayList<>(prop.items);
				for (int i = 0; i < prop.itemNumber; i++) {
					ResultRateEachAward result = rateEachAward(tempTotalRate, tempItems);
					if (result == null)
						continue;
					tempTotalRate -= result.itemRate;
					tempItems.remove(result.index);
					LuckyAward item = new LuckyAward();
					item.itemCode = result.itemCode;
					item.itemNum = result.itemNum;
					item.itemRate = result.itemRate;
					item.id = prop.iD;
					item.round = prop.round;
					this.opts.luckyAwardContainer.add(item);
				}

			}
		}
		this.opts.drawedContainer = new HashMap<>();
		this.opts.buffTimes = 1;

		this.calcLuckyAwardTotalRate();
	}

	public void calcLuckyAwardTotalRate() {
		this.luckyAwardTotalRate = 0;
		for (int i = 0; i < this.opts.luckyAwardContainer.size(); i++) {

			LuckyAward luckyAward = this.opts.luckyAwardContainer.get(i);

			if (this.opts.drawedContainer.size() + 1 < luckyAward.round) { // 轮数筛选

				continue;
			}

			this.luckyAwardTotalRate += luckyAward.itemRate;
		}
	}

	/*
	 * ActivityManager.prototype.calcLuckyAwardTotalRate = function() {
	 * 
	 * this.luckyAwardTotalRate = 0;
	 * 
	 * for( var i = 0;i < this.luckyAwardContainer.length;i++ ) {
	 * 
	 * var luckyAward = this.luckyAwardContainer[i];
	 * 
	 * if( this.drawedContainerLength+1 < luckyAward.round ) { //轮数筛选
	 * 
	 * continue; }
	 * 
	 * this.luckyAwardTotalRate += luckyAward.itemRate; } }
	 */

	public static class ResultRateEachAward {
		public int index;
		public String itemCode;
		public int itemNum;
		public int itemRate;
	}

	public ResultRateEachAward rateEachAward(int totalRate, ArrayList<DrawItem> luckyAwardContainer) {
		if (totalRate <= 0) {
			return null;
		}
		int randRate = Utils.random(0, totalRate - 1);
		int addRate = 0;
		for (int i = 0; i < luckyAwardContainer.size(); i++) {
			DrawItem luckyAward = luckyAwardContainer.get(i);
			addRate += luckyAward.itemRate;
			if (addRate > randRate) {
				ResultRateEachAward result = new ResultRateEachAward();
				result.index = i;
				result.itemCode = luckyAward.itemCode;
				result.itemNum = luckyAward.itemNum;
				result.itemRate = luckyAward.itemRate;
				return result;
			}
		}
		return null;
	}
	/*
	 * ActivityManager.prototype.rateEachAward = function(
	 * totalRate,luckyAwardContainer) {
	 * 
	 * if( totalRate <= 0 ) {
	 * 
	 * return null; }
	 * 
	 * var randRate = utils.random(0,totalRate-1);
	 * 
	 * var addRate = 0;
	 * 
	 * for( var i = 0;i < luckyAwardContainer.length;i++ ) {
	 * 
	 * var luckyAward = luckyAwardContainer[i];
	 * 
	 * addRate += luckyAward.itemRate;
	 * 
	 * if( addRate > randRate ) {
	 * 
	 * return {
	 * index:i,itemCode:luckyAward.itemCode,itemNum:luckyAward.itemNum,itemRate:
	 * luckyAward.itemRate }; } } return null; };
	 */

	private ArrayList<SimpleItemInfo> getCostItem(HashMap<Integer, ArrayList<SimpleItemInfo>> data) {
		if (data.containsKey(0)) {
			return data.get(0);
		}
		return data.get(this.player.getPro());
	}

	/*
	 * ActivityManager.prototype.getCostItem = function(data){ if(data[0]) { return
	 * data[0]; } return data[this.player.pro]; };
	 */

	public int getHaoliChangeState(ActivityConfigExt activityConfigProp) {
		int key = activityConfigProp.id;
		if (activityConfigProp.parameter1 != 0) {
			HashMap<Integer, Integer> actInfo = this.getActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue());
			if (actInfo != null && actInfo.get(key) != null) {
				if (actInfo.get(key) >= activityConfigProp.parameter1) {
					return Const.HAOLI_CHANGE_STATE.RECEIVED.getValue();
				}
			}
		}
		int state = 0;
		ArrayList<SimpleItemInfo> costItem = this.getCostItem(activityConfigProp.costItems);
		for (int j = 0; j < costItem.size(); j++) {
			SimpleItemInfo item = costItem.get(j);
			if (item.itemCode.equals("diamond")) {
				if (this.player.moneyManager.enoughDiamond(item.itemNum)) {
					state++;
				}
			} else if (item.itemCode.equals("cash")) {
				if (this.player.moneyManager.enoughTicket(item.itemNum)) {
					state++;
				}
			} else if (item.itemCode.equals("gold")) {
				if (this.player.moneyManager.enoughGold(item.itemNum)) {
					state++;
				}
			} else {
				int haveNum = this.player.bag.findItemNumByCode(item.itemCode);
				if (haveNum >= item.itemNum) {
					state++;
				}
			}
		}
		if (costItem.size() == 0 || state != costItem.size()) {
			return Const.HAOLI_CHANGE_STATE.CANNOT_RECEIVE.getValue();
		}
		return Const.HAOLI_CHANGE_STATE.CAN_RECEIVE.getValue();
	}

	public OpenChangeResponse.Builder haoliChange() {
		OpenChangeResponse.Builder changeInfo = OpenChangeResponse.newBuilder();
		ArrayList<pomelo.area.ActivityHandler.changeInfo> list_changeInfo = new ArrayList<>();
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.HAOLI_CHANGE.getValue());
		if (prop == null) {
			return changeInfo;
		}
		changeInfo.setS2CBeginTime(prop.openTime);
		changeInfo.setS2CEndTime(prop.closeTime);
		changeInfo.setS2CContent(prop.activityRule);
		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		for (int i = 0; i < awardProps.size(); i++) {
			ActivityConfigExt p = (ActivityConfigExt) awardProps.get(i);
			ArrayList<SimpleItemInfo> costItem = this.getCostItem(p.costItems);
			ArrayList<SimpleItemInfo> rewardItem = this.getRankReward(p.RankReward);
			if (costItem != null && rewardItem != null) {
				pomelo.area.ActivityHandler.changeInfo.Builder changeData = pomelo.area.ActivityHandler.changeInfo.newBuilder();
				changeData.setChangeId(p.id);
				ArrayList<changeItem> l_costItem = getChangeItemList(costItem);
				ArrayList<changeItem> l_rewardItem = getChangeItemList(rewardItem);
				changeData.addAllCostItem(l_costItem);
				changeData.addAllRewardItem(l_rewardItem);
				changeData.setChangeSate(this.getHaoliChangeState(p));

				int changeNum = 0;
				HashMap<Integer, Integer> actInfo = this.getActivityInfo(Const.ActivityRewardType.HAOLI_CHANGE.getValue());
				if (actInfo != null && actInfo.get(p.id) != null) {
					changeNum = actInfo.get(p.id);
				}
				changeData.setChangeNum(changeNum);
				if (p.parameter1 != 0) {
					changeData.setChangeMax(p.parameter1);
				}
				list_changeInfo.add(changeData.build());
			}

		}
		changeInfo.addAllS2CChangeInfo(list_changeInfo);
		return changeInfo;
	}

	private ArrayList<changeItem> getChangeItemList(ArrayList<SimpleItemInfo> itemList) {
		ArrayList<changeItem> l = new ArrayList<>();
		for (SimpleItemInfo info : itemList) {
			changeItem.Builder ib = changeItem.newBuilder();
			ib.setItemCode(info.itemCode);
			ib.setItemNum(info.itemNum);
			ib.setIsBind(info.forceType.getValue());
			l.add(ib.build());
		}
		return l;
	}

	/**
	 * 获取所有有效活动列表
	 */
	public List<ActivityLs> getVailyActivityLs() {
		List<ActivityLs> ls = new ArrayList<ActivityLs>();
		for (ActivityExt t : GameData.Activitys.values()) {
			if (null == t) {
				continue;
			}

			if (Const.TimeState.TIME_UP.getValue() != DateUtil.isOutDate(t.openTime, t.closeTime) || t.isOpen == 0) {
				continue;
			}

			// 七日登录那个比较特别，领完了就消失掉吧...
			if (t.activityTab == Const.ActivityRewardType.OPEN_SEVEN_DAY.getValue()) {
				boolean continueFlag = true;
				for (int state : opts.sevendayList) {
					if (state != 2) {
						continueFlag = false;
						break;
					}
				}
				if (continueFlag) {
					continue;
				}
			}

			ActivityLs.Builder tmp = ActivityLs.newBuilder();
			tmp.setId(t.activityID);
			ls.add(tmp.build());
		}
		return ls;
	}

	public totalInfo.Builder DailyRecharge_Today() {
		totalInfo.Builder data = totalInfo.newBuilder();
		ActivityExt prop = this.findActivityByType(Const.ActivityRewardType.DAILY_RECHARGE.getValue());
		if (prop == null) {
			data.setBeginTime("");
			data.setEndTime("");
			data.setDescribe("");
			return data;
		}
		data.setBeginTime(prop.openTime);
		data.setEndTime(prop.closeTime);
		data.setDescribe(prop.activityRule);

		List<ActivityConfigExt> awardProps = GameData.findActivityConfigs((t) -> t.type == prop.activityID);
		ArrayList<awardPreview> list = new ArrayList<>();
		for (ActivityConfigCO _awardProp : awardProps) {
			ActivityConfigExt awardProp = (ActivityConfigExt) _awardProp;
			awardPreview.Builder info = awardPreview.newBuilder();
			info.setAwardId(awardProp.id);
			ArrayList<MiniItem> list_item = new ArrayList<>();
			info.setState(0);
			ArrayList<SimpleItemInfo> reward = getRankReward(awardProp.RankReward);
			for (SimpleItemInfo item : reward) {
				MiniItem.Builder _item = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
				list_item.add(_item.build());
			}
			info.addAllAwardItems(list_item);
			int todayPrepaidMoney = player.prepaidManager.getDailyCharge();
			info.setCurrNum(todayPrepaidMoney);
			info.setNeedNum(awardProp.parameter1);
			if (opts.dailyRechargeRecorder.containsKey(awardProp.id)) {
				info.setState(2);
			} else if (todayPrepaidMoney >= awardProp.parameter1) {
				info.setState(1);
			}
			list.add(info.build());
		}
		data.addAllAwards(list);

		opts.daily_recharge_have_entered = true;
		updateSuperScriptList();
		return data;
	}

	public boolean DailyRecharge_GetAward(int awardId) {
		// DailyRecharge_ResetRecharge();
		List<ActivityConfigExt> activityConfigExts = findActivitieConfigsByRewardType(Const.ActivityRewardType.DAILY_RECHARGE);
		ActivityConfigExt activityConfigExt = null;
		for (ActivityConfigExt tempActivityConfigExt : activityConfigExts) {
			if (tempActivityConfigExt.id == awardId) {
				activityConfigExt = tempActivityConfigExt;
				break;
			}
		}
		if (activityConfigExt == null)
			return false;
		if (player.prepaidManager.po.dailyChargeDiamond < activityConfigExt.parameter1) {
			return false;

		}
		if (opts.dailyRechargeRecorder.containsKey(activityConfigExt.id)) {
			return false;
		}

		// 领取
		RewardRecord rr = new RewardRecord();
		rr.awardId = activityConfigExt.id;
		rr.awardTime = new Date();
		opts.dailyRechargeRecorder.put(activityConfigExt.id, rr);

		// List<MiniItem> miniItems=new LinkedList<>();
		//
		ArrayList<SimpleItemInfo> simpleItemInfos = getRankReward(activityConfigExt.RankReward);
		for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
			List<NormalItem> list_items = ItemUtil.createItemsByItemCode(simpleItemInfo.itemCode, simpleItemInfo.itemNum);
			player.bag.addEntityItems(list_items, Const.GOODS_CHANGE_TYPE.DailyRecharge, null);

			// miniItems.add(ItemUtil.getMiniItemData(simpleItemInfo.itemCode,
			// simpleItemInfo.itemNum).build());
		}
		updateSuperScriptList();
		return true;
	}

	public int DailyRecharge_GetTodayMax() {
		int max = 0;

		List<ActivityConfigExt> activityConfigExts = findActivitieConfigsByRewardType(Const.ActivityRewardType.DAILY_RECHARGE);
		for (ActivityConfigExt tempActivityConfigExt : activityConfigExts) {
			if (tempActivityConfigExt.parameter1 > max) {
				max = tempActivityConfigExt.parameter1;
			}
		}
		return max;
	}

	// super package

	public SuperPackageInfo.Builder SuperPackage_GetInfo() {
		SuperPackageInfo.Builder res = SuperPackageInfo.newBuilder();

		ActivityExt activityExt = findActivityByType(Const.ActivityRewardType.SUPER_PACKAGE.getValue());

		res.setBeginTime(activityExt.openTime);
		res.setEndTime(activityExt.closeTime);
		res.setDescribe(activityExt.activityRule);

		for (SuperPackageCO superPackageCO : GameData.SuperPackages.values()) {
			SuperPackageAwardInfo.Builder spiBuilder = SuperPackageAwardInfo.newBuilder();
			spiBuilder.setPackageId(superPackageCO.iD);
			spiBuilder.setPackageName(superPackageCO.packageName);
			spiBuilder.setPackageCode(superPackageCO.packageCode);
			spiBuilder.setPackageNum(superPackageCO.packageNum);
			spiBuilder.setPackageIcon(superPackageCO.packageIcon);
			spiBuilder.setPackageScript(superPackageCO.packageScript);
			spiBuilder.setPackagePrice(superPackageCO.packagePrice);

			boolean bought = opts.superPackageRecorder.containsKey(superPackageCO.iD);
			spiBuilder.setPackageState(bought == true ? 1 : 0);

			// spiBuilder.setPackageId(1);
			// spiBuilder.setPackageName("1");
			// spiBuilder.setPackageCode("1");
			// spiBuilder.setPackageNum(1);
			// spiBuilder.setPackageIcon("1");
			// spiBuilder.setPackageScript(1);
			// spiBuilder.setPackagePrice(1);

			// boolean
			// bought=opts.activitySuperPackageRecorder.containsKey(superPackageCO.iD);
			// spiBuilder.setPackageState(1);

			res.addSuperPackageAwardInfo(spiBuilder.build());
		}
		opts.super_pakage_have_enterd = true;
		// res.addSuperPackageInfos(value)
		updateSuperScriptList();
		return res;
	}

	public boolean SuperPackage_GetBoughtable(int productId) {
		return !opts.superPackageRecorder.containsKey(productId);

	}

	// daily draw
	public List<AdventureItemCO> DailyDraw_Draw(int times) {
		List<AdventureItemCO> res = new LinkedList<>();

		// 根据配置，更新伪随机map
		DailyDrawUpdateForgeRandomMap();

		for (int j = 0; j < times; j++) {
			// 随机选择一个组
			int totalProb = 0;
			List<Integer> probUpList = new LinkedList<>();
			List<GroupRandomCO> groupRandomCOList = new LinkedList<>();
			boolean forgeRandom = false;

			GroupRandomCO selectedGroupRandomCO = null;
			for (GroupRandomCO groupRandomCO : GameData.GroupRandoms.values()) {
				if (!opts.daily_draw_forgerandom_map.containsKey(groupRandomCO.groupID)) {
					totalProb += groupRandomCO.groupProb;
				} else {
					// 根据策划要求,RamRed字段不再表示抽中该组后，伪随机概率的减少值，而是是否强制抽中的临界阈值，如果小于此值，则仍然沿用组概率，如果等于此值，则强制抽中，且伪随机值清零
					if (opts.daily_draw_forgerandom_map.get(groupRandomCO.groupID) < GameData.ForgedRandoms.get(groupRandomCO.groupID).ramRed) {
						totalProb += groupRandomCO.groupProb;
					} else {
						selectedGroupRandomCO = GameData.GroupRandoms.get(groupRandomCO.groupID);
						forgeRandom = true;
						break;
					}

				}
				totalProb += groupRandomCO.groupProb;
				groupRandomCOList.add(groupRandomCO);
				probUpList.add(totalProb);
			}

			if (!forgeRandom) {
				int selectedId = -1;
				int randomProb = RandomUtil.getInt(0, totalProb);

				for (int i = 0; i < probUpList.size(); i++) {
					if (randomProb <= probUpList.get(i)) {
						selectedId = i;
						break;
					}
				}
				if (selectedId == -1) {
					Out.error("随机出错");
					break;
				}
				selectedGroupRandomCO = groupRandomCOList.get(selectedId);
			}

			// 组里随机选择一个道具
			totalProb = 0;
			probUpList.clear();
			final int finalGroupId = selectedGroupRandomCO.groupID;
			List<AdventureItemCO> adventureItemCOList = GameData.findAdventureItems(t -> t.groupID == finalGroupId);
			for (AdventureItemCO adventureItemCO : adventureItemCOList) {
				if (adventureItemCO.isValid == 1) {
					if (!opts.daily_draw_forgerandom_map.containsKey(adventureItemCO.groupID)) {
						totalProb += adventureItemCO.prob;
					} else {
						totalProb += opts.daily_draw_forgerandom_map.get(adventureItemCO.groupID);
					}
				}

				probUpList.add(totalProb);
			}
			int randomProb = RandomUtil.getInt(0, totalProb);
			int selectedId = -1;
			for (int i = 0; i < probUpList.size(); i++) {
				if (randomProb <= probUpList.get(i)) {
					selectedId = i;
					break;
				}
			}
			if (selectedId == -1) {
				Out.error("随机出错。");
				break;
			}
			AdventureItemCO selectedAdventureItemCO = adventureItemCOList.get(selectedId);

			// 更新伪随机map
			for (Integer groupId : opts.daily_draw_forgerandom_map.keySet()) {
				ForgedRandomCO forgedRandomCO = GameData.ForgedRandoms.get(groupId);
				int currentProb = opts.daily_draw_forgerandom_map.get(groupId);
				if (selectedAdventureItemCO.groupID == groupId) {
					opts.daily_draw_forgerandom_map.put(groupId, 0);
				} else {
					opts.daily_draw_forgerandom_map.put(groupId, currentProb + forgedRandomCO.ramAdd);
				}
			}

			res.add(selectedAdventureItemCO);

			List<NormalItem> normalItems = ItemUtil.createItemsByItemCode(selectedAdventureItemCO.item, selectedAdventureItemCO.itemNum);

			for (NormalItem normalItem : normalItems) {
				normalItem.setBind(selectedAdventureItemCO.isBind);
			}
			player.bag.addCodeItemMail(normalItems, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.ActivityDraw, SysMailConst.ACTIVITY_TURNTABLE);
			if (selectedAdventureItemCO.isShow == 1) {
				NormalItem nItem = normalItems.get(0);

				String playerLink = ChannelUtil.setPlayerInfo(player);
				String targetPlayerText = LangService.format("NAME_LINK", playerLink, player.getName());

				String itemText = LangService.getValue(MessageUtil.getColorLink(nItem.prop.qcolor));
				String itemLink = ChannelUtil.setItemInfo(nItem);
				itemText = itemText.replace("{a}", nItem.prop.name).replace("{b}", itemLink);

				String numText = LangService.getValue("DEFAULT");
				numText = numText.replace("{a}", "*" + normalItems.size());

				String targetItemText = itemText + numText;

				// String
				// tempStr1=String.format(LangService.getValue("ACTIVITY_DAILY_ADVENTURE"),
				// player.getName(),targetItemText,Const.TipsType.NORMAL);
				String tempStr2 = String.format(LangService.getValue("ACTIVITY_DAILY_ADVENTURE"), targetPlayerText, targetItemText, Const.TipsType.NORMAL);

				// MessageUtil.sendRollChat(player.getLogicServerId(), tempStr1,
				// Const.CHAT_SCOPE.SYSTEM);
				MessageUtil.sendRollChat(player.getLogicServerId(), tempStr2, Const.CHAT_SCOPE.SYSTEM);
				// MessageUtil.sendRollChat(player.getLogicServerId(), targetPlayerText,
				// Const.CHAT_SCOPE.SYSTEM);
				// MessageUtil.sendRollChat(player.getLogicServerId(), targetItemText,
				// Const.CHAT_SCOPE.SYSTEM);
			}

		}

		updateSuperScriptList();

		return res;
	}

	// 新春抽奖
	public List<AdventureItemAddCO> DailyDraw_Draw_Add(int times) {
		List<AdventureItemAddCO> res = new LinkedList<>();

		// 根据配置，更新伪随机map
		DailyDrawUpdateForgeRandomMapAdd();

		for (int j = 0; j < times; j++) {
			// 随机选择一个组
			int totalProb = 0;
			List<Integer> probUpList = new LinkedList<>();
			List<GroupRandomAddCO> groupRandomAddCOList = new LinkedList<>();
			boolean forgeRandom = false;

			GroupRandomAddCO selectedGroupRandomAddCO = null;
			for (GroupRandomAddCO groupRandomAddCO : GameData.GroupRandomAdds.values()) {
				if (!opts.daily_draw_forgerandom_map_add.containsKey(groupRandomAddCO.groupID)) {
					totalProb += groupRandomAddCO.groupProb;
				} else {
					// 根据策划要求,RamRed字段不再表示抽中该组后，伪随机概率的减少值，而是是否强制抽中的临界阈值，如果小于此值，则仍然沿用组概率，如果等于此值，则强制抽中，且伪随机值清零
					if (opts.daily_draw_forgerandom_map_add.get(groupRandomAddCO.groupID) < GameData.ForgedRandomAdds.get(groupRandomAddCO.groupID).ramRed) {
						totalProb += groupRandomAddCO.groupProb;
					} else {
						selectedGroupRandomAddCO = GameData.GroupRandomAdds.get(groupRandomAddCO.groupID);
						forgeRandom = true;
						break;
					}

				}
				totalProb += groupRandomAddCO.groupProb;
				groupRandomAddCOList.add(groupRandomAddCO);
				probUpList.add(totalProb);
			}

			if (!forgeRandom) {
				int selectedId = -1;
				int randomProb = RandomUtil.getInt(0, totalProb);

				for (int i = 0; i < probUpList.size(); i++) {
					if (randomProb <= probUpList.get(i)) {
						selectedId = i;
						break;
					}
				}
				if (selectedId == -1) {
					Out.error("随机出错");
					break;
				}
				selectedGroupRandomAddCO = groupRandomAddCOList.get(selectedId);
			}

			// 组里随机选择一个道具
			totalProb = 0;
			probUpList.clear();
			final int finalGroupId = selectedGroupRandomAddCO.groupID;
			List<AdventureItemAddCO> adventureItemAddCOList = GameData.findAdventureItemAdds(t -> t.groupID == finalGroupId);
			for (AdventureItemAddCO adventureItemAddCO : adventureItemAddCOList) {
				if (adventureItemAddCO.isValid == 1) {
					if (!opts.daily_draw_forgerandom_map_add.containsKey(adventureItemAddCO.groupID)) {
						totalProb += adventureItemAddCO.prob;
					} else {
						totalProb += opts.daily_draw_forgerandom_map_add.get(adventureItemAddCO.groupID);
					}
				}

				probUpList.add(totalProb);
			}
			int randomProb = RandomUtil.getInt(0, totalProb);
			int selectedId = -1;
			for (int i = 0; i < probUpList.size(); i++) {
				if (randomProb <= probUpList.get(i)) {
					selectedId = i;
					break;
				}
			}
			if (selectedId == -1) {
				Out.error("随机出错。");
				break;
			}
			AdventureItemAddCO selectedAdventureItemAddCO = adventureItemAddCOList.get(selectedId);

			// 更新伪随机map
			for (Integer groupId : opts.daily_draw_forgerandom_map_add.keySet()) {
				ForgedRandomAddCO forgedRandomAddCO = GameData.ForgedRandomAdds.get(groupId);
				int currentProb = opts.daily_draw_forgerandom_map_add.get(groupId);
				if (selectedAdventureItemAddCO.groupID == groupId) {
					opts.daily_draw_forgerandom_map_add.put(groupId, 0);
				} else {
					opts.daily_draw_forgerandom_map_add.put(groupId, currentProb + forgedRandomAddCO.ramAdd);
				}
			}

			res.add(selectedAdventureItemAddCO);

			List<NormalItem> normalItems = ItemUtil.createItemsByItemCode(selectedAdventureItemAddCO.item, selectedAdventureItemAddCO.itemNum);

			for (NormalItem normalItem : normalItems) {
				normalItem.setBind(selectedAdventureItemAddCO.isBind);
			}
			player.bag.addCodeItemMail(normalItems, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.ActivityDrawSpring, SysMailConst.ACTIVITY_TURNTABLE);
			if (selectedAdventureItemAddCO.isShow == 1) {
				NormalItem nItem = normalItems.get(0);

				String playerLink = ChannelUtil.setPlayerInfo(player);
				String targetPlayerText = LangService.format("NAME_LINK", playerLink, player.getName());

				String itemText = LangService.getValue(MessageUtil.getColorLink(nItem.prop.qcolor));
				String itemLink = ChannelUtil.setItemInfo(nItem);
				itemText = itemText.replace("{a}", nItem.prop.name).replace("{b}", itemLink);

				String numText = LangService.getValue("DEFAULT");
				numText = numText.replace("{a}", "*" + normalItems.size());

				String targetItemText = itemText + numText;

				// String
				// tempStr1=String.format(LangService.getValue("ACTIVITY_DAILY_ADVENTURE"),
				// player.getName(),targetItemText,Const.TipsType.NORMAL);
				String tempStr2 = String.format(LangService.getValue("ACTIVITY_DAILY_ADVENTURE"), targetPlayerText, targetItemText, Const.TipsType.NORMAL);

				// MessageUtil.sendRollChat(player.getLogicServerId(), tempStr1,
				// Const.CHAT_SCOPE.SYSTEM);
				MessageUtil.sendRollChat(player.getLogicServerId(), tempStr2, Const.CHAT_SCOPE.SYSTEM);
				// MessageUtil.sendRollChat(player.getLogicServerId(), targetPlayerText,
				// Const.CHAT_SCOPE.SYSTEM);
				// MessageUtil.sendRollChat(player.getLogicServerId(), targetItemText,
				// Const.CHAT_SCOPE.SYSTEM);
			}

		}

		updateSuperScriptList();

		return res;
	}

	private void DailyDrawUpdateForgeRandomMap() {
		if (GameData.ForgedRandoms.size() > 1) {
			Out.error("伪随机配置不能多于1条");
		}
		// 如果配置表里有，而po里没有，则新增
		for (ForgedRandomCO forgedRandomCO : GameData.ForgedRandoms.values()) {
			if (!opts.daily_draw_forgerandom_map.containsKey(forgedRandomCO.groupID)) {
				opts.daily_draw_forgerandom_map.put(forgedRandomCO.groupID, forgedRandomCO.initial);

			}
		}

		// 如果配置表里没有，而po里有，则删除
		for (Integer groupId : opts.daily_draw_forgerandom_map.keySet()) {
			if (!GameData.ForgedRandoms.containsKey(groupId)) {
				opts.daily_draw_forgerandom_map.remove(groupId);
			}
		}

	}

	// 新春抽奖
	private void DailyDrawUpdateForgeRandomMapAdd() {
		if (GameData.ForgedRandomAdds.size() > 1) {
			Out.error("伪随机配置不能多于1条");
		}
		// 如果配置表里有，而po里没有，则新增
		for (ForgedRandomAddCO forgedRandomAddCO : GameData.ForgedRandomAdds.values()) {
			if (!opts.daily_draw_forgerandom_map_add.containsKey(forgedRandomAddCO.groupID)) {
				opts.daily_draw_forgerandom_map_add.put(forgedRandomAddCO.groupID, forgedRandomAddCO.initial);

			}
		}

		// 如果配置表里没有，而po里有，则删除
		for (Integer groupId : opts.daily_draw_forgerandom_map_add.keySet()) {
			if (!GameData.ForgedRandomAdds.containsKey(groupId)) {
				opts.daily_draw_forgerandom_map_add.remove(groupId);
			}
		}

	}

	public Date DailyDrawGetFreeTimeUpdateTime() {
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.set(Calendar.HOUR_OF_DAY, 5);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (h > 5)
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public SevenDayPackageGetInfoResponse getSevenDayPackageGetInfo() {
		SevenDayPackageGetInfoResponse.Builder result = SevenDayPackageGetInfoResponse.newBuilder();

		// 活动时间配置
		ActivityExt activityExt = findActivityByType(Const.ActivityRewardType.OPEN_SEVEN_DAY.getValue());
		if (activityExt == null) {
			result.setS2CCode(PomeloRequest.FAIL);
			result.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			return result.build();
		}

		// 活动信息
		SevenDayPackageInfo.Builder info = SevenDayPackageInfo.newBuilder();
		info.setBeginTime(activityExt.openTime);
		info.setEndTime(activityExt.closeTime);
		info.setDescribe(activityExt.activityRule);

		// 奖励配置
		for (int i = 0; i < opts.sevendayList.size(); i++) {
			SevenLoginCO sevenLoginCO = GameData.SevenLogins.get(i + 1);
			SevenDayPackageAwardInfo.Builder award = SevenDayPackageAwardInfo.newBuilder();
			award.setPackageId(sevenLoginCO.id);
			award.setItemcode(sevenLoginCO.item1code);
			award.setItemcount(sevenLoginCO.item1count);
			award.setItemmodel(sevenLoginCO.item1Model);
			award.setState(opts.sevendayList.get(i));
			info.addSevenDayPackageAwardInfo(award);
		}

		result.setS2CCode(PomeloRequest.OK);
		result.setSevenDayPackageInfo(info);
		return result.build();
	}

	// 领取七日登录奖励...
	public SevenDayPackageAwardResponse receiveSevenDayPackageAward(int packageId) {
		SevenDayPackageAwardResponse.Builder result = SevenDayPackageAwardResponse.newBuilder();

		// 超过7天
		if (packageId > 7) {
			result.setS2CCode(PomeloRequest.OK);
			return result.build();
		}

		// 不可领取
		if (opts.sevendayList.get(packageId - 1) != 1) {
			result.setS2CCode(PomeloRequest.OK);
			return result.build();
		}

		// List<SevenLoginCO> cos = GameData
		// .findSevenLogins((v) -> v.round == opts.sevenday_round && v.id ==
		// opts.sevenday_day);
		// if (cos.isEmpty()) {
		// result.setS2CCode(PomeloRequest.OK);
		// return result.build();
		// }

		// 条件没有问题，状态修正
		opts.sevendayList.set(packageId - 1, 2);
		Out.info("领取七日登录礼包 playerId=", player.getId(), ",day=", packageId);

		SevenLoginCO co = GameData.SevenLogins.get(packageId);
		this.player.getWnBag().addCodeItemMail(co.item1code, co.item1count, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.ACTIVITY_SEVENDAY, SysMailConst.BAG_FULL_COMMON);
		result.setS2CCode(PomeloRequest.OK);

		this.updateSuperScriptList();
		return result.build();
	}

	// 刷新7日登录奖励...
	private void refreshSevendayLogin() {
		for (int i = 0; i < opts.sevendayList.size(); i++) {
			if (opts.sevendayList.get(i) == 0) {
				opts.sevendayList.set(i, 1);
				break;
			}
		}
	}

	/**
	 * 获取资源找回信息.
	 */
	public RecoveredInfoResponse getRecoveredGetInfo() {
		RecoveredInfoResponse.Builder result = RecoveredInfoResponse.newBuilder();

		// 活动时间配置
		ActivityExt activityExt = findActivityByType(Const.ActivityRewardType.RECOVERY.getValue());
		if (activityExt == null) {
			result.setS2CCode(PomeloRequest.FAIL);
			result.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			return result.build();
		}

		// 活动信息
		RecoveredInfo.Builder info = RecoveredInfo.newBuilder();
		info.setBeginTime(activityExt.openTime);
		info.setEndTime(activityExt.closeTime);
		info.setDescribe(activityExt.activityRule);

		int recoveryDay = this.getRecoveryDay(activityExt.activityID);

		// 奖励配置
		for (Map.Entry<Integer, Integer> e : opts.recovery.entrySet()) {
			RecoveryExt t = GameData.Recoverys.get(e.getKey());
			if (t != null) {
				int count = e.getValue();
				RecoveredSourceInfo.Builder rsi = RecoveredSourceInfo.newBuilder();
				rsi.setId(t.iD);
				rsi.setSourceName(t.name);
				rsi.setMinDate(count);
				rsi.setMaxDate(this.getRecoveryMaxCount(t.type) * recoveryDay);
				rsi.setState(count > 0 ? 0 : 1);// 找回资源状态 0未找回 1已找回
				rsi.setNeedDiamond(t.cost * count);

				int recoverCount = count > 0 ? count : opts.recoveryHistory.getOrDefault(e.getKey(), 0);
				this.buildRecoveredItem(rsi, t.item1code, t.num1 * recoverCount);
				this.buildRecoveredItem(rsi, t.item2code, t.num2 * recoverCount);
				this.buildRecoveredItem(rsi, t.item3code, t.num3 * recoverCount);
				this.buildRecoveredItem(rsi, t.item4code, t.num4 * recoverCount);

				info.addRecoveredSourceInfo(rsi.build());
			}
		}
		result.setRecoveredInfo(info.build());
		result.setS2CCode(PomeloRequest.OK);
		return result.build();
	}

	private void buildRecoveredItem(RecoveredSourceInfo.Builder rsi, String itemcode, int num) {
		if (StringUtils.isNotEmpty(itemcode)) {
			RecoveredItem.Builder item = RecoveredItem.newBuilder();
			item.setCode(itemcode);
			item.setNum(num);
			rsi.addRecoveredItems(item);
		}
	}

	/**
	 * 一键找回功能.
	 */
	public RecoveredResponse recoveredRequest(int id, int type) {
		RecoveredResponse.Builder result = RecoveredResponse.newBuilder();
		if (!opts.recovery.containsKey(id)) {
			result.setS2CCode(PomeloRequest.FAIL);
			return result.build();
		}

		// 可追回次数...
		int count = opts.recovery.get(id);
		if (count <= 0) {
			result.setS2CCode(PomeloRequest.FAIL);
			return result.build();
		}

		RecoveryExt template = GameData.findRecoverys((v) -> v.iD == id).get(0);
		int totalCost = count * template.cost;
		// 找回方式：0免费找回,1完美找回
		if (type == 1) {
			if (!player.moneyManager.enoughTicketAndDiamond(totalCost)) {
				result.setS2CCode(PomeloRequest.FAIL);
				result.setS2CMsg(LangService.getValue("TICKET_NOT_ENOUGH"));
				return result.build();
			}
		}

		// 活动时间配置
		ActivityExt activityExt = findActivityByType(Const.ActivityRewardType.RECOVERY.getValue());
		if (activityExt == null) {
			result.setS2CCode(PomeloRequest.FAIL);
			return result.build();
		}

		float ratio = 0.5F;
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == activityExt.activityID);
		for (ActivityConfigExt t : props) {
			if (type == 0 && "Activity_Recovery_FreeRatio".equals(t.notes1)) {
				ratio = t.parameter1 / 100F;
				break;
			} else if (type == 1 && "Activity_Recovery_PerfectRatio".equals(t.notes1)) {
				ratio = t.parameter1 / 100F;
				break;
			}
		}
		List<SimpleItemInfo> items = new ArrayList<>(4);
		for (int i = 1; i <= 4; ++i) {
			String codeKey = "item" + i + "code";
			String countKey = "num" + i;
			try {
				String itemCode = ClassUtil.getProperty(template, codeKey).toString();
				if (StringUtils.isNotEmpty(itemCode)) {
					SimpleItemInfo item = new SimpleItemInfo();
					item.itemCode = itemCode;
					item.itemNum = (int) Math.ceil(((int) ClassUtil.getProperty(template, countKey)) * count * ratio);
					items.add(item);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}

		if (!this.player.getWnBag().testAddCodeItems(items)) {
			result.setS2CCode(PomeloRequest.FAIL);
			result.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
			return result.build();
		}

		Out.info("资源找回，roleId=", player.getId(), ",id=", id, ",type=", type, ",count=", count);

		if (type == 1) {
			player.moneyManager.costTicketAndDiamond(totalCost, GOODS_CHANGE_TYPE.recovered);
		}
		opts.recovery.put(id, 0);// 可找回的先清0...

		this.player.getWnBag().addCodeItems(items, Const.GOODS_CHANGE_TYPE.recovered);

		this.updateSuperScriptList();
		result.setS2CCode(PomeloRequest.OK);
		return result.build();
	}

	/**
	 * 刷新资源找回数据...
	 */
	private void refreshRecovered() {
		// 活动时间配置
		ActivityExt activityExt = findActivityByType(Const.ActivityRewardType.RECOVERY.getValue());
		if (activityExt == null) {
			return;
		}
		HashMap<Integer, Integer> stub = new HashMap<>(this.opts.recovery);
		this.opts.recovery.clear();// 清掉昨天的数据...
		this.opts.recoveryHistory.clear();

		int recoveryDay = this.getRecoveryDay(activityExt.activityID);
		int day = Math.max(Math.min(recoveryDay - 1, daysOfTwo(this.opts.refreshTime, new Date()) - 1), 0);
		int level = this.player.getPlayer().level;
		List<RecoveryExt> templates = GameData.findRecoverys((v) -> v.minLevel <= level && level <= v.maxLevel);
		for (RecoveryExt t : templates) {
			switch (t.type) {
			case 1:// 1=镇妖塔,没扫荡就算1次
			{
				if (player.demonTowerManager.getSweepCountLeft() > 0) {
					int progress = 0;
					int max = this.getRecoveryMaxCount(t.type);
					this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
				}
			}
				break;
			case 2:// 2=皓月镜
			{
				int progress = player.dailyActivityMgr.getTaskInfo(3).process;
				int max = this.getRecoveryMaxCount(t.type);
				this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
			}
				break;
			case 3:// 3=师门任务
			{
				int progress = player.dailyActivityMgr.getTaskInfo(4).process;
				int max = this.getRecoveryMaxCount(t.type);
				this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
			}
				break;
			case 4:// 4-守护神宠
			{
				int progress = player.dailyActivityMgr.getTaskInfo(11).process;
				int max = this.getRecoveryMaxCount(t.type);
				this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
			}
				break;
			case 5:// 5=极限挑战
			{
				int progress = player.dailyActivityMgr.getTaskInfo(10).process;
				int max = this.getRecoveryMaxCount(t.type);
				this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
			}
				break;
			case 6:// 6=幻妖农场
			{
				int progress = player.dailyActivityMgr.getTaskInfo(12).process;
				int max = this.getRecoveryMaxCount(t.type);
				this.refreshRecovery(t.iD, day, max, progress, stub, recoveryDay);
			}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 获取对应类型所能找回最大次数.
	 */
	private int getRecoveryMaxCount(int type) {
		switch (type) {
		case 1:// 1=镇妖塔
			return 1;
		case 2:// 2=皓月镜
			return DailyActivityMgr.getConfig(3).maxCount;
		case 3:// 3=师门任务
			return DailyActivityMgr.getConfig(4).maxCount;
		case 4:// 4-守护神宠
			return DailyActivityMgr.getConfig(11).maxCount;
		case 5:// 5=极限挑战
			return DailyActivityMgr.getConfig(10).maxCount;
		case 6:// 6=幻妖农场
			return DailyActivityMgr.getConfig(12).maxCount;
		default:
			return 0;
		}
	}

	/**
	 * 获取资源找回时所配置的可找回天数.
	 */
	private int getRecoveryDay(int activityID) {
		List<ActivityConfigExt> props = GameData.findActivityConfigs((t) -> t.type == activityID && "Activity_Recovery_Days".equals(t.notes1));
		if (props.isEmpty()) {
			Out.error("未配置资源找回功能所需要的可找回天数参数.");
			return 1;// 可追回天数默认为1.
		}
		return props.get(0).parameter1;
	}

	private void refreshRecovery(Integer type, int day, int maxCount, int progress, HashMap<Integer, Integer> stub, int recoveryDay) {
		int count = maxCount - progress + day * maxCount;
		if (day == 0) {
			count += stub.getOrDefault(type, 0);
			count = Math.min(recoveryDay * maxCount, count);
		}
		if (count > 0) {
			this.opts.recovery.put(type, count);
			this.opts.recoveryHistory.put(type, count);
		}
	}

	public static int daysOfTwo(Date fDate, Date oDate) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(fDate);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(oDate);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return day2 - day1;
	}

	/**
	 * 判定还有没有可以找回的资源
	 */
	public boolean hasRecoveryCount() {
		for (Map.Entry<Integer, Integer> e : opts.recovery.entrySet()) {
			if (e.getValue() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * GM命令调整
	 */
	public void gmRecovered(int day) {
		Date old = this.opts.refreshTime;
		this.opts.refreshTime = DateUtils.addDays(old, -day);
		refreshRecovered();
		this.opts.refreshTime = old;
	}

	/**
	 * 缓存一次限时礼包,触发条件：1、极限大礼包：极限挑战死亡 2、降妖大礼包：镇妖塔死亡 3、飞升大礼包：等级到达指定等级
	 */
	public void triggerLimitTimeGift(int condition) {
		cachedLimitTimeGiftStack.push(condition);

		CheckLimitTimeGiftList();
	}

	/**
	 * 检测缓存的限时礼包推送需求，将符合条件的进行推送
	 */
	public void CheckLimitTimeGiftList() {

		if (player.area.getSceneType() != SCENE_TYPE.NORMAL.getValue()) {
			return;
		}

		boolean needPush = false;
		while (cachedLimitTimeGiftStack.size() > 0) {
			int condition = cachedLimitTimeGiftStack.pop();
			List<LimitTimeGiftCO> limitTimeGiftCOs = GameData.findLimitTimeGifts((t) -> {
				return t.condition == condition && t.minLevel <= player.getLevel() && t.maxLevel >= player.getLevel();
			});
			if (limitTimeGiftCOs.size() != 1) {
				continue;
			}
			LimitTimeGiftCO limitTimeGiftCO = limitTimeGiftCOs.get(0);

			// 只推一次，并且已经推送
			if (limitTimeGiftCO.onlyPushOne == 1 && opts.timeLimitGiftPushMap.get(limitTimeGiftCO.id) > 0) {
				continue;
			}

			// 本类的推送记录
			// Date lastPushTime =
			// opts.timeLimitGiftTriggeredTimeMap.get(limitTimeGiftCO.condition);
			// // 本类正在推送中
			// if (lastPushTime != null) {
			// if (System.currentTimeMillis() < lastPushTime.getTime() + 1000L * 60L *
			// limitTimeGiftCO.limitTime) {
			// continue;
			// }
			//
			// }

			// 已经购买过了
			if (opts.timeLimitGiftBuyMap.get(limitTimeGiftCO.id) > 0) {
				continue;
			}

			// 没随机到
			if (RandomUtil.getInt(0, 100) > limitTimeGiftCO.pushPro) {
				continue;
			}

			// 等级判定
			// if (limitTimeGiftCO.condition == 3 && player.getLevel() !=
			// limitTimeGiftCO.value) {
			// return;
			// }

			// 成功触发
			{
				// 日志
				needPush = true;
				boolean lastOver = false;
				Date lastTime = opts.timeLimitGiftTriggeredTimeMap.get(limitTimeGiftCO.condition);
				if (lastTime != null) {
					long offset = lastTime.getTime() + 1000L * 60L * limitTimeGiftCO.limitTime - System.currentTimeMillis();
					if (offset > 0) {
						lastOver = true;
					}
				}
				Out.info("推送限时礼包，id：", limitTimeGiftCO.id, "  是否覆盖上次推送：", lastOver);
			}
			opts.timeLimitGiftTriggeredTimeMap.put(limitTimeGiftCO.condition, new Date());
			opts.timeLimitGiftTriggeredIdMap.put(limitTimeGiftCO.condition, limitTimeGiftCO.id);
			int currentPushTime = opts.timeLimitGiftPushMap.get(limitTimeGiftCO.condition);
			opts.timeLimitGiftPushMap.put(limitTimeGiftCO.condition, currentPushTime + 1);

		}

		// 红点
		checkAndPushLimitTimeSuperScript();

		if (!needPush) {
			return;
		}

		// 页面
		LimitTimeGiftInfoPush.Builder builder = LimitTimeGiftInfoPush.newBuilder();
		builder.setS2CCode(PomeloRequest.OK);
		builder.addAllLimitTimeGiftInfo(getLimitTimeGiftInfos());
		player.receive("area.activityFavorPush.limitTimeGiftInfoPush", builder.build());

	}

	private void checkAndPushLimitTimeSuperScript() {
		List<SuperScriptType> superScriptTypes = getLimitTimeSuperScript();

		if (superScriptTypes.size() != 1) {
			return;

		}

		player.updateSuperScriptList(superScriptTypes);

		SuperScriptType superScriptType = superScriptTypes.get(0);

		if (superScriptType.getNumber() == 0) {
			return;
		}
		long delay = 1000L * superScriptType.getNumber() + 500L;// 预计结束时间再延后500ms，再次进行时间扫描及推送

		JobFactory.addDelayJob(() -> {
			checkAndPushLimitTimeSuperScript();
		}, delay);
	}

	public List<LimitTimeGiftInfo> getLimitTimeGiftInfos() {
		List<LimitTimeGiftInfo> limitTimeGiftInfos = new LinkedList<>();
		for (Map.Entry<Integer, Date> entry : opts.timeLimitGiftTriggeredTimeMap.entrySet()) {

			List<LimitTimeGiftCO> limitTimeGiftCOs = GameData.findLimitTimeGifts((t) -> {
				return t.condition == entry.getKey() && t.minLevel <= player.getLevel() && t.maxLevel >= player.getLevel();
			});
			if (limitTimeGiftCOs.size() != 1) {
				continue;
			}
			LimitTimeGiftCO limitTimeGiftCO = limitTimeGiftCOs.get(0);

			if (entry.getValue() == null) {
				continue;
			}

			if (opts.timeLimitGiftBuyMap.get(limitTimeGiftCO.id) > 0) {
				continue;
			}

			long offset = entry.getValue().getTime() + 1000L * 60L * limitTimeGiftCO.limitTime - System.currentTimeMillis();
			if (offset <= 0) {
				continue;
			}
			LimitTimeGiftInfo.Builder builder = LimitTimeGiftInfo.newBuilder();
			builder.setId(limitTimeGiftCO.id);
			builder.setSecondRemain((int) (offset / 1000) + 1);
			limitTimeGiftInfos.add(builder.build());
		}
		return limitTimeGiftInfos;
	}

	/**
	 * 购买限时礼包
	 */

	public int BugLimitTimeGift(int id) {
		LimitTimeGiftCO limitTimeGiftCO = GameData.LimitTimeGifts.get(id);

		// id不存在
		if (!opts.timeLimitGiftBuyMap.containsKey(limitTimeGiftCO.id)) {
			return 1;
		}

		// 已购买次数
		int buyCount = opts.timeLimitGiftBuyMap.get(limitTimeGiftCO.id);
		// 已经买过了
		if (buyCount > 0) {
			return 2;
		}

		// 元宝不够
		if (player.moneyManager.getDiamond() < limitTimeGiftCO.price) {
			return 3;
		}

		opts.timeLimitGiftBuyMap.put(limitTimeGiftCO.id, buyCount + 1);

		player.moneyManager.costDiamond(limitTimeGiftCO.price, Const.GOODS_CHANGE_TYPE.LimitTimeGift);

		String[] strs1 = limitTimeGiftCO.rewardItem.split(";");
		for (String strs1_item : strs1) {
			String[] strs2 = strs1_item.split(":");
			List<NormalItem> normalItems = ItemUtil.createItemsByItemCode(strs2[0], Integer.parseInt(strs2[1]));

			for (NormalItem normalItem : normalItems) {
				normalItem.setBind(1);
			}
			player.bag.addCodeItemMail(normalItems, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.LimitTimeGift, SysMailConst.BAG_FULL_COMMON);

		}

		checkAndPushLimitTimeSuperScript();

		return 0;

	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:
			triggerLimitTimeGift(3);
			break;

		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.ACTIVITY;
	}
}