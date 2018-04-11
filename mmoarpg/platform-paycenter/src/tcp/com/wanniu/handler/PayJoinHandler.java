package com.wanniu.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GGlobal;
import com.wanniu.GServer;
import com.wanniu.pay.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

@GClientEvent(0xf1)
public class PayJoinHandler extends NetHandler {
	private final static Logger logger = LogManager.getLogger(PayJoinHandler.class);

	@Override
	public void execute(Packet pak) {
		int sid = pak.getInt();
		pak.setAttr(GGlobal._KEY_SERVER_ID, sid);
		GServer.getInstance().addChannel(pak.getSession());
		logger.info("sid:{} pay join!", sid);
	}

}
