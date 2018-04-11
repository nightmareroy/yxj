package com.wanniu.game.request.vip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.VipHandler.BuyVipCardResponse;

@GClientEvent("area.vipHandler.buyVipCardRequest")
public class BuyVipCardHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {

		
		BuyVipCardResponse.Builder res = BuyVipCardResponse.newBuilder();
		

	    {
			res.setS2CCode(FAIL);
			res.setS2CMsg(LangService.getValue("CONFIG_ERR"));
	    }
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
