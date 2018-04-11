package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.ActivityCenterManager;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityOpenFundsRes;

@GClientEvent("area.activityHandler.activityOpenFundsRequest")
public class ActivityOpenFundsHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ActivityOpenFundsRes.Builder res = player.activityManager.openFunds();
		res.setS2CCode(OK);
		res.setS2CNeedVipLevel(0);
		res.setS2CNeedDiamond(GlobalConfig.Activity_Fund_Buy);
		res.setS2CHasBuyNum(ActivityCenterManager.getIntance().getFundsNum(GWorld.__SERVER_ID));

		return new PomeloResponse() {
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}