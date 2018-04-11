package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.RewardInfoResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.rewardInfoRequest")
public class RewardInfoHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				RewardInfoResponse.Builder res = RewardInfoResponse.newBuilder();
			    player.soloManager.handleRewardInfo(res);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}