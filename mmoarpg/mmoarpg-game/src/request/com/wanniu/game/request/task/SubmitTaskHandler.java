package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.TaskData;
import com.wanniu.game.team.TeamData;

import pomelo.area.TaskHandler.SubmitTaskRequest;
import pomelo.area.TaskHandler.SubmitTaskResponse;

/**
 * 提交任务
 * 
 * @author Tanglt
 *
 */
@GClientEvent("area.taskHandler.submitTaskRequest")
public class SubmitTaskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		SubmitTaskRequest req = SubmitTaskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		int templateId = req.getC2STemplateId();
		if (templateId == 0) {
			Out.error("submitTaskRequest:", player.getName(), "--", templateId);
			return new ErrorResponse(LangService.getValue("CANNOT_COMPLETE_QUEST"));
		}
		int kind = req.getC2SKind();
		TaskData task = player.getPlayerTasks().getTask(templateId, kind);
		if (task == null) {
//			return new ErrorResponse(GWorld.DEBUG ? "重复提交任务:" + templateId : "");
			return new ErrorResponse("");
		}
		
		String npcId = req.getC2SNpcId();
		if (!player.canTalkWithNpc(templateId, Integer.parseInt(npcId))) {
			return new ErrorResponse(LangService.getValue("TASK_OBJ_FAR_AWAY"));
		}

		int isDouble = req.getC2SDouble();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SubmitTaskResponse.Builder res = SubmitTaskResponse.newBuilder();
				int code = 0;
				// 一条龙任务只有队长才能提交任务
				if (kind == TaskKind.LOOP) {
					TeamData team = player.getTeamManager().getTeam();
					if(team == null || !team.leaderId.equals(player.getId())) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_NO_AUTHORITY"));
						body.writeBytes(res.build().toByteArray());
						return;
					} 
//					else {
//						for (TeamMemberData member : team.teamMembers.values()) {
//							WNPlayer mPlayer = member.getPlayer();
//							if(mPlayer != null) {
//								code = mPlayer.taskManager.submitTask(templateId, kind, isDouble);
//							}
//						}
//					}
				}
//				else {
					code = player.getPlayerTasks().submitTask(templateId, kind, isDouble);
//				}
				
				if (code == 1) {
					res.setS2CCode(OK);
				} else if (code == -2) {
					res.setS2CCode(FAIL);
				} else {
					res.setS2CCode(FAIL);
//					res.setS2CMsg(LangService.getValue("CANNOT_COMPLETE_QUEST"));
				}

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
