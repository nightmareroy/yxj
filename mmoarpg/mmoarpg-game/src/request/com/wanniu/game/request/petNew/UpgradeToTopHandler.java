package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.UpgradeToTopRequest;
import pomelo.area.PetNewHandler.UpgradeToTopResponse;

@GClientEvent("area.petNewHandler.upgradeToTopRequest")
public class UpgradeToTopHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			protected void write() throws IOException {

				UpgradeToTopRequest req = UpgradeToTopRequest.parseFrom(pak.getRemaingBytes());
				int id = req.getC2SId();				
				UpgradeToTopResponse.Builder res = player.petNewManager.reqUpgrade2Top(id);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
