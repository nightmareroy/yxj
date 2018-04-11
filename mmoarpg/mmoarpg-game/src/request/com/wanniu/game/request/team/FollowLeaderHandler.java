package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.FollowLeaderRequest;
import pomelo.area.TeamHandler.FollowLeaderResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.followLeaderRequest")
public class FollowLeaderHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request() throws Exception {
		return request((WNPlayer) pak.getPlayer());
	}

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		FollowLeaderRequest req = FollowLeaderRequest.parseFrom(pak.getRemaingBytes());
		boolean follow = req.getFollow() == 1;
		TeamMemberData teamMember = player.getTeamManager().getTeamMember();
		if (teamMember != null && follow) {
			TeamData team = player.getTeamManager().getTeam();
			if (team == null || team.islock()) {
				return new ErrorResponse(LangService.getValue("TEAM_LOCKED"));
			}
			WNPlayer leander = PlayerUtil.getOnlinePlayer(team.leaderId);
			if (!player.getInstanceId().equals(leander.getInstanceId()) && !leander.getArea().isOpenJoinTeam()) {
				return new ErrorResponse(LangService.getValue("TEAM_BATTLE_FOLLOW"));
			}
		}
		Out.debug("team follow : ", follow);
		String res = TeamService.followLeader(player, follow);
		if (res != null) {
			return new ErrorResponse(res);
		}
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FollowLeaderResponse.Builder res = FollowLeaderResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
