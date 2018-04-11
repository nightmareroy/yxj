package com.wanniu.pay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.tcp.PacketDispatcher;
import com.wanniu.tcp.protocol.Header;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

/**
 * 逻辑分发
 * 
 * @author agui
 */
public final class LogicDispatcher extends PacketDispatcher {
	private final static Logger logger = LogManager.getLogger(LogicDispatcher.class);

	/**
	 * 逻辑句柄处理
	 */
	public final void execute(Packet action) {
		Header header = action.getHeader();
		NetHandler handler = handlers.get(header.getType());
		if (handler != null) {
			if (action.isClosed())
				return;
			try {
				handler.bindSession(action.getSession());
				handler.execute(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.warn("未找到该协议号:{}" + header.getTypeHexString());
		}
	}
}
