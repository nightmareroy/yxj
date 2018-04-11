package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveAgreeMatchResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveAgreeMatchRequest")
public class AgreeMatchHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveAgreeMatchResponse.Builder res = Five2FiveAgreeMatchResponse.newBuilder();
				String msg = player.five2FiveManager.agreeMatch(player);
				if (!StringUtil.isEmpty(msg)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}
