package com.wanniu.game.request.flee;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FleeHandler.FleeInfoResponse;

/**
 * 大逃杀主界面请求
 * 
 * @author lxm
 *
 */
@GClientEvent("area.fleeHandler.fleeInfoRequest")
public class FleeInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FleeInfoResponse res = player.fleeManager.getFleeInfoResponse();
				body.writeBytes(res.toByteArray());
			}
		};
	}
}