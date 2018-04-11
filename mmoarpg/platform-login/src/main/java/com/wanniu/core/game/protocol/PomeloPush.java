package com.wanniu.core.game.protocol;

import io.netty.buffer.ByteBuf;

/**
 * pomelo消息结构
 * 
 * @author agui
 */
public abstract class PomeloPush extends PomeloResponse {

	public PomeloPush() {
		this.type = Protocol.TYPE_PUSH;
		this.getHeader().route_s = getRoute();
	}

	public ByteBuf getContent() {
		return super.getContent().slice();
	}
	
	public abstract String getRoute();

}
