package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.consignmentShop.ConsignmentUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ConsignmentLineHandler.MyConsignmentRequest;
import pomelo.area.ConsignmentLineHandler.MyConsignmentResponse;

@GClientEvent("area.consignmentLineHandler.myConsignmentRequest")
public class MyConsignmentHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		MyConsignmentRequest msg = MyConsignmentRequest.parseFrom(pak.getRemaingBytes());
		int globalZone = msg.getC2SGlobal();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MyConsignmentResponse.Builder res = MyConsignmentResponse.newBuilder();
				res.setS2CCode(OK);
				List<pomelo.item.ItemOuterClass.ConsignmentItem> list = player.consignmentManager.getAll();
				res.addAllS2CData(list);
				res.setS2CCanSellNum(ConsignmentUtil.sellNum(player));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
