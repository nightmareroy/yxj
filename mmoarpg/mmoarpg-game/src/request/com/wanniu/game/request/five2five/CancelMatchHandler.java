package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveCancelMatchResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveCancelMatchRequest")
public class CancelMatchHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveCancelMatchResponse.Builder res = Five2FiveCancelMatchResponse.newBuilder();
				res.setS2CCode(OK);
				player.five2FiveManager.cancelFive2FiveMatch(true);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
