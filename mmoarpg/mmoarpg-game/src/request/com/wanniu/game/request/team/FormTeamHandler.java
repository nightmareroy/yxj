package com.wanniu.game.request.team;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.MessageData.MessageData_Team_apply;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.FormTeamRequest;
import pomelo.area.TeamHandler.FormTeamResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.formTeamRequest")
public class FormTeamHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		FormTeamRequest msg = FormTeamRequest.parseFrom(pak.getRemaingBytes());
		String toPlayerId = msg.getC2SPlayerId();
		if (toPlayerId == null) {
			return new ErrorResponse(LangService.getValue("SOMETHING_ERR"));
		}
		if (!PlayerUtil.isOnline(toPlayerId)) {
			return new ErrorResponse(LangService.getValue("TEAM_OFF_LINE"));
		}
		if (player.getFriendManager().isInBlackList(toPlayerId)) {
			return new ErrorResponse(LangService.getValue("FRIEND_TARGET_IN_BLACK_LIST"));
		}
		WNPlayer toPlayer = PlayerUtil.findPlayer(toPlayerId);
		if (toPlayer.getFriendManager().isInBlackList(player.getId())) {
			return new ErrorResponse(LangService.getValue("FRIEND_IN_BLACK_LIST"));
		}
		if (toPlayer.getLevel() < GlobalConfig.Team_Min_Level) {
			return new ErrorResponse(LangService.getValue("TEAM_OTHER_LV_LESS"));
		}

		if (!TeamUtil.isValidOfMap(player.getSceneType())) {
			return new ErrorResponse(LangService.getValue("TEAM_IN_RAID"));
		}

		int toAreaType = AreaUtil.getAreaType(toPlayer.getAreaId());
		if (!TeamUtil.isValidOfMap(toAreaType)) {
			return new ErrorResponse(LangService.getValue("TEAM_MAPTPYE_TARGET_DISABLE"));
		}

		if (player.getSceneType() == Const.SCENE_TYPE.CROSS_SERVER.getValue() && toAreaType != Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
			return new ErrorResponse(LangService.getValue("CROSS_SERVER_AUTH_LIMIT_TEAM_FORM2"));
		} else if (player.getSceneType() != Const.SCENE_TYPE.CROSS_SERVER.getValue() && toAreaType == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
			return new ErrorResponse(LangService.getValue("CROSS_SERVER_AUTH_LIMIT_TEAM_FORM1"));
		}

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				FormTeamResponse.Builder res = FormTeamResponse.newBuilder();
				res.setS2CCode(OK);

				TeamMemberData selfTeamMember = player.getTeamManager().getTeamMember();
				TeamMemberData targetTeamMember = toPlayer.getTeamManager().getTeamMember();

				if (selfTeamMember != null) { // 邀请
					if (targetTeamMember != null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_TARGET_IN_TEAM"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					TeamData team = player.getTeamManager().getTeam();
					String result = TeamUtil.canJoinTeam(player, team);
					if (result != null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(result);
						body.writeBytes(res.build().toByteArray());
						return;
					}
					if (team.invites.containsKey(toPlayerId)) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_INVITATION"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					int sendLimit = MessageUtil.getSendLimit(Const.MESSAGE_TYPE.team_invite.getValue());
					if (sendLimit > 0 && team.invites.size() >= sendLimit) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_THIS_MSG_SEND_TOO_MUCH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					team.invite(player, toPlayerId);
					PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_INVITATION"), player.getId());
				} else if (targetTeamMember != null) { // 申请
					String teamId = targetTeamMember.teamId;
					TeamData team = TeamService.getTeam(teamId);

					String result = TeamUtil.canJoinTeam(player, team);
					if (result != null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(result);
						body.writeBytes(res.build().toByteArray());
						return;
					}

					if (team.applies.containsKey(player.getId())) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_ALREADY_INVITATION"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					Map<String, String> strMsg = new HashMap<>(1);
					strMsg.put("playerName", PlayerUtil.getColorPlayerNameByPro(player.getPro(), player.getName()));
					MessageData_Team_apply msgData = new MessageData_Team_apply();
					msgData.applyId = player.getId();
					msgData.name = player.getName();
					msgData.teamId = teamId;
					MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.team_apply.getValue(), player.getId(), msgData, strMsg);
					message.id = player.getId();
					MessageUtil.sendMessageToPlayer(message, team.leaderId);
					team.addApply(player.getId(), message.validTime);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_NULL"));
				}

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
