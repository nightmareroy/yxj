package com.wanniu.game.request.activity;


import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityLuckyAwardViewResponse;
@GClientEvent("area.activityHandler.activityLuckyAwardViewRequest")
public class ActivityLuckyAwardViewHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
//		ActivityLuckyAwardViewRequest req = ActivityLuckyAwardViewRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				ActivityLuckyAwardViewResponse.Builder data = player.activityManager.activityLuckyAwardView();
				data.setS2CCode(OK);
				body.writeBytes(data.build().toByteArray());
			}
		};
	}

}

