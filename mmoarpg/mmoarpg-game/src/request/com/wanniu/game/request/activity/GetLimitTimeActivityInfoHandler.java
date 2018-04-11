package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.LimitTimeActivityHandler.GetLimitTimeActivityInfoResponse;

@GClientEvent("area.limitTimeActivityHandler.getLimitTimeActivityInfoRequest")
public class GetLimitTimeActivityInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			protected void write() throws IOException {
				GetLimitTimeActivityInfoResponse.Builder res = GetLimitTimeActivityInfoResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
