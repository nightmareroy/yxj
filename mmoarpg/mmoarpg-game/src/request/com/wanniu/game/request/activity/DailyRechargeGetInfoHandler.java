package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.prepaid.PrepaidManager;

import pomelo.area.ActivityFavorHandler.DailyRechargeGetInfoRequest;
import pomelo.area.ActivityFavorHandler.DailyRechargeGetInfoResponse;
import pomelo.area.ActivityHandler.totalInfo;


@GClientEvent("area.activityFavorHandler.dailyRechargeGetInfoRequest")
public class DailyRechargeGetInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		DailyRechargeGetInfoRequest req = DailyRechargeGetInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				ActivityManager activityManager=player.activityManager;
				PrepaidManager prepaidManager=player.prepaidManager;
				
				DailyRechargeGetInfoResponse.Builder res=DailyRechargeGetInfoResponse.newBuilder();
				
				
				totalInfo totalInfo=activityManager.DailyRecharge_Today().build();
				res.setTotalInfo(totalInfo);
				res.setRechargeNum(prepaidManager.getDailyCharge());
				res.setRechargeMax(activityManager.DailyRecharge_GetTodayMax());
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
