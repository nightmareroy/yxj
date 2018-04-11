package com.wanniu.gm;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GGlobal;
import com.wanniu.core.tcp.protocol.Prefix;
import com.wanniu.game.GWorld;

public class GMJsonResponse extends GMResponse {

	private String json;

	public GMJsonResponse(JSONObject json) {
		super(0xFA);
		this.json = json.toJSONString();
	}

	@Override
	protected void response() throws IOException {
		body.writeInt(GWorld.__SERVER_ID);
		body.writeByte(1);
		body.writeBytes(Prefix.INT, json.getBytes(GGlobal.UTF_8));
	}

	public GMJsonResponse(String json) {
		super(0xFA);
		this.json = json;
	}
}
