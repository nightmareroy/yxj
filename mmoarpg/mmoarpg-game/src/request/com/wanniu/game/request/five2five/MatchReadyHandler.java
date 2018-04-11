package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveReadyRequest;
import pomelo.five2five.Five2FiveHandler.Five2FiveReadyResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveReadyRequest")
public class MatchReadyHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveReadyRequest req = Five2FiveReadyRequest.parseFrom(pak.getRemaingBytes());

				String teamId = req.getTempTeamId();
				Five2FiveReadyResponse.Builder res = Five2FiveReadyResponse.newBuilder();
				String msg = player.five2FiveManager.matchReady(teamId);
				if (!StringUtil.isEmpty(msg)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
