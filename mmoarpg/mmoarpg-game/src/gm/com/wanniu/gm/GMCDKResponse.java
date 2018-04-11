package com.wanniu.gm;

import java.io.IOException;

import com.wanniu.core.GGlobal;

public class GMCDKResponse extends GMResponse {

	private boolean ok = true;
	private byte[] bytes;

	public GMCDKResponse(byte[] bytes) {
		super(0xFC);
		this.bytes = bytes;
	}

	public GMCDKResponse(String errMsg) {
		this(errMsg.getBytes(GGlobal.UTF_8));
		ok = false;
	}

	@Override
	protected void response() throws IOException {
		body.writeBoolean(ok);
		body.writeInt(bytes.length);
		body.writeBytes(bytes);
	}

}
