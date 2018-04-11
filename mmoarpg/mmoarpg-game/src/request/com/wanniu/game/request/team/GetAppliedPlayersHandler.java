package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData;

import pomelo.area.TeamHandler.GetAppliedPlayersResponse;
import pomelo.area.TeamHandler.TeamMemberBasic;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.getAppliedPlayersRequest")
public class GetAppliedPlayersHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		TeamData team = player.getTeamManager().getTeam();
		if (team == null) {
			return new ErrorResponse(LangService.getValue("TEAM_NULL"));
		}
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				GetAppliedPlayersResponse.Builder res = GetAppliedPlayersResponse.newBuilder();
				for (String key : team.applies.keySet()) {
					WNPlayer member = PlayerUtil.getOnlinePlayer(key);
					if (member != null && !member.getTeamManager().isInTeam()) {
						PlayerPO po = member.getPlayer();

						TeamMemberBasic.Builder data = TeamMemberBasic.newBuilder();
						data.setId(po.id);
						data.setName(po.name);
						data.setPro(po.pro);
						data.setLevel(po.level);
						data.setUpLevel(po.upLevel);
						data.setGuildName(GuildUtil.getGuildName(po.id));

						res.addS2CPlayers(data);
					} else {
						team.removeApply(key);
					}
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

	public short getType() {
		return 0x303;
	}
	
}
