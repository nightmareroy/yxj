package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.UpgradeOneLevelRequest;
import pomelo.area.PetNewHandler.UpgradeOneLevelResponse;

@GClientEvent("area.petNewHandler.upgradeOneLevelRequest")
public class UpgradeOneLevelHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			protected void write() throws IOException {
				UpgradeOneLevelRequest req = UpgradeOneLevelRequest.parseFrom(pak.getRemaingBytes());
				int id = req.getC2SId();
				UpgradeOneLevelResponse.Builder res= player.petNewManager.reqUpgradeOneLevel(id);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
