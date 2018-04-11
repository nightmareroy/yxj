package com.wanniu.core.game.protocol;

import com.wanniu.core.tcp.protocol.Header;

import io.netty.buffer.ByteBuf;

/**
 * pomelo通讯协议包头
 * @author agui
 */
public final class PomeloHeader extends Header {

	public final static byte SIZE = 4;

	protected byte pomelo_type = Protocol.TYPE_DATA;
	protected int length;

	public boolean compressRoute;
	public short route_n;
	public String route_s;
	
	public boolean isBattlePak() {
		return compressRoute || "area.playerHandler.battleEventNotify".equals(route_s);
	}

	public void setRoute(String route) {
		route_s = route;
		compressRoute = false;
	}
	public void setRoute(short route) {
		route_n = route;
		type = route;
		compressRoute = true;
	}
	public void setRoute(short route_n, String route_s) {
		this.route_n = route_n;
		this.route_s = route_s;
		if (route_s != null) {
			compressRoute = true;
		}
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getPomeloType() {
		return this.pomelo_type;
	}
	
	public void setPomeloType(byte pomelo_type) {
		this.pomelo_type = pomelo_type;
	}

	public String getTypeHexString() {
		return super.getTypeHexString() + ":" + route_s;
	}
	
	/**
	 * 把包头的各个属性编码到out里 建议只在codec的encoder里使用
	 */

	public void encode(ByteBuf out) {
		out.writeByte(pomelo_type);
		out.writeByte(length >> 16 & 0xFF);
		out.writeByte(length >> 8 & 0xFF);
		out.writeByte(length & 0xFF);
	}

	/**
	 * 从in里解析包头的各个属性 建议只在codec的decoder里使用
	 */
	public void decode(ByteBuf in) {
		pomelo_type = in.readByte();
		length = ((in.readByte() & 0xFF) << 16) | ((in.readByte() & 0xFF) << 8) | (in.readByte() & 0xFF);
	}

}
