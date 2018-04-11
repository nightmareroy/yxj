package com.wanniu.game.request.revelry;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.player.WNPlayer;

import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeAwardRequest;
import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeAwardResponse;

/**
 * 冲榜累计充值领取奖励.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryRechargeAwardRequest")
public class RevelryRechargeAwardHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		RevelryRechargeAwardRequest req = RevelryRechargeAwardRequest.parseFrom(pak.getRemaingBytes());
		final int id = req.getId();
		WNPlayer player = (WNPlayer) pak.getPlayer();

		PomeloResponse errorcode = RechargeActivityService.getInstance().receiveRevelryRecharge(player, id);
		if (errorcode != null) {
			return errorcode;
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RevelryRechargeAwardResponse.Builder result = RevelryRechargeAwardResponse.newBuilder();
				result.setS2CCode(OK);
				body.writeBytes(result.build().toByteArray());
			}
		};
	}
}