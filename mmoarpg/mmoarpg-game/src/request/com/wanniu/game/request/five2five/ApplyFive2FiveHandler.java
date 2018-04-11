package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveRequest")
public class ApplyFive2FiveHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				Five2FiveResponse.Builder res = Five2FiveResponse.newBuilder();
				player.five2FiveManager.applyFive2Five(player.getId(), res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
