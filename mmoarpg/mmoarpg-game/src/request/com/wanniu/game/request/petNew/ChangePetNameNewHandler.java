package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.ChangePetNameNewRequest;
import pomelo.area.PetNewHandler.ChangePetNameNewResponse;

@GClientEvent("area.petNewHandler.changePetNameNewRequest")
public class ChangePetNameNewHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			protected void write() throws IOException {
				ChangePetNameNewRequest req = ChangePetNameNewRequest.parseFrom(pak.getRemaingBytes());
				int id = req.getC2SId();
				String name = req.getC2SName();
				ChangePetNameNewResponse.Builder res = player.petNewManager.changePetName(id, name);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
