package com.wanniu.core.game.message;

import com.wanniu.core.game.protocol.PomeloResponse;

import io.netty.buffer.ByteBuf;

/**
 * 组播消息
 * 
 * @author agui
 */
public abstract class MulticastMessage extends PomeloResponse {

	@Override
	public ByteBuf getContent() {
		return super.getContent().slice();
	}

	public String getRoute() {
		return null;
	}
}
