package com.wanniu.game.request.interact;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.InteractHandler.InteractTimesResponse;

@GClientEvent("area.interactHandler.interactTimesRequest")
public class InteractTimesHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				InteractTimesResponse.Builder data = player.getInteractManager().interactTimes(player);
				data.setS2CCode(OK);
				body.writeBytes(data.build().toByteArray());
			}
		};
	}

}
