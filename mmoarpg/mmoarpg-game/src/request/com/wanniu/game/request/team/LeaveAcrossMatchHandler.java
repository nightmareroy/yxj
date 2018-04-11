package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.LeaveAcrossMatchResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.leaveAcrossMatchRequest")
public class LeaveAcrossMatchHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		TeamUtil.removeAcrossMatch(player);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				LeaveAcrossMatchResponse.Builder res = LeaveAcrossMatchResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
