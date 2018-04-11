/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package com.wanniu.game.activity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.SUPERSCRIPT_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.AddRechargeLimitCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.StartSerRechargeCO;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ContinuousRechargePO;
import com.wanniu.game.poes.RechargeActivityPO;
import com.wanniu.game.poes.RevelryRechargePO;
import com.wanniu.game.poes.SingleRechargePO;
import com.wanniu.game.poes.TotalConsumePO;
import com.wanniu.game.poes.TotalRechargePO;
import com.wanniu.redis.PlayerPOManager;

/**
 * 充值活动业务逻辑处理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class RechargeActivityService {
	private static final RechargeActivityService instance = new RechargeActivityService();
	// 未完成状态
	private static final int STATE_NOT_COMPLETE = 0;
	// 已完成，待领取
	private static final int STATE_COMPLETED = 1;
	// 已领取
	private static final int STATE_RECEIVED = 2;

	public static RechargeActivityService getInstance() {
		return instance;
	}

	/**
	 * 处理充值事件.
	 * 
	 * @param playerId 玩家ID
	 * @param todayPayRmb 今天累计充值
	 * @param payRmb 本次单笔充值
	 */
	public void onPayEvent(String playerId, int todayPayRmb, int payRmb) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		long now = System.currentTimeMillis();

		// 连续充值
		if (inContinuousTime(now)) {
			onContinuousRecharge(playerId, po, todayPayRmb);
		}

		// 单笔充值
		if (inSingleTime(now)) {
			onSingleRecharge(playerId, po, payRmb);
		}

		// 累计充值
		if (inActvityTime(Const.ActivityRewardType.TOTAL_PAY, now)) {
			onTotalRecharge(playerId, po, payRmb);
		}

		// 开服狂欢
		if (inRevelayTime()) {
			onRevelryRecharge(playerId, po, todayPayRmb);
		}
	}

	/**
	 * 处理消耗事件.
	 */
	public void onConsumeEvent(String playerId, int value) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		long now = System.currentTimeMillis();

		// 累计消耗
		if (inActvityTime(Const.ActivityRewardType.TOTAL_CONSUME, now)) {
			onTotalConsume(playerId, po, value);
		}
	}

	private void onTotalConsume(String playerId, RechargeActivityPO po, int value) {
		refreshTotalConsumePO(playerId, po);
		TotalConsumePO info = po.totalConsume;
		info.setRmb(info.getRmb() + value);
		Out.info("累计消耗金额变更 playerId=", playerId, ", rmb=", info.getRmb());
	}

	private void refreshTotalConsumePO(String playerId, RechargeActivityPO po) {
		if (po.totalConsume == null || !inActvityTime(Const.ActivityRewardType.TOTAL_CONSUME, po.totalConsume.getDate().getTime())) {
			Out.info("重置累计消耗PO playerId=", playerId, ", po=", JSON.toJSONString(po.totalConsume));
			po.totalConsume = new TotalConsumePO();
			po.totalConsume.setDate(new Date());
			po.totalConsume.setRmb(0);
		}
	}

	public int getTotalConsumeValue(String playerId) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		if (po.totalConsume == null) {
			return 0;
		}
		refreshTotalConsumePO(playerId, po);
		return po.totalConsume.getRmb();
	}

	private void onTotalRecharge(String playerId, RechargeActivityPO po, int payRmb) {
		refreshTotalRechargePO(playerId, po);
		TotalRechargePO info = po.totalRecharge;
		info.setRmb(info.getRmb() + payRmb);
		Out.info("累计充值金额变更 playerId=", playerId, ", rmb=", info.getRmb());
	}

	// 刷新PO信息
	private void refreshTotalRechargePO(String playerId, RechargeActivityPO po) {
		if (po.totalRecharge == null || !inActvityTime(Const.ActivityRewardType.TOTAL_PAY, po.totalRecharge.getDate().getTime())) {
			Out.info("重置累计充值PO playerId=", playerId, ", po=", JSON.toJSONString(po.totalRecharge));
			po.totalRecharge = new TotalRechargePO();
			po.totalRecharge.setDate(new Date());
			po.totalRecharge.setRmb(0);
		}
	}

	public int getTotalPayValue(String playerId) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		if (po.totalRecharge == null) {
			return 0;
		}
		refreshTotalRechargePO(playerId, po);
		return po.totalRecharge.getRmb();
	}

	/**
	 * 判定是否在指定活动期间内.
	 */
	private boolean inActvityTime(Const.ActivityRewardType type, long time) {
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityTab == type.getValue());
		if (props.isEmpty()) {
			return false;
		}
		ActivityExt activityExt = props.get(0);
		if (activityExt == null) {
			return false;
		}
		if (activityExt.beginTime > time) {
			return false;
		}
		if (activityExt.endTime < time) {
			return false;
		}
		return true;
	}

	private void onContinuousRecharge(String playerId, RechargeActivityPO po, int rmb) {
		refreshContinuousRechargePO(po);

		ContinuousRechargePO info = po.continuousRecharge;
		// 未完成状态
		if (info.getState().getOrDefault(info.getDay(), STATE_NOT_COMPLETE) == STATE_NOT_COMPLETE) {
			AddRechargeLimitCO template = this.getAddRechargeLimitCO(info.getDay());
			// 指定金额....
			if (template != null && template.rechargeLimit * 100 <= rmb) {
				info.getState().put(info.getDay(), STATE_COMPLETED);
				info.setDate(new Date());
				
				// 前面要全部完成.
				boolean flag = true;
				for (AddRechargeLimitCO temlate : GameData.AddRechargeLimits.values()) {
					if (po.continuousRecharge.getState().getOrDefault(temlate.addTime, STATE_NOT_COMPLETE) == STATE_NOT_COMPLETE) {
						flag = false;
						break;
					}
				}
				// 最终的大奖
				if (flag) {
					info.getState().put(0, STATE_COMPLETED);
				}

				// 计算红点(值为2，活动开着且有奖励)...
				WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
				if (player != null) {
					player.updateSuperScript(SUPERSCRIPT_TYPE.CONTINUOUS_RECHARGE, 2);
				}
			}
		}
	}

	private AddRechargeLimitCO getAddRechargeLimitCO(int day) {
		List<AddRechargeLimitCO> ts = GameData.findAddRechargeLimits(v -> day == v.addTime);
		if (!ts.isEmpty()) {
			return ts.get(0);
		}
		return null;
	}

	// 刷新连续充值信息...
	private void refreshContinuousRechargePO(RechargeActivityPO po) {
		// 初始化
		if (po.continuousRecharge == null || !inContinuousTime(po.continuousRecharge.getDate().getTime())) {
			po.continuousRecharge = new ContinuousRechargePO();
			po.continuousRecharge.setDate(new Date());
			po.continuousRecharge.setDay(1);
			po.continuousRecharge.setState(new HashMap<>());
		} else {
			ContinuousRechargePO info = po.continuousRecharge;
			// 完成的了情况
			if (info.getState().getOrDefault(info.getDay(), 0) > 0) {
				if (!DateUtils.isSameDay(info.getDate(), new Date())) {
					info.setDate(new Date());
					info.setDay(info.getDay() + 1);
				}
			}
		}
	}

	public RechargeActivityPO getRechargeActivityPO(String playerId) {
		RechargeActivityPO po = PlayerPOManager.findPO(ConstsTR.playerRechargeActivityTR, playerId, RechargeActivityPO.class);
		if (po == null) {
			po = new RechargeActivityPO();
			PlayerPOManager.put(ConstsTR.playerRechargeActivityTR, playerId, po);
		}
		return po;
	}

	public Map<Integer, Integer> getContinuousRechargeInfo(String playerId) {
		// 活动期间...
		if (!inContinuousTime(System.currentTimeMillis())) {
			return Collections.emptyMap();
		}

		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		refreshContinuousRechargePO(po);

		// 当前状态
		return po.continuousRecharge.getState();
	}

	public int getContinuousRechargeDay(String playerId) {
		// 活动期间...
		if (!inContinuousTime(System.currentTimeMillis())) {
			return 0;
		}

		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		refreshContinuousRechargePO(po);
		// 当前状态
		return po.continuousRecharge.getDay();
	}

	private boolean inContinuousTime(long time) {
		int day = this.getOpenServerDay();
		return 0 < day && day <= 7;
		// long startTime =
		// DateUtil.format(GlobalConfig.AddRecharge_Time_Begin).getTime();
		// if (startTime > time) {
		// return false;
		// }
		// long endTime = DateUtil.format(GlobalConfig.AddRecharge_Time_End).getTime();
		// if (endTime < time) {
		// return false;
		// }
	}

	public PomeloResponse receiveContinuousRecharge(WNPlayer player, int day) {
		RechargeActivityPO po = this.getRechargeActivityPO(player.getId());
		if (po == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		if (po.continuousRecharge == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 不是待领取状态...
		if (po.continuousRecharge.getState().getOrDefault(day, STATE_NOT_COMPLETE) != STATE_COMPLETED) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 标识为已领取状态...
		po.continuousRecharge.getState().put(day, STATE_RECEIVED);
		Out.info("领取连续充值奖励，playerId=", player.getId(), ", day=", day);

		// 给奖励
		String reward;
		if (day > 0) {
			AddRechargeLimitCO template = this.getAddRechargeLimitCO(day);
			reward = template.rechargeFReward;
		} else {
			reward = GlobalConfig.AddRecharge_Reward;
		}

		List<NormalItem> result = new ArrayList<>();
		String[] strs1 = reward.split(",");
		for (String strs1_item : strs1) {
			String[] strs2 = strs1_item.split(":");
			result.addAll(ItemUtil.createItemsByItemCode(strs2[0], Integer.parseInt(strs2[1])));
		}
		player.bag.addCodeItemMail(result, Const.ForceType.BIND, Const.GOODS_CHANGE_TYPE.ContinuousRecharge, SysMailConst.BAG_FULL_COMMON);

		// 刷一下红点...
		this.onLogin(player);

		return null;
	}

	public Map<Integer, Integer> getSingleRechargeInfo(String playerId) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		refreshSingleRechargePO(po);
		// 当前状态
		return po.singleRecharge.getState();
	}

	private void refreshSingleRechargePO(RechargeActivityPO po) {
		// 初始化
		if (po.singleRecharge == null || !inSingleTime(po.singleRecharge.getDate().getTime())) {
			po.singleRecharge = new SingleRechargePO();
			po.singleRecharge.setDate(new Date());
			po.singleRecharge.setState(new HashMap<>());
		}
	}

	private boolean inSingleTime(long time) {
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityTab == Const.ActivityRewardType.SINGLE_RECHARGE.getValue());
		if (props.isEmpty()) {
			return false;
		}
		ActivityExt activityExt = props.get(0);
		if (activityExt == null) {
			return false;
		}
		if (activityExt.beginTime > time) {
			return false;
		}
		if (activityExt.endTime < time) {
			return false;
		}
		return true;
	}

	private void onSingleRecharge(String playerId, RechargeActivityPO po, int payRmb) {
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityTab == Const.ActivityRewardType.SINGLE_RECHARGE.getValue());
		if (props.isEmpty()) {
			return;
		}
		ActivityExt activityExt = props.get(0);
		if (activityExt == null) {
			return;
		}
		List<ActivityConfigExt> activityConfigExts = GameData.findActivityConfigs((t) -> t.type == activityExt.activityID && payRmb == t.parameter1 * 100);
		if (activityConfigExts.isEmpty()) {
			return;
		}

		ActivityConfigExt template = activityConfigExts.get(0);

		refreshSingleRechargePO(po);

		SingleRechargePO info = po.singleRecharge;

		// 未完成状态
		if (info.getState().getOrDefault(template.id, STATE_NOT_COMPLETE) == STATE_NOT_COMPLETE) {
			info.getState().put(template.id, STATE_COMPLETED);
			Out.info("完成单笔充值活动 playerId=", playerId, ", id=", template.id, ", rmb=", payRmb);
			// 计算红点...
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null) {
				player.activityManager.updateSuperScriptList();
			}
		}
	}

	public int getSingleRechargeRedPoint(String playerId) {
		RechargeActivityPO po = PlayerPOManager.findPO(ConstsTR.playerRechargeActivityTR, playerId, RechargeActivityPO.class);
		if (po == null) {
			return 0;
		}
		if (po.singleRecharge == null) {
			return 0;
		}
		if (!inSingleTime(System.currentTimeMillis())) {
			return 0;
		}
		refreshSingleRechargePO(po);
		for (Integer i : po.singleRecharge.getState().values()) {
			if (i == STATE_COMPLETED) {
				return 1;
			}
		}
		return 0;
	}

	public PomeloResponse receiveSingleRecharge(WNPlayer player, int id) {
		List<ActivityConfigExt> activityConfigExts = GameData.findActivityConfigs((t) -> t.id == id);
		if (activityConfigExts.isEmpty()) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		RechargeActivityPO po = this.getRechargeActivityPO(player.getId());
		if (po == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		if (po.singleRecharge == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 不是待领取状态...
		if (po.singleRecharge.getState().getOrDefault(id, STATE_NOT_COMPLETE) != STATE_COMPLETED) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 标识为已领取状态...
		po.singleRecharge.getState().put(id, STATE_RECEIVED);
		Out.info("领取单笔充值奖励 playerId=", player.getId(), ", id=", id);

		ActivityConfigExt template = activityConfigExts.get(0);
		// 给奖励
		ArrayList<SimpleItemInfo> reward = player.activityManager.getRankReward(template.RankReward);
		List<NormalItem> result = new ArrayList<>();
		for (SimpleItemInfo sii : reward) {
			result.addAll(ItemUtil.createItemsByItemCode(sii.itemCode, sii.itemNum));
		}
		player.bag.addCodeItemMail(result, Const.ForceType.BIND, Const.GOODS_CHANGE_TYPE.SingleRecharge, SysMailConst.BAG_FULL_COMMON);
		return null;
	}

	public void onLogin(WNPlayer player) {
		int value = 0;
		if (inContinuousTime(System.currentTimeMillis())) {
			value = 1;

			RechargeActivityPO po = this.getRechargeActivityPO(player.getId());
			refreshContinuousRechargePO(po);
			for (int v : po.continuousRecharge.getState().values()) {
				if (v == STATE_COMPLETED) {
					value = 2;
					break;
				}
			}
		}

		player.updateSuperScript(Const.SUPERSCRIPT_TYPE.CONTINUOUS_RECHARGE, value);
	}

	// 开服狂欢的累计充值活动
	// 计算一下 今天是开服第几天...
	public int getOpenServerDay() {
		return (int) ChronoUnit.DAYS.between(GWorld.OPEN_SERVER_DATE, LocalDate.now()) + 1;
	}

	public Map<Integer, String> getAllColumn() {
		Map<Integer, String> result = new HashMap<>();
		for (StartSerRechargeCO template : GameData.StartSerRecharges.values()) {
			result.put(template.date, template.showDate);
		}
		return result;
	}

	public RevelryRechargePO getRevelryRechargeInfo(String playerId, int day) {
		RechargeActivityPO po = this.getRechargeActivityPO(playerId);
		if (po.revelryRecharge == null) {
			return null;
		}
		return po.revelryRecharge.get(day);
	}

	private boolean inRevelayTime() {
		LocalDate openServerDate = GWorld.OPEN_SERVER_DATE;
		LocalDate now = LocalDate.now();
		return GameData.Revelrys.values().stream().filter(v -> v.isOpen == 1 && now.isBefore(openServerDate.plusDays(v.endDays2))).findFirst().isPresent();
	}

	private void onRevelryRecharge(String playerId, RechargeActivityPO po, int todayPayRmb) {
		refreshRevelryRechargePO(po);
		Integer day = getOpenServerDay();
		RevelryRechargePO info = po.revelryRecharge.get(day);
		if (info == null) {
			info = new RevelryRechargePO();
			info.setState(new HashMap<>());
			po.revelryRecharge.put(day, info);
		}
		info.setRmb(todayPayRmb);

		boolean flag = false;
		for (StartSerRechargeCO template : GameData.findStartSerRecharges(v -> v.date == day)) {
			if (info.getRmb() >= template.rechargeNumber * 100) {
				if (info.getState().getOrDefault(template.iD, STATE_NOT_COMPLETE) == STATE_NOT_COMPLETE) {
					info.getState().put(template.iD, STATE_COMPLETED);
					flag = true;
				}
			}
		}
		if (flag) {// 推送红点.
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null) {
				player.activityManager.updateSuperScriptList();
			}
		}
	}

	private void refreshRevelryRechargePO(RechargeActivityPO po) {
		if (po.revelryRecharge == null) {
			po.revelryRecharge = new HashMap<>();
		}
	}

	public PomeloResponse receiveRevelryRecharge(WNPlayer player, int id) {
		List<StartSerRechargeCO> templates = GameData.findStartSerRecharges(v -> v.iD == id);
		if (templates.isEmpty()) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		StartSerRechargeCO template = templates.get(0);
		if (template == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		RevelryRechargePO po = this.getRevelryRechargeInfo(player.getId(), template.date);
		if (po == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		if (po.getState().getOrDefault(template.iD, STATE_NOT_COMPLETE) == STATE_COMPLETED) {
			po.getState().put(template.iD, STATE_RECEIVED);
			Out.info("领取开服狂欢的充值奖励，playerId=", player.getId(), ", day=", template.date, ", id=", template.iD);

			List<NormalItem> result = new ArrayList<>();
			result.addAll(ItemUtil.createItemsByItemCode(template.reward1, template.num1));
			result.addAll(ItemUtil.createItemsByItemCode(template.reward2, template.num2));
			result.addAll(ItemUtil.createItemsByItemCode(template.reward3, template.num3));
			result.addAll(ItemUtil.createItemsByItemCode(template.reward4, template.num4));
			result.addAll(ItemUtil.createItemsByItemCode(template.reward5, template.num5));
			player.bag.addCodeItemMail(result, Const.ForceType.BIND, Const.GOODS_CHANGE_TYPE.RevelryRecharge, SysMailConst.BAG_FULL_COMMON);
		}

		// 刷一下红点...
		player.activityManager.updateSuperScriptList();
		return null;
	}

	public int getRevelryRechargeRedPoint(String playerId) {
		RechargeActivityPO po = PlayerPOManager.findPO(ConstsTR.playerRechargeActivityTR, playerId, RechargeActivityPO.class);
		if (po == null) {
			return 0;
		}
		if (po.revelryRecharge == null) {
			return 0;
		}
		if (!inRevelayTime()) {
			return 0;
		}
		for (RevelryRechargePO p : po.revelryRecharge.values()) {
			for (Integer i : p.getState().values()) {
				if (i == STATE_COMPLETED) {
					return 1;
				}
			}
		}
		return 0;
	}
}