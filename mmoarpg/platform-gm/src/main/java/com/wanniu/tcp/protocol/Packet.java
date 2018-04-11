package com.wanniu.tcp.protocol;

import com.wanniu.GGlobal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 通讯协议数据请求包<br>
 * 解析请求包时可根据具体协议调用相应的getBoolen、getByte、getInt、getString等方法，注意不要重复解析<br>
 * @author agui
 */
public class Packet {
	/** 消息包头 */
	protected Header header;

	/** 消息体 */
	protected ByteBuf body;

	/** 每个请求包所关联的io */
	protected Channel session;

	public Packet() { }

	public Packet(Channel session) {
		this.session = session;
		this.header = new Header();
	}
	
	public ByteBuf init() {
		this.body = Unpooled.buffer(header.getLength()).order(GGlobal.__BYTE_ORDER);
		return this.body;
	}

	public double getDouble() {
		return body.readDouble();
	}

	public float getFloat() {
		return body.readFloat();
	}

	public long getLong() {
		return body.readLong();
	}

	public int getInt() {
		return body.readInt();
	}

	public short getShort() {
		return body.readShort();
	}

	public byte getByte() {
		return body.readByte();
	}

	public boolean getBoolean(){
		return getByte() == 1;
	}
	
	/**
	 * 从body里decode字节填充到指定的dst
	 * @param dst 不能为null
	 */
	public void getBytes(byte[] dst) {
		body.readBytes(dst);
	}

	/**
	 * 从body里decode一个String，String是由2字节short表示字节数，后面紧跟着字节（byte[short])
	 * @return new String(getPrefixedData(Prefix.SHORT))
	 */
	public String getString() {
		byte[] src = getBytes(Prefix.SHORT);
		return src == null ? null : new String(src, GGlobal.UTF_8);
	}

	/**
	 * 根据指定的前缀获取前缀表达的字节数组
	 */
	public byte[] getBytes(Prefix prefix) {
		int len = 0;

		switch (prefix) {
			case BYTE:
				len = getByte();
				break;
			case SHORT:
				len = getShort();
				break;
			case INT:
				len = getInt();
				break;
		}

		if (len == 0)
			return null;

		byte[] dst = new byte[len];
		getBytes(dst);

		return dst;
	}

	/**
	 * 填充报文体
	 * 
	 * @param src
	 */
	public void fillBody(byte[] src) {
		body.writeBytes(src);
	}

	/**
	 * 清除包体的内容，相当于调用body.clear()，大部分在重用此包体时调用，注意设置header的setType Clears body
	 * buffer. The position is set to zero, the limit is set to the capacity,
	 * and the mark is discarded
	 */
	public void resetBody() {
		body.clear();
	}

	/**
	 * 获取跟此包相关联的IoSession
	 * 
	 * @return
	 */
	public Channel getSession() {
		return session;
	}

	public <T> T getAttr(AttributeKey<T> att) {
		return session.attr(att).get();
	}

	public <T> void setAttr(AttributeKey<T> att, T value) {
		session.attr(att).set(value);
	}

	public String toString() {
		return header.getTypeHexString();
	}

	public short getPacketType() {
		return header.getType();
	}

	/**
	 * 获取此buffer里存储的所有字节数 调用此方法后，不能再次读取数据包中的信息 可视情况filp
	 */
	public byte[] getBytes() {
		return body.array();
	}

	public byte[] getRemaingBytes() {
		int length = body.readableBytes();
		byte[] array = new byte[length];
		body.getBytes(body.readerIndex(), array, 0, length);
		return array;
	}
	
	/**
	 * 获取协议头
	 */
	public Header getHeader() {
		return header;
	}
	/**
	 * 获取协议体
	 */
	public ByteBuf getBody() {
		return body;
	}
	/**
	 * 跳过字节数
	 */
	public void skip(int len) {
		body.skipBytes(len);
	}
	/**
	 * 剩余字节数
	 */
	public int remaing() {
		return body.readableBytes();
	}

	/**
	 * 是否已关闭
	 */
	public boolean isClosed() {
		return session == null || !session.isActive();
	}

	public void close() {
		if (session != null) {
			session.close();
		}
	}

	public String getIp() {
		if (session != null) {
			String ip = session.remoteAddress().toString();
			return ip.substring(1, ip.indexOf(":"));
		}
		return null;
	}

	public void setSession(Channel session) {
		this.session = session;
	}
	
	public boolean remaining() {
		return body.readableBytes() > 0;
	}

}
