package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.DrawDailyRewardRequest;
import pomelo.area.SoloHandler.DrawDailyRewardResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.drawDailyRewardRequest")
public class DrawDailyRewardHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				DrawDailyRewardResponse.Builder res = DrawDailyRewardResponse.newBuilder();
				DrawDailyRewardRequest req = DrawDailyRewardRequest.parseFrom(pak.getRemaingBytes());
				int index = req.getC2SIndex();
			    player.soloManager.handleDrawDailyReward(index,res);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}