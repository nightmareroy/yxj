package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.GetTeamMembersResponse;

@GClientEvent("area.teamHandler.getTeamMembersRequest")
public class GetTeamMembersHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				GetTeamMembersResponse.Builder res = GetTeamMembersResponse.newBuilder();
				TeamMemberData teamMember = player.getTeamManager().getTeamMember();
				int isAutoTeam = player.getTeamManager().getIsAutoTeam();
				res.setS2CCode(OK);
				res.setS2CIsAcceptAutoTeam(isAutoTeam);
				if (teamMember != null) {
					TeamData team = player.getTeamManager().getTeam();
					res.setS2CTeamTarget(TeamUtil.generateTeamTargetData(team));
					res.addAllS2CTeamMembers(TeamUtil.generateTeamMemberDetailData(player, team));
					res.setS2CIsAcceptAutoTeam(team.isAutoTeam ? 1 : 0);
					res.setFollow(teamMember.getFollow());
					res.setHaveApply(team.applies.size());
				} else {
					Out.warn(player.getName(), " not in team request!");
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x307;
	}
	
}
