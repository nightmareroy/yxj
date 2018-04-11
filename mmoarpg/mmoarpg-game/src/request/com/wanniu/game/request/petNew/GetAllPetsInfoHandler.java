package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.GetAllPetsInfoResponse;

@GClientEvent("area.petNewHandler.getAllPetsInfoRequest")
public class GetAllPetsInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			protected void write() throws IOException {
				GetAllPetsInfoResponse.Builder res = player.petNewManager.toJson4Payload();
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
