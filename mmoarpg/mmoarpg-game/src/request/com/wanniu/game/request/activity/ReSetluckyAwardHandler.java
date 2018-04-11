package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ReSetluckyAwardResponse;
@GClientEvent("area.activityHandler.reSetluckyAwardRequest")
public class ReSetluckyAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		ReSetluckyAwardRequest req = ReSetluckyAwardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				ReSetluckyAwardResponse.Builder res = ReSetluckyAwardResponse.newBuilder();
				ReSetluckyAwardResponse.Builder data = player.activityManager.reSetluckyAward();
				if(data.getS2CCode()==OK)
				{
					res.setS2CCode(OK);
					res.addAllS2CAwards(data.getS2CAwardsList());
					body.writeBytes(res.build().toByteArray());
				}else{
					res.setS2CCode(FAIL);
					res.setS2CMsg(data.getS2CMsg());
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}

}
