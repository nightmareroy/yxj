package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.ContinuousRechargeAwardRequest;
import pomelo.area.ActivityFavorHandler.ContinuousRechargeAwardResponse;

/**
 * 领取连续充值奖励.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityFavorHandler.continuousRechargeAwardRequest")
public class ContinuousRechargeAwardHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		ContinuousRechargeAwardRequest request = ContinuousRechargeAwardRequest.parseFrom(pak.getRemaingBytes());
		final int day = request.getDay();

		if (day < 0) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		WNPlayer player = (WNPlayer) pak.getPlayer();

		PomeloResponse errorcode = RechargeActivityService.getInstance().receiveContinuousRecharge(player, day);
		if (errorcode != null) {
			return errorcode;
		}

		return new PomeloResponse() {
			protected void write() throws IOException {
				ContinuousRechargeAwardResponse.Builder res = ContinuousRechargeAwardResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}