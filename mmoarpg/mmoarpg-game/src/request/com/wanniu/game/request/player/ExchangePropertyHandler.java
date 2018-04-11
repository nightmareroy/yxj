package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.ExchangePropertyRequest;
import pomelo.area.PlayerHandler.ExchangePropertyResponse;

/**
 * 兑换属性请求.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.playerHandler.exchangePropertyRequest")
public class ExchangePropertyHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		ExchangePropertyRequest request = ExchangePropertyRequest.parseFrom(pak.getRemaingBytes());
		final int type = request.getType();

		WNPlayer player = (WNPlayer) pak.getPlayer();

		PomeloResponse result = player.baseDataManager.exchange(type);
		if (result != null) {
			return result;
		}

		ExchangePropertyResponse.Builder res = ExchangePropertyResponse.newBuilder();
		res.setS2CCode(OK);
		res.setCount(player.getPlayer().exchangCount);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}