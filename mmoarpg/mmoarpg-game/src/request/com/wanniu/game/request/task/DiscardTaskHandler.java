package com.wanniu.game.request.task;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;

import pomelo.area.TaskHandler.DiscardTaskRequest;
import pomelo.area.TaskHandler.DiscardTaskResponse;

/**
 * 放弃任务
 * @author Tanglt
 *
 */
@GClientEvent("area.taskHandler.discardTaskRequest")
public class DiscardTaskHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		DiscardTaskRequest req = DiscardTaskRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer)pak.getPlayer();
		int templateId = req.getC2STemplateId();
	    int kind = req.getC2SKind();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DiscardTaskResponse.Builder res = DiscardTaskResponse.newBuilder();
				// 一条龙任务只有队长才能放弃任务
				if (kind == TaskKind.LOOP) {
					TeamData team = player.getTeamManager().getTeam();
					if(team != null && !team.leaderId.equals(player.getId())) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("TEAM_NO_AUTHORITY"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

				}
				
				if(templateId > 0 && player.getPlayerTasks().discardTask(templateId, kind)){
					res.setS2CCode(OK);
			    }else{
			    	res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CANNOT_GIVEUP_QUEST"));
			    }
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
