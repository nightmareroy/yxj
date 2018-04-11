package com.wanniu.gm;

import java.io.IOException;

import com.wanniu.game.GWorld;

public class GMErrorResponse extends GMResponse {

	public GMErrorResponse() {
		super(0xFB);
	}

	@Override
	protected void response() throws IOException {
		body.writeInt(GWorld.__SERVER_ID);
	}

}
