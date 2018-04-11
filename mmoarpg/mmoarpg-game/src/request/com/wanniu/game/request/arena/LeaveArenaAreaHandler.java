package com.wanniu.game.request.arena;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.area.ArenaHandler.LeaveArenaAreaResponse;

/**
 * 请求离开竞技场场景
 * 
 * @author wfy
 *
 */
@GClientEvent("area.arenaHandler.leaveArenaAreaRequest")
public class LeaveArenaAreaHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {

				LeaveArenaAreaResponse.Builder res = LeaveArenaAreaResponse.newBuilder();
				player.arenaManager.handleLeaveArenaArea(res);

				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}