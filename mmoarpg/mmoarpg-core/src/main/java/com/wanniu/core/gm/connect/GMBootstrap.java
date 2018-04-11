package com.wanniu.core.gm.connect;

import com.wanniu.core.tcp.client.ClientBootstrap;
import com.wanniu.core.tcp.protocol.GCodecFactory;

import io.netty.channel.ChannelHandler;

/**
 * 
 * @author agui
 */
public class GMBootstrap extends ClientBootstrap {

	public GMBootstrap(ChannelHandler handler) {
		super(new GCodecFactory(handler));
	}

}
