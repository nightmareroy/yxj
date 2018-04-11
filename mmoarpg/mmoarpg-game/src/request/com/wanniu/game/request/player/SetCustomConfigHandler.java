package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.CustomConfigRequest;
import pomelo.area.PlayerHandler.CustomConfigResponse;

/**
 * 保存客户端自定义数据
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.setCustomConfigRequest")
public class SetCustomConfigHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();

		CustomConfigRequest req = CustomConfigRequest.parseFrom(pak.getRemaingBytes());
		String key = req.getC2SKey();
		String value = req.getC2SValue();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CustomConfigResponse.Builder res = CustomConfigResponse.newBuilder();

				if (player.setClientCustomConfig(key, value, false)) {
					res.setS2CCode(OK);
				} else {
					res.setS2CCode(FAIL);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
