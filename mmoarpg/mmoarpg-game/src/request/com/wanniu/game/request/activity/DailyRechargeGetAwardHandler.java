package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.DailyRechargeGetAwardRequest;
import pomelo.area.ActivityFavorHandler.DailyRechargeGetAwardResponse;


@GClientEvent("area.activityFavorHandler.dailyRechargeGetAwardRequest")
public class DailyRechargeGetAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		DailyRechargeGetAwardRequest req = DailyRechargeGetAwardRequest.parseFrom(pak.getRemaingBytes());
		int awardId=req.getAwardId();
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				ActivityManager activityManager=player.activityManager;
//				PrepaidManager prepaidManager=player.prepaidManager;
				
				DailyRechargeGetAwardResponse.Builder res=DailyRechargeGetAwardResponse.newBuilder();
				
				
				boolean result=activityManager.DailyRecharge_GetAward(awardId);
				
				if(result)
				{
					res.setS2CCode(OK);
					Out.info(player.getId(),":每日充值奖励领取成功,奖励id:",awardId);
				}
				else {
					res.setS2CCode(FAIL);
					res.setS2CMsg("ACTIVITY_NOT_REQUIRMENT");
				}
				
				
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
