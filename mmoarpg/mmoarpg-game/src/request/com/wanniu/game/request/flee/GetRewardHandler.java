package com.wanniu.game.request.flee;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FleeHandler.GetRewardRequest;
import pomelo.area.FleeHandler.GetRewardResponse;

/**
 * 大逃杀领取段位奖励
 * 
 * @author lxm
 *
 */
@GClientEvent("area.fleeHandler.getRewardRequest")
public class GetRewardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetRewardRequest req = GetRewardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetRewardResponse res = player.fleeManager.getRewardResponse(req.getGradeId());
				body.writeBytes(res.toByteArray());
			}
		};
	}
}