package com.wanniu.game.common.msg;

import java.io.IOException;

import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.Common.ErrMsg;

public class ErrorResponse extends PomeloResponse {

	private String errMsg;

	public ErrorResponse() {}

	public ErrorResponse(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	protected void write() throws IOException {
		ErrMsg.Builder err = ErrMsg.newBuilder();
		err.setS2CCode(500);
		err.setS2CMsg(errMsg != null ? errMsg : "");
		body.writeBytes(err.build().toByteArray());
	}
}