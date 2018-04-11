package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveMatchRequest;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveMatchRequest")
public class MatchHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				Five2FiveMatchRequest req = Five2FiveMatchRequest.parseFrom(pak.getRemaingBytes());
				int matchOrReMatch = req.getMatchOrReMatch();

				Five2FiveMatchResponse.Builder res = Five2FiveMatchResponse.newBuilder();
				player.five2FiveManager.applyFive2FiveMatch(res, matchOrReMatch);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
