package com.wanniu.core.proxy;

import com.wanniu.core.tcp.client.ClientBootstrap;
import com.wanniu.core.tcp.protocol.GCodecFactory;

import io.netty.channel.ChannelHandler;

/**
 * 
 * @author agui
 */
public class ProxyBootstrap extends ClientBootstrap {

	public ProxyBootstrap(ChannelHandler handler) {
		super(new GCodecFactory(handler));
	}

}
