package com.wanniu.game.request.entry;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.connector.EntryHandler.GetSysTimeResponse;

@GClientEvent("connector.entryHandler.getSysTimeRequest")
public class GetSysTimeHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
//		GetSysTimeRequest req = GetSysTimeRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetSysTimeResponse.Builder res = GetSysTimeResponse.newBuilder();
				res.setS2CCode(OK);
				res.setS2CTime(String.valueOf(System.currentTimeMillis()));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x312;
	}

}
