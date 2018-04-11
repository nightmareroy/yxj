package com.wanniu.game.request.rich;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rich.RichManager;


import pomelo.rich.RichHandler.FetchTurnAwardRequest;
import pomelo.rich.RichHandler.FetchTurnAwardResponse;

/**
 * 领取大富翁轮回奖励
 * 
 * @author liyue
 */
@GClientEvent("rich.richHandler.fetchTurnAwardRequest")
public class FetchTurnAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		FetchTurnAwardRequest req=FetchTurnAwardRequest.parseFrom(pak.getRemaingBytes());
		int turnId=req.getId();
		
		FetchTurnAwardResponse.Builder res=player.richManager.fetchTurnAward(player.getId(),turnId);
		
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
