package com.wanniu.game.request.flee;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FleeHandler.CancelMatchResponse;

/**
 * 取消匹配大逃杀
 * 
 * @author lxm
 *
 */
@GClientEvent("area.fleeHandler.cancelMatchRequest")
public class CancelMatchHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CancelMatchResponse res = player.fleeManager.cancelMatchFlee();
				body.writeBytes(res.toByteArray());
			}
		};
	}
}