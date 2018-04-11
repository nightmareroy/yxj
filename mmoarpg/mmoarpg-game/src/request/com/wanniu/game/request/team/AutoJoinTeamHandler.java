package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TeamTargetCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.AutoJoinTeamRequest;
import pomelo.area.TeamHandler.AutoJoinTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.autoJoinTeamRequest")
public class AutoJoinTeamHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		AutoJoinTeamRequest msg = AutoJoinTeamRequest.parseFrom(pak.getRemaingBytes());
		
		int targetId = msg.getC2STargetId();
		
		if(targetId == 0) {
			return new ErrorResponse(LangService.getValue("TEAM_TARGET_ERROR"));
		}

		if (player.getTeamManager().isInTeam()) {
			return new ErrorResponse(LangService.getValue("TEAM_ALREADY_IN_TEAM"));
		}

		int difficulty = msg.getC2SDifficulty();
		TeamTargetCO targetProp = GameData.TeamTargets.get(targetId);
		// 参数检测
		if ((targetProp == null) || (difficulty != 1 && difficulty != 2 && difficulty != 3)) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}
		
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				AutoJoinTeamResponse.Builder res = AutoJoinTeamResponse.newBuilder();

				TeamService.addAutoMatch(player, targetId, difficulty);
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
