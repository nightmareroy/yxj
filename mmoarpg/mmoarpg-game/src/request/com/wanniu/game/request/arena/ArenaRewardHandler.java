package com.wanniu.game.request.arena;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.area.ArenaHandler.ArenaRewardRequest;
import pomelo.area.ArenaHandler.ArenaRewardResponse;

/**
 * 请求领取奖励
 * 
 * @author wfy
 *
 */
@GClientEvent("area.arenaHandler.arenaRewardRequest")
public class ArenaRewardHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {

				ArenaRewardResponse.Builder res = ArenaRewardResponse.newBuilder();
				ArenaRewardRequest req = ArenaRewardRequest.parseFrom(pak.getRemaingBytes());
				player.arenaManager.handleGetReward(req.getC2SType(), res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}