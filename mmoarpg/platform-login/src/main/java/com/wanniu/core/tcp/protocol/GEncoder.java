package com.wanniu.core.tcp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 发送消息编码类
 * @author agui
 */
public final class GEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.getContent());
		ctx.flush();
	}

}
