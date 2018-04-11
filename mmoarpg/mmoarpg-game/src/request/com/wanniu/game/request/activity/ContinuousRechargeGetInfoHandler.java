package com.wanniu.game.request.activity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.DateUtils;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.data.AddRechargeLimitCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.ContinuousRechargeAwardInfo;
import pomelo.area.ActivityFavorHandler.ContinuousRechargeGetInfoResponse;
import pomelo.area.ActivityFavorHandler.ContinuousRechargeInfo;
import pomelo.area.ActivityFavorHandler.ContinuousRechargeItem;

/**
 * 连续充值入口.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityFavorHandler.continuousRechargeGetInfoRequest")
public class ContinuousRechargeGetInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		ContinuousRechargeGetInfoResponse.Builder res = ContinuousRechargeGetInfoResponse.newBuilder();

		ContinuousRechargeInfo.Builder info = ContinuousRechargeInfo.newBuilder();

		LocalDate openServerDate = GWorld.OPEN_SERVER_DATE;
		info.setBeginTime(openServerDate.atTime(0, 0, 0).format(DateUtils.F_YYYYMMDDHHMMSS));
		info.setEndTime(openServerDate.plusDays(6).atTime(23, 59, 59).format(DateUtils.F_YYYYMMDDHHMMSS));

		final Map<Integer, Integer> stateInfo = RechargeActivityService.getInstance().getContinuousRechargeInfo(player.getId());
		// 当前第几天，进度
		info.setDay(RechargeActivityService.getInstance().getContinuousRechargeDay(player.getId()));

		for (AddRechargeLimitCO temlate : GameData.AddRechargeLimits.values()) {
			ContinuousRechargeAwardInfo.Builder awardInfo = ContinuousRechargeAwardInfo.newBuilder();
			awardInfo.setDay(temlate.addTime);
			awardInfo.setMoney(temlate.rechargeLimit);

			String[] strs1 = temlate.rechargeFReward.split(",");
			for (String strs1_item : strs1) {
				ContinuousRechargeItem.Builder item = ContinuousRechargeItem.newBuilder();
				String[] strs2 = strs1_item.split(":");
				item.setCode(strs2[0]);
				item.setGroupCount(Integer.parseInt(strs2[1]));
				awardInfo.addItem(item);
			}

			awardInfo.setState(stateInfo.getOrDefault(temlate.addTime, 0));// 0=未领取，1=可领取，2=已领取
			info.addContinuousRechargeAwardInfo(awardInfo);

			// 是当天，需要采集一下进度参数
			if (awardInfo.getDay() == info.getDay()) {
				info.setNeedNum(awardInfo.getMoney());
				info.setCurrNum(player.prepaidManager.getTodayPayValue() / 100);
			}
		}

		{// 最终那个奖励...
			ContinuousRechargeAwardInfo.Builder awardInfo = ContinuousRechargeAwardInfo.newBuilder();
			awardInfo.setDay(0);
			awardInfo.setMoney(0);

			String[] strs1 = GlobalConfig.AddRecharge_Reward.split(",");
			for (String strs1_item : strs1) {
				ContinuousRechargeItem.Builder item = ContinuousRechargeItem.newBuilder();
				String[] strs2 = strs1_item.split(":");
				item.setCode(strs2[0]);
				item.setGroupCount(Integer.parseInt(strs2[1]));
				awardInfo.addItem(item);
			}

			awardInfo.setState(stateInfo.getOrDefault(0, 0));// 0=未领取，1=可领取，2=已领取
			info.addContinuousRechargeAwardInfo(awardInfo);
		}

		res.setS2CCode(OK);
		res.setContinuousRechargeInfo(info);

		return new PomeloResponse() {
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}