package com.wanniu.core.proxy.message;

import java.io.IOException;

import com.wanniu.core.GGame;
import com.wanniu.core.game.message.ResponseMessage;
import com.wanniu.core.proxy.ProxyType;

/**
 * 
 * @author agui
 */
public class ProxyPingMessage extends ResponseMessage {


	public ProxyPingMessage() {
		super();
	}

	@Override
	protected void write() throws IOException {
		body.writeShort(GGame.getInstance().getOnlineCount());
	}

	@Override
	public short getType() {
		return ProxyType.PING;
	}

}
