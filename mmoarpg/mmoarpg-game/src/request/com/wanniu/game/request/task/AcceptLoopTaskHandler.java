package com.wanniu.game.request.task;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.task.AcceptTaskHandler.AcceptTaskResult;
import com.wanniu.game.task.TaskData;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TaskHandler.AcceptLoopTaskRequest;
import pomelo.area.TaskHandler.AcceptLoopTaskResponse;

/**
 * 接受一条龙任务
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.taskHandler.acceptLoopTaskRequest")
public class AcceptLoopTaskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		AcceptLoopTaskRequest req = AcceptLoopTaskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		String npcId = req.getC2SNpcId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AcceptLoopTaskResponse.Builder res = AcceptLoopTaskResponse.newBuilder();
				if (!player.canTalkWithNpc(0, Integer.parseInt(npcId))) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TASK_OBJ_FAR_AWAY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 判断所有队员都超过25级
				int openLevel = FunctionOpenUtil.getPropByName(Const.FunctionType.LoopTask.getValue()).openLv;
				if (player.getLevel() < openLevel) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("AREA_PLAYER_LEVEL_LIMIT").replace("{playerLevel}", openLevel + ""));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				// 没有队伍
				TeamData team = player.getTeamManager().getTeam();
				if (team == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(String.format(LangService.getValue("TEAM_NEED_MEMBER"), Const.LOOP_TASK_TEAM_MEMBER_COUNT));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 只有队长才能接任务
				if (!team.leaderId.equals(player.getId())) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TEAM_NO_AUTHORITY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 判断是否在三人以上队伍中
				if (team.memberCount() < Const.LOOP_TASK_TEAM_MEMBER_COUNT) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(String.format(LangService.getValue("TEAM_NEED_MEMBER"), Const.LOOP_TASK_TEAM_MEMBER_COUNT));
					body.writeBytes(res.build().toByteArray());
					return;
				}


				if (team.isInLoopTask()) {
					//客户端此时有可能没有收到push消息，造成状态与服务器不一致，此时再主动推送一次 (对应禅道3494bug)
					Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
					StringBuilder sb = new StringBuilder(player.getId()).append(" in acceptLoopTaskRequest||");
					if (loopTasks != null) {						
						for (TaskPO db : loopTasks.values()) {
							player.taskManager.pushTaskUpdate(new TaskData(db));
							sb.append("loopTask:").append(db.templateId).append(",").append(db.state).append(",").append(db.progress).append(",");
						}
					}
					Out.info(sb.toString());
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TASK_IS_ACCPETED"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 默认给第一个一条龙任务
				TaskBase prop = TaskUtils.getFirstLoopTask();

				AcceptTaskResult result = player.getPlayerTasks().acceptTask(prop.iD, prop.kind);
				if (result.task != null) {
					result.task.setSecProgress(player.taskManager.getSecProgress(result.task.getKind()));
					// 给队员推送消息
					for (TeamMemberData member : team.teamMembers.values()) {
						WNPlayer mPlayer = member.getPlayer();
						if (mPlayer == null)
							continue;
						mPlayer.getPlayerTasks().pushTaskUpdate(result.task);
						
						// 完成接取皓月镜任务
						mPlayer.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY_LOOP, String.valueOf(TaskKind.LOOP), 1);
					}
				} else {
					Out.error("accept loop task failed");
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CANNOT_ACCEPT_QUEST"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				res.setS2CCode(OK);

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
