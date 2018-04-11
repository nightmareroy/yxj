package com.wanniu.game.request.sevengoal;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;


import pomelo.sevengoal.SevenGoalHandler.GetSevenGoalRequest;
import pomelo.sevengoal.SevenGoalHandler.GetSevenGoalResponse;

/**
 * 获取七日目标详情
 * 
 * @author liyue
 */
@GClientEvent("sevengoal.sevenGoalHandler.getSevenGoalRequest")
public class GetSevenGoalHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetSevenGoalResponse.Builder res=player.sevenGoalManager.getSevenGoal();
		
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
