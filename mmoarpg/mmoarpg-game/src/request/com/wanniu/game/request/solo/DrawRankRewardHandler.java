package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.DrawRankRewardRequest;
import pomelo.area.SoloHandler.DrawRankRewardResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.drawRankRewardRequest")
public class DrawRankRewardHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		DrawRankRewardRequest req = DrawRankRewardRequest.parseFrom(pak.getRemaingBytes());
		int rankId = req.getC2SRankId();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				DrawRankRewardResponse.Builder res = DrawRankRewardResponse.newBuilder();
			    player.soloManager.handleDrawRankReward(rankId,res);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}