package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.GotoTeamTargetRequest;
import pomelo.area.TeamHandler.GotoTeamTargetResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.gotoTeamTargetRequest")
public class GotoTeamTargetHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		GotoTeamTargetRequest target = GotoTeamTargetRequest.parseFrom(pak.getRemaingBytes());
		int targetId = target.getTargetId();
		int difficulty = target.getDifficulty();
		TeamData team = player.getTeamManager().getTeam();
		if (team == null) {
			return new ErrorResponse(LangService.getValue("TEAM_MEMBER_COUNT"));
		}
		if (!player.getTeamManager().isTeamLeader()) {
			return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
		}
		if (!team.isAllOnline()) {
			return new ErrorResponse(LangService.getValue("TEAM_PLAYER_OFF_LINE"));
		}
		team.curTargetId = targetId;
		team.curDifficulty = difficulty;
		int mapId = team.getTargetMap();
		if (mapId == 0) {
			return new ErrorResponse(LangService.getValue("TEAM_NOTARGET_GO"));
		}
		
		Area area = TeamUtil.goToTeamTarget(team, player);
		if (area == null) {
			return new ErrorResponse("");
		}
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GotoTeamTargetResponse.Builder res = GotoTeamTargetResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
