package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.ArrayList;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.LuckyAwardViewResponse;
import pomelo.item.ItemOuterClass.MiniItem;
@GClientEvent("area.activityHandler.luckyAwardViewRequest")
public class LuckyAwardViewHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		LuckyAwardViewRequest req = LuckyAwardViewRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				LuckyAwardViewResponse.Builder res = LuckyAwardViewResponse.newBuilder();
				ArrayList<MiniItem> data = player.activityManager.luckyAwardView();
				res.setS2CCode(OK);
				res.addAllS2CData(data);
				body.writeBytes(res.build().toByteArray());
				
			}
		};
	}

}
