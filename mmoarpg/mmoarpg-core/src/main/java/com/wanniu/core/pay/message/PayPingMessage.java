package com.wanniu.core.pay.message;

import java.io.IOException;

import com.wanniu.core.game.message.ResponseMessage;
import com.wanniu.core.pay.PayType;

public class PayPingMessage extends ResponseMessage {


	public PayPingMessage() {
		super();
	}

	@Override
	protected void write() throws IOException {
	}

	@Override
	public short getType() {
		return PayType.PING;
	}

}
