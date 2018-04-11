package com.wanniu.csharp.protocol;

import com.wanniu.core.GGlobal;
import com.wanniu.core.tcp.protocol.Header;

import io.netty.buffer.ByteBuf;

/**
 * pomelo通讯协议包头
 * 
 * @author agui
 */
public final class CSharpHeader extends Header {

	/** 包头的字节数 */
	public final static byte SIZE = 8;

	private String uid;//, instanceId;
	private int uidLength = 0;//, instanceIdLength = 0;

	public int getCharpLength() {
		return this.uidLength + this.length;// + this.instanceIdLength;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
		this.uidLength = uid.length();
	}

	public int getUidLength() {
		return this.uidLength;
	}

	public int getBodyLength() {
		return getLength();
	}

//	public String getInstanceId() {
//		return this.instanceId;
//	}

	/**
	 * 把包头的各个属性编码到out里 建议只在codec的encoder里使用
	 */

	public void encode(ByteBuf out) {
		out.writeShort(uid.length());
		out.writeInt(this.getLength());
		out.writeBytes(uid.getBytes(GGlobal.UTF_8));
	}

	/**
	 * 从in里解析包头的各个属性 建议只在codec的decoder里使用
	 */
	public void decode(ByteBuf in) {
		uidLength = readU16(in);
		//instanceIdLength = readU16(in);
		length = readU32(in);
	}

	public int readU8(ByteBuf in) {
		return in.readByte() & 0xFF;
	}

	public int readU16(ByteBuf in) {
		return readU8(in) | (readU8(in) << 8);
	}

	public int readU32(ByteBuf in) {
		return readU8(in) | (readU8(in) << 8) | (readU8(in) << 16) | (readU8(in) << 24);
	}

	public void readBody(ByteBuf in) {
		byte[] uid = new byte[uidLength];
		in.readBytes(uid);
		this.uid = new String(uid, GGlobal.UTF_8);

//		byte[] instanceId = new byte[instanceIdLength];
//		in.readBytes(instanceId);
//		this.instanceId = new String(instanceId, GGlobal.UTF_8);
	}

}
