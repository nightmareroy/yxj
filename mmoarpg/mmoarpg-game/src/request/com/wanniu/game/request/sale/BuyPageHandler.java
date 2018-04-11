package com.wanniu.game.request.sale;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sale.SaleManager;

import pomelo.area.SaleHandler.BuyPageRequest;
import pomelo.area.SaleHandler.BuyPageResponse;

@GClientEvent("area.saleHandler.buyPageRequest")
public class BuyPageHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		BuyPageRequest req = BuyPageRequest.parseFrom(pak.getRemaingBytes());
		List<Integer> typeIds = req.getC2SSellIndexList();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				BuyPageResponse.Builder res = player.saleManager.handleBuyPage(typeIds);
				if(res.getS2CCode() == SaleManager.ERR_CODE.ERR_CODE_OK.getValue()){
					res.setS2CCode(OK);
				}
				else{
					res.setS2CCode(FAIL);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
