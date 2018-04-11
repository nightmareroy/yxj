package com.wanniu.core.pay.connect;

import com.wanniu.core.tcp.client.ClientBootstrap;
import com.wanniu.core.tcp.protocol.GCodecFactory;

import io.netty.channel.ChannelHandler;

/**
 * 
 * @author agui
 */
public class PayBootstrap extends ClientBootstrap {

	public PayBootstrap(ChannelHandler handler) {
		super(new GCodecFactory(handler));
	}

}
