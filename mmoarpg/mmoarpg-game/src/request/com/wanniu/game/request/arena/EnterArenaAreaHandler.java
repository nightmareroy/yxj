package com.wanniu.game.request.arena;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.area.ArenaHandler.EnterArenaAreaResponse;

/**
 * 请求进入竞技场
 * 
 * @author wfy
 *
 */
@GClientEvent("area.arenaHandler.enterArenaAreaRequest")
public class EnterArenaAreaHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterArenaAreaResponse.Builder res = EnterArenaAreaResponse.newBuilder();
				player.arenaManager.handleEnterArenaArea(res);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}