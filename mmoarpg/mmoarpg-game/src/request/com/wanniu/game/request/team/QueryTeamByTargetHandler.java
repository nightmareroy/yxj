package com.wanniu.game.request.team;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TeamTargetCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamService;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.QueryTeamByTargetRequest;
import pomelo.area.TeamHandler.QueryTeamByTargetResponse;
import pomelo.area.TeamHandler.Team;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.queryTeamByTargetRequest")
public class QueryTeamByTargetHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		QueryTeamByTargetRequest msg = QueryTeamByTargetRequest.parseFrom(pak.getRemaingBytes());
		int targetId = msg.getC2STargetId();
		int difficulty = msg.getC2SDifficulty();
		Out.debug("targetId - ", targetId);

		TeamTargetCO targetProp = GameData.TeamTargets.get(targetId);
		// 参数检测
		if ((targetProp == null) || (difficulty != 1 && difficulty != 2 && difficulty != 3)) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		List<TeamData> finalTeams = TeamService.queryTeamByTarget(team -> targetId <= 1 ? true : (team.targetId == targetId && team.difficulty == difficulty));
		if (targetId <= 1) {
			Collections.sort(finalTeams, new Comparator<TeamData>() {
				@Override
				public int compare(TeamData o1, TeamData o2) {
					if (o1.targetId == o2.targetId) {
						return 0;
					}
					return o1.targetId > o2.targetId ? 1 : -1;
				}
			});
		}

		QueryTeamByTargetResponse.Builder res = QueryTeamByTargetResponse.newBuilder();
		Out.debug("queryTeamByTargetRequest finalTeams:", finalTeams.size());
		res.setS2CCode(OK);
		for (TeamData team : finalTeams) {
			Team.Builder tm = team.createTeamProto();
			if (tm != null) {
				if (TeamUtil.isHasAppliedTeam(player, team)) {
					tm.setIsApplied(1);
				} else {
					tm.setIsApplied(0);
				}
				tm.setIsFighting(!team.isOpenJoin());
				res.addS2CTeams(tm.build());
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x309;
	}
}