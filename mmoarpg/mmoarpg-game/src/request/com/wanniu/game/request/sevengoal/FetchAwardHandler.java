package com.wanniu.game.request.sevengoal;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.sevengoal.SevenGoalHandler.FetchAwardRequest;
import pomelo.sevengoal.SevenGoalHandler.FetchAwardResponse;
import pomelo.sevengoal.SevenGoalHandler.GetSevenGoalRequest;
import pomelo.sevengoal.SevenGoalHandler.GetSevenGoalResponse;

/**
 * 领奖励
 * 
 * @author liyue
 */
@GClientEvent("sevengoal.sevenGoalHandler.fetchAwardRequest")
public class FetchAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		FetchAwardRequest request = FetchAwardRequest.parseFrom(pak.getRemaingBytes());
		int dayId=request.getId();
		FetchAwardResponse.Builder res=player.sevenGoalManager.fetchAward(dayId);
		
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
