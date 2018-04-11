package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.SetAutoAcceptTeamRequest;
import pomelo.area.TeamHandler.SetAutoAcceptTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.setAutoAcceptTeamRequest")
public class SetAutoAcceptTeamHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		SetAutoAcceptTeamRequest msg = SetAutoAcceptTeamRequest.parseFrom(pak.getRemaingBytes());
		int isAccept = msg.getC2SIsAccept();
		if (isAccept != 1 && isAccept != 0) {
			return new ErrorResponse(LangService.getValue("SOMETHING_ERR"));
		}
		Out.debug("isAccept:::", isAccept);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SetAutoAcceptTeamResponse.Builder res = SetAutoAcceptTeamResponse.newBuilder();
					res.setS2CCode(OK);
				TeamMemberData teamLeader = player.getTeamManager().getTeamMember();
				if (teamLeader != null && teamLeader.isLeader) {
					TeamData team = player.getTeamManager().getTeam();
					if (team != null) {
						team.setAutoTeam(isAccept == 1);
					}
					TeamService.refreshTeam(team, false);
				} else {
					player.setIsAcceptAutoTeam(isAccept);
				}
				res.setS2CIsAcceptAutoTeam(player.getTeamManager().getIsAutoTeam());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
