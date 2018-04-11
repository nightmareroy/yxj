package com.wanniu.game.request.flee;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FleeHandler.EnterFleeResponse;

/**
 * 请求匹配大逃杀
 * 
 * @author lxm
 *
 */
@GClientEvent("area.fleeHandler.enterFleeRequest")
public class EnterFleeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterFleeResponse res = player.fleeManager.enterFlee();
				body.writeBytes(res.toByteArray());
			}
		};
	}
}