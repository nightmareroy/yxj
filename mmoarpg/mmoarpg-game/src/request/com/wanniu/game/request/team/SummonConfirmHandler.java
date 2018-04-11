package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;

import pomelo.area.TeamHandler.SummonConfirmRequest;
import pomelo.area.TeamHandler.SummonConfirmResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.summonConfirmRequest")
public class SummonConfirmHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		SummonConfirmRequest msg = SummonConfirmRequest.parseFrom(pak.getRemaingBytes());

		int operate = msg.getS2COperate();
		if (operate != 1 && operate != 0) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}

		String messageId = msg.getC2SId();
		TeamMemberData selfTeamMember = player.getTeamManager().getTeamMember();
		if (selfTeamMember == null || selfTeamMember.isLeader || !selfTeamMember.teamId.equals(messageId)) {
			return new ErrorResponse(LangService.getValue("EXPIRED_MSG"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SummonConfirmResponse.Builder res = SummonConfirmResponse.newBuilder();

				TeamData team = player.getTeamManager().getTeam();
				String summonResultStr = "";
				if (operate == 0) {
					summonResultStr = LangService.getValue("TEAM_SUMMON_FAIL").replace("{playerName}", player.getName());
				} else {
					if (team == null || !team.isOpenFollow()) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_LOCKED"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					WNPlayer leader = PlayerUtil.getOnlinePlayer(team.leaderId);
					Area area = leader.getArea();
					String result = AreaUtil.canEnterArea(area.prop, player);
					if (result == null) {
						result = TeamService.followLeader(player, true);
					}
					if (result != null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(result);
						body.writeBytes(res.build().toByteArray());
						return;
					}
//					JSONObject pos = PlayerUtil.getPlayerNowPosition(team.leaderId);
//					int x = 0, y = 0;
//					if (pos != null) {
//						x = pos.getIntValue("x");
//						y = pos.getIntValue("y");
//					}
//					AreaUtil.changeArea(player, new AreaData(area.areaId, x, y, leader.getInstanceId()));
					summonResultStr = LangService.getValue("TEAM_SUMMON_SUCCESS").replace("{playerName}", player.getName());
				}

				PlayerUtil.sendSysMessageToPlayer(summonResultStr, team.leaderId, Const.TipsType.BLACK);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
