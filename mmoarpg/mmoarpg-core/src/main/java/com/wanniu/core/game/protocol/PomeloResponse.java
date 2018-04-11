package com.wanniu.core.game.protocol;

import com.wanniu.core.tcp.BufferUtil;
import com.wanniu.core.tcp.protocol.Header;
import com.wanniu.core.tcp.protocol.Message;

import io.netty.buffer.ByteBuf;

/**
 * pomelo消息结构
 * 
 * @author agui
 */
public abstract class PomeloResponse extends Message {

	protected Header newHeader() {
		return new PomeloHeader();
	}

	public PomeloHeader getHeader() {
		return (PomeloHeader) this.header;
	}

	private ByteBuf route;
	private int capacity;
	public byte type = Protocol.TYPE_RESPONSE;

	protected ByteBuf newContent() {
		PomeloHeader header = this.getHeader();
		int capacity = Protocol.MSG_FLAG_BYTES;
		capacity += (Protocol.msgHasId(type) ? Protocol.caculateMsgIdBytes(header.getType()) : 0);
		if (Protocol.msgHasRoute(type)) {
			if (header.compressRoute) {
				capacity += Protocol.MSG_ROUTE_CODE_BYTES;
			} else {
				route = Protocol.strencode(header.route_s);
				capacity += Protocol.MSG_ROUTE_LEN_BYTES;
				capacity += route.readableBytes();
			}
		}
		capacity += body.getBuffer().readableBytes();
		this.capacity = capacity;
		return BufferUtil.getAutoBuffer(PomeloHeader.SIZE + capacity);
	}

	protected void encodeHeader() {
		PomeloHeader header = this.getHeader();
		// header.setPomeloType(Protocol.TYPE_DATA);
		header.setLength(capacity);
		header.encode(content);
	}

	protected void encodeBody() {
		PomeloHeader header = this.getHeader();

		// flag
		int flag = (type << 1) | (header.compressRoute ? 1 : 0);
		// if (header.compressGzip) {
		// flag = flag | Protocol.MSG_COMPRESS_GZIP_ENCODE_MASK;
		// }
		content.writeByte(flag);

		// id
		if (Protocol.msgHasId(type)) {
			int id = header.getType();
			do {
				int tmp = id % 128;
				int next = id / 128;
				if (next != 0) {
					tmp = tmp + 128;
				}
				content.writeByte(tmp);
				id = next;
			} while (id != 0);
		}

		// route
		if (Protocol.msgHasRoute(type)) {
			if (header.compressRoute) {
				content.writeByte((header.route_n >> 8) & 0xff);
				content.writeByte(header.route_n & 0xff);
			} else {
				if (route != null) {
					content.writeByte(route.readableBytes() & 0xff);
					content.writeBytes(route);
				} else {
					content.writeByte(0);
				}
			}
		}

		// body
		content.writeBytes(body.getBuffer());
	}
	
	public short getType() {
		return 0;
	}
	
}
