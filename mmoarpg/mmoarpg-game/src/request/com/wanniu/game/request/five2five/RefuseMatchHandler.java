package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveRefuseMatchResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveRefuseMatchRequest")
public class RefuseMatchHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveRefuseMatchResponse.Builder res = Five2FiveRefuseMatchResponse.newBuilder();
				String msg = player.five2FiveManager.refuseMatch(player);
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
