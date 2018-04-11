package com.wanniu.csharp.protocol;

import com.wanniu.core.tcp.protocol.Packet;

import io.netty.buffer.ByteBuf;

/**
 * CSharp战斗服通讯协议请求包继承自网络包<br>
 * 解析请求包时可根据具体协议调用相应的getBoolean、getByte、getInt、getString等方法，注意不要重复解析
 * 
 * @author agui
 */
public class CSharpPacket extends Packet {
	
	public CSharpPacket(CSharpHeader header) {
		this.header = header;
	}

	public short getPacketType() {
		return header.getType();
	}

	/**
	 * 获取包头的引用
	 * 
	 * @return
	 */
	public CSharpHeader getHeader() {
		return (CSharpHeader) header;
	}

	public void readBody(ByteBuf in) {
		ByteBuf body = init();
		body.writeBytes(in, header.getLength());
	}

	/**
	 * override 方法
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return header.getTypeHexString();
	}

}
