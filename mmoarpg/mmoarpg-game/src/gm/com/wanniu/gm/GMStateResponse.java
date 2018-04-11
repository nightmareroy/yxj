package com.wanniu.gm;

import java.io.IOException;

import com.wanniu.game.GWorld;

public class GMStateResponse extends GMResponse {

	private byte state;

	public GMStateResponse(int state) {
		super(0xFA);
		this.state = (byte) state;
	}

	@Override
	protected void response() throws IOException {
		body.writeInt(GWorld.__SERVER_ID);
		body.writeByte(0);
		body.writeByte(state);
	}

}
