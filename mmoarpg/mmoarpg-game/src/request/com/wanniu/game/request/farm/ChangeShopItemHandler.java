package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;

import pomelo.farm.FarmHandler.ChangeShopItemRequest;
import pomelo.farm.FarmHandler.ChangeShopItemResponse;

@GClientEvent("farm.farmHandler.buyShopItemRequest")
public class ChangeShopItemHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ChangeShopItemRequest msg = ChangeShopItemRequest.parseFrom(pak.getRemaingBytes());
		int itemId=msg.getItemId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeShopItemResponse.Builder res = ChangeShopItemResponse.newBuilder();
				FarmMgr farmMgr = player.farmMgr;
				
				boolean changeRes=farmMgr.ChangeShopItem(itemId);
				if(!changeRes)
				{
					res.setS2CCode(OK);
					res.setS2CMsg(LangService.getValue("FARM_CANNOT_CHANGE"));
					return;
				}
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}