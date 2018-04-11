package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.PlayerHandler.ReliveSendPosResponse;

/**
 * 发送坐标
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.reliveSendPosRequest")
public class ReliveSendPosHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ReliveSendPosResponse.Builder res = ReliveSendPosResponse.newBuilder();


				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}


}