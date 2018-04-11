package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.TaskHandler.UpdateTaskStatusRequest;
import pomelo.area.TaskHandler.UpdateTaskStatusResponse;

/**
 * 更新任务状态
 * @author Tanglt
 *
 */
@GClientEvent("area.taskHandler.updateTaskStatusRequest")
public class UpdateTaskStatusHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		UpdateTaskStatusRequest req = UpdateTaskStatusRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer)pak.getPlayer();
		int templateId = req.getC2STemplateId();
	    int kind = req.getC2SKind();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpdateTaskStatusResponse.Builder res = UpdateTaskStatusResponse.newBuilder();
			    if(templateId > 0 && player.getPlayerTasks().updateStatus(templateId, kind)){
			    	res.setS2CCode(OK);
			    }else{
			    	res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}