package com.wanniu.game.request.team;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler.GetNearTeamsResponse;
import pomelo.area.TeamHandler.NearTeam;
import pomelo.area.TeamHandler.NearTeamLeader;
import pomelo.area.TeamHandler.NearTeamMember;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.getNearTeamsRequest")
public class GetNearTeamsHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetNearTeamsResponse.Builder res = GetNearTeamsResponse.newBuilder();
				Area area = player.getArea();
				Map<String, TeamData> teams = new HashMap<>();
				for (String playerId : area.actors.keySet()) {
					WNPlayer near = PlayerUtil.getOnlinePlayer(playerId);
					if (near == null) {
						continue;
					}
					TeamData team = near.getTeamManager().getTeam();
					if (team != null && team != player.getTeamManager().getTeam()) {
						teams.put(team.id, team);
					}
				}
				for (TeamData team : teams.values()) {
					NearTeam.Builder nearTeam = NearTeam.newBuilder();
					nearTeam.setTeamId(team.id);
					PlayerPO leader = PlayerUtil.getPlayerBaseData(team.leaderId);
					NearTeamLeader.Builder ntLeader = NearTeamLeader.newBuilder();
					ntLeader.setName(leader.name);
					ntLeader.setGuildName(GuildUtil.getGuildName(leader.id));
					ntLeader.setLv(leader.level);
					ntLeader.setPro(leader.pro);
					nearTeam.setLeader(ntLeader);
					for (TeamMemberData teamMember : team.teamMembers.values()) {
						NearTeamMember.Builder ntMember = NearTeamMember.newBuilder();
						PlayerPO member = teamMember.getPlayerData();
						ntMember.setPro(member.pro);
						ntMember.setLv(member.level);
						nearTeam.addMembers(ntMember);
					}
					nearTeam.setApply(team.applies.containsKey(player.getId()) ? 1 : 0);
					res.addTeams(nearTeam);
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x305;
	}
	
}
