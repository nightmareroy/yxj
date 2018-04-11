package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.PlayerHandler.PickItemResponse;

/**
 * 拾取道具
 * 
 * @author Yangzz
 *
 */
@Deprecated
@GClientEvent("area.playerHandler.pickItemRequest")
public class PickItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

//		WNPlayer player = (WNPlayer) pak.getPlayer();
//
//		PickItemRequest req = PickItemRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				PickItemResponse.Builder res = PickItemResponse.newBuilder();

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}