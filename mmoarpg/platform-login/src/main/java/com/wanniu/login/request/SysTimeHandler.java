package com.wanniu.login.request;

import java.io.IOException;

import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.game.request.GClientEvent;
import com.wanniu.login.proto.Common.SysTimeResponse;

@GClientEvent("connector.entryHandler.getSysTimeRequest")
public class SysTimeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SysTimeResponse.Builder res = SysTimeResponse.newBuilder();
				res.setS2CCode(OK);

				res.setS2CTime(String.valueOf(System.currentTimeMillis()));

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
