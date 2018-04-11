package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.SingleRechargeAwardInfo;
import pomelo.area.ActivityFavorHandler.SingleRechargeGetInfoResponse;
import pomelo.area.ActivityFavorHandler.SingleRechargeInfo;
import pomelo.area.ActivityFavorHandler.SingleRechargeItem;

/**
 * 单笔充值入口.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityFavorHandler.singleRechargeGetInfoRequest")
public class SingleRechargeGetInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		ActivityExt activityExt = player.activityManager.findActivityByType(Const.ActivityRewardType.SINGLE_RECHARGE.getValue());
		if (activityExt == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 活动信息// 活动时间配置
		SingleRechargeInfo.Builder info = SingleRechargeInfo.newBuilder();
		info.setBeginTime(activityExt.openTime);
		info.setEndTime(activityExt.closeTime);
		info.setDescribe(activityExt.activityRule);

		final Map<Integer, Integer> stateInfo = RechargeActivityService.getInstance().getSingleRechargeInfo(player.getId());
		List<ActivityConfigExt> activityConfigExts = GameData.findActivityConfigs((t) -> t.type == activityExt.activityID);
		for (ActivityConfigExt template : activityConfigExts) {
			SingleRechargeAwardInfo.Builder srab = SingleRechargeAwardInfo.newBuilder();
			srab.setId(template.id);
			// 奖励...
			ArrayList<SimpleItemInfo> rewardItem = player.activityManager.getRankReward(template.RankReward);
			for (SimpleItemInfo sii : rewardItem) {
				SingleRechargeItem.Builder item = SingleRechargeItem.newBuilder();
				item.setCode(sii.itemCode);
				item.setGroupCount(sii.itemNum);
				srab.addItem(item);
			}
			srab.setState(stateInfo.getOrDefault(template.id, 0));
			srab.setCurrNum(0);
			srab.setNeedNum(template.parameter1);
			info.addSingleRechargeAwardInfo(srab);
		}

		SingleRechargeGetInfoResponse.Builder res = SingleRechargeGetInfoResponse.newBuilder();
		res.setS2CCode(OK);
		res.setSingleRechargeInfo(info);
		return new PomeloResponse() {
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}