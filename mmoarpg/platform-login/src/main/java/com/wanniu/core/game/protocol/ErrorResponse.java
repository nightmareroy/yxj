package com.wanniu.core.game.protocol;

import java.io.IOException;

import com.wanniu.login.proto.Common.ErrMsg;

public class ErrorResponse extends PomeloResponse {

	private String errMsg;

	public ErrorResponse(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	protected void write() throws IOException {
		ErrMsg.Builder err = ErrMsg.newBuilder();
		err.setS2CCode(500);
		err.setS2CMsg(errMsg);
		body.writeBytes(err.build().toByteArray());
	}

}
