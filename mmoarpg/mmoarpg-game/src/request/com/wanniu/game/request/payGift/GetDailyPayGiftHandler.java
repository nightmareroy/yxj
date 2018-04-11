package com.wanniu.game.request.payGift;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

@GClientEvent("area.payGiftHandler.getDailyPayGiftRequest")
public class GetDailyPayGiftHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		
		return null;
	}

}
