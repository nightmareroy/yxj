package com.wanniu.core.gm.message;

import java.io.IOException;

import com.wanniu.core.GGame;
import com.wanniu.core.game.message.ResponseMessage;
import com.wanniu.core.gm.GMType;

/**
 * 
 * @author agui
 */
public class GMPingMessage extends ResponseMessage {


	public GMPingMessage() {
		super();
	}

	@Override
	protected void write() throws IOException {
		body.writeShort(GGame.getInstance().getOnlineCount());
	}

	@Override
	public short getType() {
		return GMType.PING;
	}

}
