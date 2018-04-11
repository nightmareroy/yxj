package com.wanniu.game.request.playerSkills;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.SkillHandler.UnlockSkillResponse;

@GClientEvent("area.skillHandler.unlockSkillRequest")
public class UnlockSkillHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				UnlockSkillResponse.Builder res = UnlockSkillResponse.newBuilder();

			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    	body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
