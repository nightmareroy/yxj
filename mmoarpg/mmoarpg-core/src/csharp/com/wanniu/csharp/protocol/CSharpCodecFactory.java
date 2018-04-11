package com.wanniu.csharp.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * 编解码工厂
 * @author agui
 */
public class CSharpCodecFactory extends ChannelInitializer<SocketChannel> {

	private ChannelHandler handler;
	
	public CSharpCodecFactory(ChannelHandler handler) {
		this.handler = handler;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("decoder", new CSharpDecoder());
		pipeline.addLast("encoder", new CSharpEncoder());
		pipeline.addLast("handler", handler);
	}
	
}
