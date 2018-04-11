package com.wanniu.core.tcp.protocol;

import java.io.IOException;

import com.wanniu.core.GGlobal;
import com.wanniu.core.tcp.BufferUtil;

import io.netty.buffer.ByteBuf;

/**
 * 包体构建类
 * @author agui
 */
public final class Body {
	
	/**
	 * 字节缓冲
	 */
	private ByteBuf body;

	public Body() {
		this(GGlobal.__BODY_CAPACITY);
	}

	public Body(int initCapacity) {
		body = BufferUtil.getAutoBuffer(initCapacity);
	}

	/**
	 * 写入一个boolean类型
	 */
	public void writeBoolean(boolean bool) {
		writeByte(bool);
	}

	/**
	 * 写入一个字节
	 */
	public void writeByte(int value) {
		body.writeByte(value);
	}

	/**
	 * 写入一个字节
	 */
	public void writeByte(boolean value) {
		body.writeByte(value ? 1 : 0);
	}

	/**
	 * 写入一个短整型数值
	 */
	public void writeShort(short value) {
		body.writeShort(value);
	}

	/**
	 * 写入一个短整型数值
	 */
	public void writeShort(int value) {
		body.writeShort((short) value);
	}

	/**
	 * 写入整型数值
	 */
	public void writeInt(int value) {
		body.writeInt(value);
	}

	/**
	 * 写入单精度浮点型数值
	 */
	public void writeFloat(float value) {
		body.writeFloat(value);
	}

	/**
	 * 写入长整型数值
	 */
	public void writeLong(long value) {
		body.writeLong(value);
	}

	/**
	 * 写入双精度浮点型数值
	 */
	public void writeDouble(double value) {
		body.writeDouble(value);
	}

	/**
	 * 写入一个字符串，长度限制为5000
	 */
	public void writeString(String utf) {
		if (utf == null) {
			body.writeShort((short) 0);
			return;
		} else if (utf.length() > 5000) {
			utf = utf.substring(0, 5000);
		}

		byte[] bytes = utf.getBytes(GGlobal.UTF_8);
		body.writeShort((short) bytes.length);
		body.writeBytes(bytes);
	}

	/**
	 * 写入字节数组
	 */
	public void writeBytes(byte[] inwrite) {
		body.writeBytes(inwrite);
	}

	/**
	 * 写入从指定下标开始的字节数组
	 * 
	 * @param inwrite
	 *            字节数组
	 * @param offset
	 *            数组起始下标
	 * @throws IOException
	 */
	public void writeBytes(byte[] inwrite, int offset) {
		body.writeBytes(inwrite, offset, inwrite.length - offset);
	}

	/**
	 * 写入字节数组中的部分
	 * 
	 * @param inwrite
	 *            字节数组
	 * @param offset
	 *            数组起始下标
	 * @param length
	 *            要写入的从起始位置开始到结尾的字节数
	 * @throws IOException
	 */
	public void writeBytes(byte[] inwrite, int offset, int length) {
		body.writeBytes(inwrite, offset, length);
	}

	/**
	 * 写入二维字节数组
	 */
	public void write2DBytes(byte[][] inwrite) {
		for (int i = 0; i < inwrite.length; i++) {
			body.writeBytes(inwrite[i]);
		}
	}
	/**
	 * 写入缓冲内的数据
	 */
	public void writeBuffer(ByteBuf buffer) {
		this.body.writeBytes(buffer);
	}
	/**
	 * 写入带标识的字节数组
	 */
	public void writeBytes(Prefix prefix, byte[] inwrite) {
		int length = inwrite != null ? inwrite.length : 0;
		switch (prefix) {
			case BYTE:
				body.writeByte((byte) length);
				break;
			case SHORT:
				body.writeShort((short) length);
				break;
			case INT:
				body.writeInt(length);
				break;
		}
		if (length == 0) {
			return;
		}
		body.writeBytes(inwrite);
	}

	/**
	 * 重置
	 */
	public void clear() {
		body.clear();
	}

	/**
	 * 缓冲区中的字节长度
	 */
	public int size() {
		return body.readableBytes();
	}

	/**
	 * 获取缓冲区中的字节数组
	 */
	public byte[] getBytes() {
		return body.array();
	}

	public ByteBuf getBuffer() {
		return body;
	}

}
