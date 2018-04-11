package com.wanniu.core.game;

import com.wanniu.core.tcp.PacketDispatcher;
import com.wanniu.core.tcp.protocol.Header;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * 战斗逻辑分发
 * 
 * @author agui
 */
public final class BattleDispatcher extends PacketDispatcher {

	/**
	 * 逻辑句柄处理
	 */
	public final void execute(Packet action) {
		Header header = action.getHeader();
		NetHandler handler = handlers.get(header.getType());
		if (handler != null) {
			if (action.isClosed()) return;
			handler.bindSession(action.getSession());
			handler.execute(action);
		} else {
//			sendClient(action);
		}
	}

}
