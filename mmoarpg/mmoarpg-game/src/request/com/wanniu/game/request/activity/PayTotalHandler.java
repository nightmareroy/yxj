package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.PayTotalResponse;

@GClientEvent("area.activityHandler.payTotalRequest")
public class PayTotalHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		// PayTotalRequest req =
		// PayTotalRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				PayTotalResponse.Builder res = PayTotalResponse.newBuilder();

				pomelo.area.ActivityHandler.totalInfo.Builder data = player.activityManager.payTotal();
				res.setS2CCode(OK);
				res.setS2CData(data.build());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
