package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.LeaveTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.leaveTeamRequest")
public class LeaveTeamHandler extends TeamRequestFilter {

	protected PomeloResponse checkRemote(WNPlayer player) {
		return null;
	}

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				LeaveTeamResponse.Builder res = LeaveTeamResponse.newBuilder();
				boolean isInTeam = player.getTeamManager().isInTeam();
			    if(isInTeam){
			        TeamUtil.leaveTeamInAreaServer(player);
			        res.setS2CCode(OK);
			    }else{
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("TEAM_LEAVE"));
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
