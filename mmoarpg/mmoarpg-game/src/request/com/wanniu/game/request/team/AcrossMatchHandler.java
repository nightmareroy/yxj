package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.AcrossMatchRequest;
import pomelo.area.TeamHandler.AcrossMatchResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.acrossMatchRequest")
public class AcrossMatchHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		AcrossMatchRequest target = AcrossMatchRequest.parseFrom(pak.getRemaingBytes());
		int targetId = target.getTargetId();
		int difficulty = target.getDifficulty();
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			if (!player.getTeamManager().isTeamLeader()) {
				return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
			}
			team.curTargetId = targetId;
			team.curDifficulty = difficulty;
		} else {
			player.getTeamManager().acrossTargetId = targetId;
			player.getTeamManager().acrossDifficulty = difficulty;
		}

		TeamUtil.pushAcrossMatch(player);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AcrossMatchResponse.Builder res = AcrossMatchResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
