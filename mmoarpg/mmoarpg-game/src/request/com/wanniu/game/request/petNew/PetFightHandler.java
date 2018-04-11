package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.ChangePetNameNewResponse;
import pomelo.area.PetNewHandler.PetFightRequest;

@GClientEvent("area.petNewHandler.petFightRequest")
public class PetFightHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		return new PomeloResponse() {
			protected void write() throws IOException {
				PetFightRequest req = PetFightRequest.parseFrom(pak.getRemaingBytes());
				int id = req.getC2SId();
				int type = req.getC2SType();
				ChangePetNameNewResponse.Builder res = ChangePetNameNewResponse.newBuilder();
				
				String msg = player.petNewManager.petOutFight(id, type);
				if(msg!=null)
				{
					res.setS2CMsg(msg);
					res.setS2CCode(FAIL);
				}else{
					res.setS2CCode(OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
