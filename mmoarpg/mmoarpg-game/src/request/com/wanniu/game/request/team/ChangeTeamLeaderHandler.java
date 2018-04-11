package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.ChangeTeamLeaderRequest;
import pomelo.area.TeamHandler.ChangeTeamLeaderResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.changeTeamLeaderRequest")
public class ChangeTeamLeaderHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		ChangeTeamLeaderRequest msg = ChangeTeamLeaderRequest.parseFrom(pak.getRemaingBytes());
		String playerId = msg.getC2SPlayerId();
		if (StringUtil.isEmpty(playerId)) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}
		if (!PlayerUtil.isOnline(playerId)) {
			return new ErrorResponse(LangService.getValue("TEAM_OFF_LINE"));
		}
		TeamMemberData teamLeader = player.getTeamManager().getTeamMember();
	    if(teamLeader == null){
	        return new ErrorResponse(LangService.getValue("TEAM_LEAVE"));
	    }
	    if(!teamLeader.isLeader){
	        return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
		}
		TeamData team = player.getTeamManager().getTeam();
		TeamMemberData teamMember = team.getMember(playerId);
		if (teamMember == null) {
			return new ErrorResponse(LangService.getValue("TEAM_OBJ_LEAVE_TEAM"));
		}
		if (!teamLeader.teamId.equals(teamMember.teamId)) {
			return new ErrorResponse(LangService.getValue("TEAM_WE_NOT_ONE_TEAM"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeTeamLeaderResponse.Builder res = ChangeTeamLeaderResponse.newBuilder();
				
				teamLeader.isLeader = false;
				teamMember.isLeader = true;
				team.leaderId = teamMember.id;

				TeamService.refreshTeam(team);
				TeamUtil.sendSysMessageByLeaderChanged(teamLeader.teamId, player.getName());
			    
			    res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
