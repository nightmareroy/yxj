package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.GetPetInfoNewRequest;
import pomelo.area.PetNewHandler.GetPetInfoNewResponse;

@GClientEvent("area.petNewHandler.getPetInfoNewRequest")
public class GetPetInfoNewHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			protected void write() throws IOException {
				GetPetInfoNewRequest req = GetPetInfoNewRequest.parseFrom(pak.getRemaingBytes());
				GetPetInfoNewResponse.Builder res = player.petNewManager.getPetInfo(req.getC2SPetId());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
