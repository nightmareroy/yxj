package com.wanniu.csharp.protocol;

import com.wanniu.core.tcp.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 返回消息编码类
 * 
 * @author agui
 */
public class CSharpEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.getContent());
	}

}
