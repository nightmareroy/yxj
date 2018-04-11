package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityLevelOrSwordRequest;
import pomelo.area.ActivityHandler.ActivityLevelOrSwordResponse;

@GClientEvent("area.activityHandler.activityLevelOrSwordRequest")
public class ActivityLevelOrSwordHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		ActivityLevelOrSwordRequest req = ActivityLevelOrSwordRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				ActivityLevelOrSwordResponse.Builder res = ActivityLevelOrSwordResponse.newBuilder();
				res = player.activityManager.levelOrSword(req.getC2SActivityId());
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
