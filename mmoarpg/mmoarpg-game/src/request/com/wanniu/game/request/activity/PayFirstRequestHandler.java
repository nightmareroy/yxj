package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.PayFirstResponse;
import pomelo.area.ActivityHandler.PayFirstResponse.Builder;
@GClientEvent("area.activityHandler.payFirstRequest")
public class PayFirstRequestHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		PayFirstRequest req = PayFirstRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				PayFirstResponse.Builder res = PayFirstResponse.newBuilder();
				Builder data = player.activityManager.payFirst();
				data.setS2CCode(OK);
				body.writeBytes(data.build().toByteArray());
			}
		};
	}

}
