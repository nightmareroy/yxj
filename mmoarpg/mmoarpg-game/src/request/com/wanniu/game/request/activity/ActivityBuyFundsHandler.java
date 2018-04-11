package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.HashMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.ActivityCenterManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityBuyFundsRes;

/**
 * 购买基金入口.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityHandler.activityBuyFundsRequest")
public class ActivityBuyFundsHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		HashMap<Integer, Integer> actInfo = player.activityManager.getActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue());
		if (actInfo != null) {
			return new ErrorResponse(LangService.getValue("ACTIVITY_NOT_REQUIRMENT"));
		}

		int needDiamond = GlobalConfig.Activity_Fund_Buy;
		if (!player.moneyManager.costDiamond(needDiamond, Const.GOODS_CHANGE_TYPE.BUY_FUNDS)) {
			return new ErrorResponse(LangService.getValue("ACTIVITY_NOT_CONDITION"));
		}

		Out.info("购买基金，playerId=", player.getId());
		HashMap<Integer, Integer> data = new HashMap<>();
		player.activityManager.addActivityInfo(Const.ActivityRewardType.FOUNDATION.getValue(), data);

		ActivityCenterManager.getIntance().addFundRecord(GWorld.__SERVER_ID);

		return new PomeloResponse() {
			protected void write() throws IOException {
				ActivityBuyFundsRes.Builder res = ActivityBuyFundsRes.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

				player.activityManager.updateSuperScriptList();
			}
		};
	}
}