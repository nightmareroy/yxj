package com.wanniu.core.game.protocol;

import com.wanniu.core.GGame;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Pomelo通讯协议请求包继承自网络包<br>
 * 解析请求包时可根据具体协议调用相应的getBoolean、getByte、getInt、getString等方法，注意不要重复解析
 * 
 * @author agui
 */
public class PomeloPacket extends Packet {
	public static final AttributeKey<Short> AUTO_INDEX = AttributeKey.valueOf("auto_index");
	public static final AttributeKey<Integer> ERROR_COUNT = AttributeKey.valueOf("error_count");

	public PomeloPacket(Channel session) {
		header = new PomeloHeader();
		this.session = session;
	}

	public short getPacketType() {
		return header.getType();
	}

	/**
	 * 获取包头的引用
	 * 
	 * @return
	 */
	public PomeloHeader getHeader() {
		return (PomeloHeader) header;
	}

	/**
	 * override 方法
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return header.getTypeHexString();
	}

	public int getU8() {
		return body.readByte() & 0xFF;
	}

	public void readyBody() {
		byte flag = getByte();
		PomeloHeader header = getHeader();
		header.compressRoute = (flag & Protocol.MSG_COMPRESS_ROUTE_MASK) != 0;
		byte type = (byte) ((flag >> 1) & Protocol.MSG_TYPE_MASK);

		boolean copy = false;// 是否为复制封包...
		Integer count = 0;

		if (Protocol.msgHasId(type)) {
			int id = 0;
			int m = 0;
			int i = 0;
			do {
				m = getU8();
				id += (m & 0x7f) * (1 << (7 * i));
				i++;
			} while (m >= 128);
			header.setType((short) id);

			// 自增校验位的判定...
			Attribute<Short> attr = session.attr(AUTO_INDEX);
			Short autoIndex = attr.get();
			if (autoIndex == null || autoIndex == header.getType() - 1 || autoIndex == Short.MAX_VALUE) {
				attr.set(header.getType());
			} else {
				Out.error("复制封包 autoIndex:", autoIndex, " header:", header.getType(), " Id:", this.getPlayer().getId(), " Name:", this.getPlayer().getName());
				Attribute<Integer> errorAttr = session.attr(ERROR_COUNT);
				count = errorAttr.get();
				if (count == null) {
					count = 1;
					errorAttr.set(1);
				} else {
					errorAttr.set(count + 1);
					copy = true;
				}
			}
		}
		if (Protocol.msgHasRoute(type)) {
			if (header.compressRoute) {
				header.route_n = (short) (getU8() << 8 | getU8());
			} else {
				int routeLen = getU8();
				byte[] dst = new byte[routeLen];
				getBytes(dst);
				header.route_s = Protocol.strdecode(dst);
			}
		}

		// 复制封包进行额外的处理.
		if (copy) {
			GGame.getInstance().handleCopyPacket(this.getPlayer(), count, header);
		}
	}
}