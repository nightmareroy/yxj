package com.wanniu.game.request.interact;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.interact.PlayerInteract;

import pomelo.area.InteractHandler.InteractConfigResponse;

@GClientEvent("area.interactHandler.interactConfigRequest")
public class InteractConfigHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				InteractConfigResponse.Builder res = InteractConfigResponse.newBuilder();
				res.setS2CCode(OK);
				res.addAllS2CData(PlayerInteract.getConfig());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
