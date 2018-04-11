package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.ExchangePropertyInfoResponse;

/**
 * 拉取兑换属性信息请求.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.playerHandler.exchangePropertyInfoRequest")
public class ExchangePropertyInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		ExchangePropertyInfoResponse.Builder res = ExchangePropertyInfoResponse.newBuilder();
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