package com.wanniu.handler;

import com.wanniu.pay.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

@GClientEvent(0xf2)
public class PayPingHandler extends NetHandler {

	@Override
	public void execute(Packet pak) {
//		Integer sid = pak.getAttr(GGlobal._KEY_SERVER_ID);
	}

}
