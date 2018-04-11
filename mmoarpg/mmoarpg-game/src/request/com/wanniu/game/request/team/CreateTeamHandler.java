package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.CreateTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.createTeamRequest")
public class CreateTeamHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {

		if (!TeamUtil.isValidOfMap(player.getSceneType())) {
			return new ErrorResponse(LangService.getValue("TEAM_IN_RAID"));
		}
		if (player.getTeamManager().isInTeam()) {
			return new ErrorResponse(LangService.getValue("TEAM_ALREADY_IN_TEAM"));
		}

		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				CreateTeamResponse.Builder res = CreateTeamResponse.newBuilder();

				String result = null;
				if (player.getSceneType() == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
					result = TeamUtil.createAcrossTeam(player.getId(), null);
				} else {
					result = TeamUtil.createLocaleTeam(player.getId(), null);
				}

				if (result == null) {
					res.setS2CCode(OK);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(result);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
