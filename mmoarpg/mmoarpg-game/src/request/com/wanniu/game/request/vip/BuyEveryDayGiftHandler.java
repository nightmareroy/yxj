package com.wanniu.game.request.vip;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

@GClientEvent("area.vipHandler.buyEveryDayGiftRequest")
public class BuyEveryDayGiftHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		return null;
	}

}