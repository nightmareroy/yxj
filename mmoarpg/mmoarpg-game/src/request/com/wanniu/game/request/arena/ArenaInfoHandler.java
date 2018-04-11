package com.wanniu.game.request.arena;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.area.ArenaHandler.ArenaInfoResponse;

/**
 * 竞技场界面信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.arenaHandler.arenaInfoRequest")
public class ArenaInfoHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ArenaInfoResponse.Builder res = player.arenaManager.getArenaInfo();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}