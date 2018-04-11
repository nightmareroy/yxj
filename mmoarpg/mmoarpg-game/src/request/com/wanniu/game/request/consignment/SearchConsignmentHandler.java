package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ConsignmentLineHandler.SearchConsignmentRequest;
import pomelo.area.ConsignmentLineHandler.SearchConsignmentResponse;
import pomelo.item.ItemOuterClass.ConsignmentItem;

@GClientEvent("area.consignmentLineHandler.searchConsignmentRequest")
public class SearchConsignmentHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SearchConsignmentRequest msg = SearchConsignmentRequest.parseFrom(pak.getRemaingBytes());
		String condition = msg.getC2SCondition();
		List<ConsignmentItem> result = ConsignmentLineService.getInstance().search(player, GWorld.__SERVER_ID, condition);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SearchConsignmentResponse.Builder res = SearchConsignmentResponse.newBuilder();
				res.setS2CCode(OK);
				res.addAllS2CData(result);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}