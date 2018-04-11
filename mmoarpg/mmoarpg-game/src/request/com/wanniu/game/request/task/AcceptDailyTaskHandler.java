package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.task.AcceptTaskHandler.AcceptTaskResult;
import com.wanniu.game.task.TaskUtils;

import pomelo.area.TaskHandler.AcceptDailyTaskRequest;
import pomelo.area.TaskHandler.AcceptDailyTaskResponse;

/**
 * 接受师门任务
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.taskHandler.acceptDailyTaskRequest")
public class AcceptDailyTaskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		AcceptDailyTaskRequest req = AcceptDailyTaskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		String npcId = req.getC2SNpcId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AcceptDailyTaskResponse.Builder res = AcceptDailyTaskResponse.newBuilder();
				if (!player.canTalkWithNpc(0, Integer.parseInt(npcId))) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TASK_OBJ_FAR_AWAY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (!player.functionOpenManager.isOpen(Const.FunctionType.teacher.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if (player.taskManager.dailyTasks.size() > 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DAILY_HAS_ACCEPTED"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				// 当日师门任务已达上限
				if (player.taskManager.taskListPO != null
						&& player.taskManager.taskListPO.todayDailyTaskCount >= GlobalConfig.Daily_completeMax) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DAILY_TODAY_MAX"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 随机获取一条日常任务
				TaskBase prop = TaskUtils.getRDDailyTask(player.getLevel(), 0, player.getPro());
				AcceptTaskResult result = player.getPlayerTasks().acceptTask(prop.iD, TaskKind.DAILY);
				if (result.task != null) {
					result.task.setSecProgress(player.taskManager.getSecProgress(result.task.getKind()));
					player.getPlayerTasks().pushTaskUpdate(result.task);
					res.setS2CCode(OK);
					
					// 完成接取师门任务
					player.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY_LOOP, String.valueOf(TaskKind.DAILY), 1);
					player.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY, String.valueOf(TaskKind.DAILY), 1);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(result.msg);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
