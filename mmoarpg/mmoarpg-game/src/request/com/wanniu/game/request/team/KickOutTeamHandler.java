package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.KickOutTeamRequest;
import pomelo.area.TeamHandler.KickOutTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.kickOutTeamRequest")
public class KickOutTeamHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		KickOutTeamRequest msg = KickOutTeamRequest.parseFrom(pak.getRemaingBytes());
		String playerId = msg.getC2SPlayerId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				KickOutTeamResponse.Builder res = KickOutTeamResponse.newBuilder();
				TeamData team = player.getTeamManager().getTeam();
				TeamMemberData teamLeader = player.getTeamManager().getTeamMember();
				if (team == null || teamLeader == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_LEAVE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if (!teamLeader.isLeader) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_NO_AUTHORITY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				TeamMemberData teamMember = team.getMember(playerId);
				if (teamMember == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_OBJ_LEAVE_TEAM"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if (!teamLeader.teamId.equals(teamMember.teamId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_WE_NOT_ONE_TEAM"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (TeamService.kickOutTeam(team, teamMember)) {
					PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_KICK"), playerId, null);
					PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_TARGET_KICK").replace("{playerName}", teamMember.getPlayerData().name),
							player.getId(), null);
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
