package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TransportCO;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.TransportRequest;
import pomelo.area.PlayerHandler.TransportResponse;

/**
 * 进入场景请求
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.transportRequest")
public class TransportHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		TransportRequest req = TransportRequest.parseFrom(pak.getRemaingBytes());
		int transportId = req.getC2STransportId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TransportResponse.Builder res = TransportResponse.newBuilder();

				boolean result = PlayerUtil.transPortById(player, transportId);
				if (result) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else {
					TransportCO transportProp = GameData.Transports.get(transportId);
					if (transportProp != null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(transportProp.failTips);
						body.writeBytes(res.build().toByteArray());
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
					}
				}

			}
		};
	}
}