package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ConsumeTotalResponse;

@GClientEvent("area.activityHandler.consumeTotalRequest")
public class ConsumeTotalHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		// ConsumeTotalRequest req = ConsumeTotalRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				ConsumeTotalResponse.Builder res = ConsumeTotalResponse.newBuilder();

				pomelo.area.ActivityHandler.totalInfo.Builder data = player.activityManager.consumeTotal();
				res.setS2CCode(OK);
				res.setS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
