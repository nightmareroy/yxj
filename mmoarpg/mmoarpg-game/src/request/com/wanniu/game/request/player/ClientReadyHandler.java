package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.ClientReadyResponse;

/**
 * 客户端资源加载完成的通知
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.clientReadyRequest")
public class ClientReadyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		// ClientReadyRequest req = ClientReadyRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ClientReadyResponse.Builder res = ClientReadyResponse.newBuilder();
				player.onReady();
				if (player.area != null) {
					player.area.onReady(player);
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}