package com.wanniu.game.request.prepaid;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PrepaidHandler.FeeItem;
import pomelo.area.PrepaidHandler.PrepaidListResponse;

@GClientEvent("area.prepaidHandler.prepaidListRequest")
public class PrepaidListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		// PrepaidListRequest req = PrepaidListRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			protected void write() throws IOException {
				PrepaidListResponse.Builder res = PrepaidListResponse.newBuilder();
				List<FeeItem> items = player.prepaidManager.getPrepaidList();
				res.addAllS2CItems(items);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
