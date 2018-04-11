package com.wanniu.game.player;

import com.wanniu.core.tcp.protocol.Body;

@FunctionalInterface
public interface ProxyEventCB {
	void put(Body body);
}
