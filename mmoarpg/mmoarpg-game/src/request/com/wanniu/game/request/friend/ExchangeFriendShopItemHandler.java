package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.FriendHandler.ExchangeFriendShopItemResponse;

@GClientEvent("area.friendHandler.exchangeFriendShopItemRequest")
public class ExchangeFriendShopItemHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {

				ExchangeFriendShopItemResponse.Builder res = ExchangeFriendShopItemResponse.newBuilder();

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
