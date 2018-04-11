package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.OpenChangeResponse;
@GClientEvent("area.activityHandler.openChangeRequest")
public class OpenChangeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		OpenChangeRequest req = OpenChangeRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				OpenChangeResponse.Builder res = OpenChangeResponse.newBuilder();
				pomelo.area.ActivityHandler.OpenChangeResponse.Builder changeInfo = player.activityManager.haoliChange();
				changeInfo.setS2CCode(OK);
				body.writeBytes(changeInfo.build().toByteArray());
				
			}
		};
	}

}
