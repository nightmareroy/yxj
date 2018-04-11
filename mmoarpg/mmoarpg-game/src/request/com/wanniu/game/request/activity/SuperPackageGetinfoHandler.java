package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.prepaid.PrepaidManager;

import pomelo.area.ActivityFavorHandler.SuperPackageGetInfoRequest;
import pomelo.area.ActivityFavorHandler.SuperPackageGetInfoResponse;
import pomelo.area.ActivityFavorHandler.SuperPackageInfo;


@GClientEvent("area.activityFavorHandler.superPackageGetInfoRequest")
public class SuperPackageGetinfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		SuperPackageGetInfoRequest req = SuperPackageGetInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				ActivityManager activityManager=player.activityManager;
				PrepaidManager prepaidManager=player.prepaidManager;
				
				SuperPackageGetInfoResponse.Builder res=SuperPackageGetInfoResponse.newBuilder();
				SuperPackageInfo.Builder spiBuilder=activityManager.SuperPackage_GetInfo();
				
//				
//				totalInfo totalInfo=activityManager.payToday().build();
//				res.setTotalInfo(totalInfo);
//				res.setRechargeNum(prepaidManager.getDailyCharge());
//				res.setRechargeMax(activityManager.DailyRecharge_GetTodayMax());
				res.setSuperPackageInfo(spiBuilder.build());
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				Out.debug(3);
				return;

			}
		};
	}

}
