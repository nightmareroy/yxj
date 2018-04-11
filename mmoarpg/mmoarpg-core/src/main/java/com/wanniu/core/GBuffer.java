package com.wanniu.core;

import java.nio.ByteBuffer;

import com.wanniu.core.tcp.protocol.Prefix;

/**
 * 字节缓冲工具（非Direct）
 * @author agui
 */
public final class GBuffer {

	private ByteBuffer body;

	private GBuffer(int size) {
		malloc(size);
	}
	
	private GBuffer(byte[] src) {
		body = ByteBuffer.wrap(src).order(GGlobal.__BYTE_ORDER);
	}

	private GBuffer(ByteBuffer input) {
		this.body = input;
	}
	
	/** 默认分配XConst.__BUFFER_CAPACITY */
	public static GBuffer allocate(){
		return allocate(GGlobal.__BUFFER_CAPACITY);
	}
	public static GBuffer allocate(int size) {
		return new GBuffer(size);
	}
	/** bytes工具 */
	public static GBuffer wrap(byte[] bytes) {
		return new GBuffer(bytes);
	}
	/** buffer工具 */
	public static GBuffer wrap(ByteBuffer buffer) {
		return new GBuffer(buffer);
	}

	public boolean getBoolean() {
		return getByte() != 0;
	}

	public byte getByte() {
		return body.get();
	}

	public short getShort() {
		return body.getShort();
	}

	public int getInt() {
		return body.getInt();
	}

	public float getFloat() {
		return body.getFloat();
	}

	public long getLong() {
		return body.getLong();
	}

	public double getDouble() {
		return body.getDouble();
	}

	/** short长度 */
	public String getString() {
		short size = getShort();
		if (size == 0) {
			return null;
		}
		byte[] tmp = new byte[size];
		body.get(tmp);
		return new String(tmp, GConst.UTF_8);
	}

	public byte[] getBytes(int size) {
		byte[] bytes = new byte[size];
		body.get(bytes);
		return bytes;
	}

	public GBuffer flip() {
		body.flip();
		return this;
	}
	
	public void skip(int size) {
		body.position(body.position() + size);
	}
	
	/**
	 * 返回指定前缀的字节
	 */
	public byte[] getBytes(Prefix prefix) {
		byte[] bytes = null;
		switch (prefix) {
			case BYTE:
				bytes = new byte[body.get()];
				break;
			case SHORT:
				bytes = new byte[body.getShort()];
				break;
			case INT:
				bytes = new byte[body.getInt()];
				break;
		}
		body.get(bytes);
		return bytes;
	}

	/**
	 * 返回缓冲区的字节
	 */
	public byte[] getBytes() {
		if (body.position() > 0) {
			byte[] bytes = new byte[body.position()];
			body.flip();
			body.get(bytes);
			return bytes;
		}
		byte[] remain = new byte[body.limit() - body.position()];
		body.get(remain);
		return remain;
	}
	
	/**
	 * 向body填充指定前缀的data数据
	 */
	public void putBytes(Prefix prefix, byte[] data) {
		switch (prefix) {
			case BYTE:
				putByte(data.length);
				break;
			case SHORT:
				putShort(data.length);
				break;
			case INT:
				putInt(data.length);
				break;
		}
		put(data);
	}

	public void putBuffer(Prefix prefix, ByteBuffer buffer) {
		switch (prefix) {
			case BYTE:
				putByte(buffer.remaining());
				break;
			case SHORT:
				putShort(buffer.remaining());
				break;
			case INT:
				putInt(buffer.remaining());
				break;
		}
		put(buffer);
	}

	/** short长度 */
	public void putString(String value) {
		if (value == null) {
			putShort(0);
			return;
		}
		byte[] sbuf = value.getBytes(GConst.UTF_8);
		putBytes(Prefix.SHORT, sbuf);
	}

	public void putBoolean(boolean b){
		putByte((byte) (b ? 1: 0));
	}
	
	public void putShort(int i) {
		putShort((short) i);
	}

	public void putByte(int i) {
		putByte((byte) i);
	}
	
	/**
	 * 把指定的src数据encode到body里
	 */
	public void put(byte[] src) {
		autoExpand(src.length);
		body.put(src);
	}

	public void put(byte[] src, int start, int length) {
		autoExpand(length);
		body.put(src, start, length);
	}

	public void put(ByteBuffer buffer) {
		autoExpand(buffer.remaining());
		body.put(buffer);
	}

	public void putDouble(double d) {
		autoExpand(8);
		body.putDouble(d);
	}

	public void putFloat(float f) {
		autoExpand(4);
		body.putFloat(f);
	}

	public void putLong(long l) {
		autoExpand(8);
		body.putLong(l);
	}

	public void putInt(int i) {
		autoExpand(4);
		body.putInt(i);
	}

	public void putShort(short s) {
		autoExpand(2);
		body.putShort(s);
	}

	public void putByte(byte b) {
		autoExpand(1);
		body.put(b);
	}

	private void malloc(int size) {
		body = ByteBuffer.allocate(size).order(GGlobal.__BYTE_ORDER);
	}
	
	private void autoExpand(int count){
		if(body.capacity() < body.position()+ count) {
			ByteBuffer buffer = body;
			buffer.flip();
			malloc(body.capacity() +Math.max(32, count));
			body.put(buffer);
		}
	}

}
