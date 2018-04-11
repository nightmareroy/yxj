package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler.OnSummonTeamPush;
import pomelo.area.TeamHandler.SummonRequest;
import pomelo.area.TeamHandler.SummonResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.summonRequest")
public class SummonHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		TeamData team = player.getTeamManager().getTeam();
		if (team == null) {
			return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
		}

		SummonRequest msg = SummonRequest.parseFrom(pak.getRemaingBytes());
		String targetId = msg.getC2STeamMemberId();

		Area area = player.getArea();
		String content = LangService.getValue("TEAM_SUMMON_MESSAGE").replace("{1}", area.getSceneName()).replace("{2}", String.valueOf(area.lineIndex));
		OnSummonTeamPush.Builder data = OnSummonTeamPush.newBuilder();
		data.setS2CId(team.id);
		data.setS2CContent(content);
		String targetNmae = null;
		MessagePush push = new MessagePush("area.teamPush.onSummonTeamPush", data.build());
		if (StringUtil.isNotEmpty(targetId)) {
			if (!PlayerUtil.isOnline(targetId)) {
				return new ErrorResponse(LangService.getValue("PLAYER_NOT_ONLINE"));
			}
			WNPlayer other = PlayerUtil.getOnlinePlayer(targetId);
			if (!other.getArea().isNormal()) {
				return new ErrorResponse(LangService.getValue("TEAM_TARGET_IN_RAID"));
			}
			TeamMemberData selfTeamMember = player.getTeamManager().getTeamMember();
			TeamMemberData targetTeamMember = other.getTeamManager().getTeamMember();

			if (selfTeamMember == null || !selfTeamMember.isLeader || targetTeamMember == null
					|| !selfTeamMember.teamId.equals(targetTeamMember.teamId)) {
				return new ErrorResponse(LangService.getValue("TEAM_SUMMON_NOT_LEADER"));
			}

			if (player.getInstanceId().equals(other.getInstanceId()) && targetTeamMember.follow) {
				return new ErrorResponse(LangService.getValue("TEAM_SUMMON_FAIL_TARGET_INSTANCE"));
			}
			other.receive(push);
			targetNmae = other.getName();
		} else {
			targetNmae = LangService.getValue("TEAM_MEMBER");
			for (TeamMemberData member : team.teamMembers.values()) {
				if (!member.follow && !member.isLeader) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer != null) {
						mPlayer.receive(push);
					}
				}
			}
		}

		PlayerUtil.sendSysMessageToPlayer(LangService.format("TEAM_SUMMON_SEND_MESSAGE", targetNmae), player.getId(), Const.TipsType.BLACK);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SummonResponse.Builder res = SummonResponse.newBuilder();

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
