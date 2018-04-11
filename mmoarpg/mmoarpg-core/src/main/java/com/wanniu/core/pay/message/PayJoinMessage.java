package com.wanniu.core.pay.message;

import java.io.IOException;

import com.wanniu.core.GGame;
import com.wanniu.core.pay.PayType;
import com.wanniu.core.tcp.protocol.Message;

/**
 * 加入到充值服务
 */
public class PayJoinMessage extends Message {

	@Override
	protected void write() throws IOException {
		body.writeInt(GGame.__SERVER_ID);
	}

	@Override
	public short getType() {
		return PayType.JOIN;
	}

}
