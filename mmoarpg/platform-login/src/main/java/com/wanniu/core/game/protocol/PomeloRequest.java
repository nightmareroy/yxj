package com.wanniu.core.game.protocol;

import com.wanniu.core.game.request.GameHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * 游戏客户端报文处理句柄基类
 * @author agui
 */
public abstract class PomeloRequest extends GameHandler {

	public static final int OK = 200;
	public static final int KICK = 400;
	public static final int FAIL = 500;

	protected Packet pak;

//	@SuppressWarnings("unchecked")
//	public <T extends MessageLite> T getProto(MessageLite prototype) throws InvalidProtocolBufferException {
//		return (T) prototype.getParserForType().parseFrom(pak.getRemaingBytes());
//	}

	public void execute(Packet pak) {
		this.pak = pak;
		PomeloResponse res = null;
		try {
			res = this.request();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (res != null) {
			PomeloHeader header = res.getHeader();
			header.setType(pak.getHeader().getType());
			write(res);
		}
	}

	public abstract PomeloResponse request() throws Exception;
	
}
