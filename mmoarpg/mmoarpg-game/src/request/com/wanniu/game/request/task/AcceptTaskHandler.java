package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.TaskData;
import com.wanniu.game.task.TaskUtils;

import pomelo.area.TaskHandler.AcceptTaskRequest;
import pomelo.area.TaskHandler.AcceptTaskResponse;

/**
 * 接受任务
 * 
 * @author agui
 *
 */
@GClientEvent("area.taskHandler.acceptTaskRequest")
public class AcceptTaskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		AcceptTaskRequest req = AcceptTaskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		int templateId = req.getC2STemplateId();
		int kind = req.getC2SKind();
		String npcId = req.getC2SNpcId();
		TaskBase prop = TaskUtils.getTaskProp(templateId);
		if (prop == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}
		if (prop.getType() == Const.TaskType.INTERACT_NPC.getValue() && player != null && !player.canTalkWithNpc(templateId, Integer.parseInt(npcId))) {
			return new ErrorResponse(LangService.getValue("TASK_OBJ_FAR_AWAY"));
		}

		if (prop.kind == TaskKind.DAILY) {
			// 当日师门任务已达上限
			if (player.taskManager.taskListPO != null && player.taskManager.taskListPO.todayDailyTaskCount >= GlobalConfig.Daily_completeMax) {
				Out.warn("当日师门任务已达上限，playerId=", player.getId(), ", templateId=", templateId, ",kind=", kind);
				return new ErrorResponse("系统异常，请联系客服处理");
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AcceptTaskResponse.Builder res = AcceptTaskResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					PlayerUtil.logWarnIfPlayerNull(pak);
					return;
				}
				AcceptTaskResult result = player.getPlayerTasks().acceptTask(templateId, kind);
				if (result.task != null) {
					player.getPlayerTasks().pushTaskUpdate(result.task);
					res.setS2CCode(OK);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(result.msg);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public static class AcceptTaskResult {
		public TaskData task;
		public String msg;

		public AcceptTaskResult() {

		}

		public AcceptTaskResult(TaskData task, String msg) {
			this.task = task;
			this.msg = msg;
		}
	}

}
