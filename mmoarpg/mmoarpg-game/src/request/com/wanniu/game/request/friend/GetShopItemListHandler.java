package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.FriendHandler.GetShopItemListResponse;

@GClientEvent("area.friendHandler.getShopItemListRequest")
public class GetShopItemListHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
//				FriendManager friendManager = player.getFriendManager();
//				ArrayList<FriendShopExchangeInfo> data = friendManager.getShopItemList();
				GetShopItemListResponse.Builder res = GetShopItemListResponse.newBuilder();
				res.setS2CCode(OK);
//				res.addAllS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
