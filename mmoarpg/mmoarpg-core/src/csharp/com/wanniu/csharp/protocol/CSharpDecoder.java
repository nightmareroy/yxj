package com.wanniu.csharp.protocol;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 请求消息解码类，大于MAX_REQUEST_PACKET的包将不予解析
 * @author agui
 */
public class CSharpDecoder extends ByteToMessageDecoder {

	public CSharpDecoder() {
		
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> messages) throws Exception {
		if(in.readableBytes() < CSharpHeader.SIZE) {
			return;
		}
		in.markReaderIndex();
		CSharpHeader header = new CSharpHeader();
		// pomelo header length
//		while (true) {
//			if ((header.readU8(in) & 0x80) == 0) {
//				break;
//			}
//		}
//		if(in.readableBytes() < CSharpHeader.SIZE) {
//			in.resetReaderIndex();
//			return;
//		}
		// pomelo header length
		header.decode(in);

		int len = header.getCharpLength();
		if (len < 0) {
			ctx.channel().close();
			return;
		}

		if (in.readableBytes() < len) {
			in.resetReaderIndex();
			header = null;
			return;
		}
		header.readBody(in);

		CSharpPacket packet = new CSharpPacket(header);
		packet.readBody(in);
		
		messages.add(packet);
	}

}
