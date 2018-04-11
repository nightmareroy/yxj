package com.wanniu.game.common.msg;

import java.io.IOException;

import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.game.protocol.PomeloPush;

/**
 * 推送消息
 * 
 * @author Yangzz
 *
 */
public class MessagePush extends PomeloPush {

	private GeneratedMessage msg;
	private String route;

	public MessagePush(String route, GeneratedMessage msg) {
		this.msg = msg;
		this.route = route;
		this.getHeader().route_s = route;
	}

	@Override
	protected void write() throws IOException {
		body.writeBytes(msg.toByteArray());
	}

	@Override
	public String getRoute() {
		return this.route;
	}

	public GeneratedMessage getMsg() {
		return this.msg;
	}

}
