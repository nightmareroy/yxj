package com.wanniu.csharp;

import com.wanniu.core.tcp.client.ClientBootstrap;
import com.wanniu.csharp.protocol.CSharpCodecFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 
 * @author agui
 */
public class CSharpBootstrap extends ClientBootstrap {

	public CSharpBootstrap(ChannelHandler handler) {
		super(new CSharpCodecFactory(handler), new NioEventLoopGroup());
	}
	
}
