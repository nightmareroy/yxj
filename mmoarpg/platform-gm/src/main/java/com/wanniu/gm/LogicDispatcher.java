package com.wanniu.gm;

import com.wanniu.tcp.PacketDispatcher;
import com.wanniu.tcp.protocol.Header;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.util.Out;

/**
 * 逻辑分发
 * 
 * @author agui
 */
public final class LogicDispatcher extends PacketDispatcher {

	/**
	 * 逻辑句柄处理
	 */
	public final void execute(Packet action) {
		Header header = action.getHeader();
		NetHandler handler = handlers.get(header.getType());
		if (handler != null) {
			if (action.isClosed()) return;
			try {
				handler.bindSession(action.getSession());
				handler.execute(action);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			Out.warn("未找到该协议号:" + header.getTypeHexString());
		}
	}
}
