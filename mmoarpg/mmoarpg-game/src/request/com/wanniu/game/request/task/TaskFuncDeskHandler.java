package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.TaskHandler.TaskFuncDeskRequest;
import pomelo.area.TaskHandler.TaskFuncDeskResponse;

/**
 * 
 * @author Tanglt
 *
 */
@GClientEvent("area.taskHandler.taskFuncDeskRequest")
public class TaskFuncDeskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		TaskFuncDeskRequest req = TaskFuncDeskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer)pak.getPlayer();
		String npcId = req.getC2SNcpId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TaskFuncDeskResponse.Builder res = TaskFuncDeskResponse.newBuilder();
				player.getPlayerTasks().dealTaskEvent(TaskType.FUNC_DESK, npcId, 1);
				res.setS2CCode(OK);				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
