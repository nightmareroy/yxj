package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.CancelAutoResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.cancelAutoRequest")
public class CannelAutoHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
//		CancelAutoRequest msg = CancelAutoRequest.parseFrom(pak.getRemaingBytes());
		
		TeamData team = player.getTeamManager().getTeam();
		if (team != null && !player.getTeamManager().isTeamLeader()) {
			return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
		}
		
		TeamService.removeAutoMatch(player.getId());
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CancelAutoResponse.Builder res = CancelAutoResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
