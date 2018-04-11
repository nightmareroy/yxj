package com.wanniu.core.game.message;

import java.io.IOException;

import com.wanniu.core.tcp.protocol.Packet;

import io.netty.buffer.ByteBuf;

/***
 * 功能描述：直接转发消息封装
 * @author agui
 */
public class DispatcherMessage extends MulticastMessage {

	private ByteBuf buffer;
	
	public DispatcherMessage(Packet packet) {
		this.header.decode(packet.getHeader());
		this.buffer = packet.getBody();
	}

	@Override
	protected void write() throws IOException {
		body.writeBuffer(buffer);
	}

	@Override
	public short getType() {
		return header.getType();
	}

}
