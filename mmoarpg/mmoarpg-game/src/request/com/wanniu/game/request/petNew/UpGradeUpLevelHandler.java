package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.UpGradeUpLevelRequest;
import pomelo.area.PetNewHandler.UpGradeUpLevelResponse;

@GClientEvent("area.petNewHandler.upGradeUpLevelRequest")
public class UpGradeUpLevelHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpGradeUpLevelRequest req = UpGradeUpLevelRequest.parseFrom(pak.getRemaingBytes());
				int id= req.getC2SId();				
				UpGradeUpLevelResponse.Builder res = player.petNewManager.upgradeUplevel(id);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
