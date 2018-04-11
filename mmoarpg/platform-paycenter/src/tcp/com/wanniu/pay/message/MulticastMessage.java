package com.wanniu.pay.message;

import com.wanniu.tcp.protocol.Message;

import io.netty.buffer.ByteBuf;

/**
 * 组播消息
 * 
 * @author agui
 */
public abstract class MulticastMessage extends Message {

	@Override
	public ByteBuf getContent() {
		return super.getContent().slice();
	}

	public String getRoute() {
		return null;
	}
}
