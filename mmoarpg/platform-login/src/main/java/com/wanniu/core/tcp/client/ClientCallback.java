package com.wanniu.core.tcp.client;

import com.wanniu.core.tcp.NetEvent;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * 处理事件接口
 * @author agui
 */
public interface ClientCallback extends NetEvent {

	/** 接收到信息包 */
	void handlePacket(Packet packet);
	
}
