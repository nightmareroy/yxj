package com.wanniu.game.request.playerSkills;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SkillHandler.GetAllSkillResponse;

@GClientEvent("area.skillHandler.getAllSkillRequest")
public class GetAllSkillHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetAllSkillResponse.Builder res = player.skillManager.toJson4Payload();
				res.setS2CCode(OK);				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
