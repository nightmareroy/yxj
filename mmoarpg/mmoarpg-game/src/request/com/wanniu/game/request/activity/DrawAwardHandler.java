package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.ActivityHandler.DrawAwardRequest;

@GClientEvent("area.activityHandler.drawAwardRequest")
public class DrawAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		DrawAwardRequest req = DrawAwardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {

			}
		};
	}

}
