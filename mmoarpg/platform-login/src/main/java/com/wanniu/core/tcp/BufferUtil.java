package com.wanniu.core.tcp;

import com.wanniu.core.GGlobal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Buffer工具类
 * @author agui
 */
public final class BufferUtil {
	
	/**
	 * 相当调用getAutoBuffer(XConst.__BUFFER_CAPACITY)
	 * @return
	 */
	public static ByteBuf getAutoBuffer() {
		return getAutoBuffer(GGlobal.__BUFFER_CAPACITY);
	}

	/**
	 * 用指定的初始容量生成一个自动扩展容量的ChannelBuffer
	 * 
	 * @param initCapacity
	 * @return
	 */
	public static ByteBuf getAutoBuffer(int initCapacity) {
		ByteBuf body = Unpooled.buffer(initCapacity).order(GGlobal.__BYTE_ORDER);
		return body;
	}

	/**
	 * 取出给定的ChannelBuffer的所有字节
	 * 
	 * @param src
	 *            要取字节的ChannelBuffer
	 * @return
	 */
	public static byte[] getBufBytes(ByteBuf src) {
		return src.array();
	}

	public static ByteBuf getBuffer(byte[] buf) {
		ByteBuf body = Unpooled.buffer(buf.length).order(GGlobal.__BYTE_ORDER);
		return body.writeBytes(buf);
	}

}
