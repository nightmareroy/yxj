package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.PayFirstResponse.Builder;
import pomelo.area.ActivityHandler.PaySecondResponse;
@GClientEvent("area.activityHandler.paySecondRequest")
public class PaySecondHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		PaySecondRequest req = PaySecondRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				PaySecondResponse.Builder res = PaySecondResponse.newBuilder();
				
				Builder data = player.activityManager.paySecond();
				data.setS2CCode(OK);
				body.writeBytes(data.build().toByteArray());
			}
		};
	}

}
