package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.GetBenifitableRequest;
import pomelo.area.FightLevelHandler.GetBenifitableResponse;


@GClientEvent("area.fightLevelHandler.getBenifitableRequest")
public class GetBenifitableHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		GetBenifitableRequest req = GetBenifitableRequest.parseFrom(pak.getRemaingBytes());

		Area area = player.getArea();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBenifitableResponse.Builder res = GetBenifitableResponse.newBuilder();

//				FightLevelManager fightLevelManager = player.fightLevelManager;
				

				
				
				

				res.setS2CCode(OK);
				res.setBenifitable(area.getActor(player.getId()).profitable);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}