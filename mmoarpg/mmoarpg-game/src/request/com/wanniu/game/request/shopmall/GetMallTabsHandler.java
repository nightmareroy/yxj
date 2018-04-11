package com.wanniu.game.request.shopmall;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ShopMallHandler.GetMallTabsResponse;
import pomelo.area.ShopMallHandler.MallTab;


@GClientEvent("area.shopMallHandler.getMallTabsRequest")
public class GetMallTabsHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		return new PomeloResponse(){
			@Override
			protected void write() throws IOException {
				GetMallTabsResponse.Builder res = GetMallTabsResponse.newBuilder();
				List<MallTab.Builder> items = wPlayer.shopMallManager.getMallTabs();
				res.setS2CCode(OK);
				for(int i = 0;i<items.size();i++){
					res.addS2CTabs(items.get(i).build());
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
