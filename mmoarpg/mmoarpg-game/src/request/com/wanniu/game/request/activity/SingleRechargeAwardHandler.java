package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.ContinuousRechargeAwardResponse;
import pomelo.area.ActivityFavorHandler.SingleRechargeAwardRequest;

/**
 * 领取单笔充值奖励.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityFavorHandler.singleRechargeAwardRequest")
public class SingleRechargeAwardHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		SingleRechargeAwardRequest request = SingleRechargeAwardRequest.parseFrom(pak.getRemaingBytes());
		final int id = request.getId();
		WNPlayer player = (WNPlayer) pak.getPlayer();

		PomeloResponse errorcode = RechargeActivityService.getInstance().receiveSingleRecharge(player, id);
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