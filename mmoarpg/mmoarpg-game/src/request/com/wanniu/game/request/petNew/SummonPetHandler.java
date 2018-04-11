package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.SummonPetRequest;
import pomelo.area.PetNewHandler.SummonPetResponse;

@GClientEvent("area.petNewHandler.summonPetRequest")
public class SummonPetHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SummonPetRequest req = SummonPetRequest.parseFrom(pak.getRemaingBytes());
				int id= req.getC2SId();				
				SummonPetResponse.Builder res = player.petNewManager.summonPet(id);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
