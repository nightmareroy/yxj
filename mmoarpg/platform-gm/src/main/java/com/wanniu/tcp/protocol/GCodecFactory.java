package com.wanniu.tcp.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * 编解码工厂
 * @author agui
 */
public final class GCodecFactory extends ChannelInitializer<SocketChannel> {

	private ChannelHandler handler;
	
	public GCodecFactory(ChannelHandler handler) {
		this.handler = handler;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast("idleHandler", new IdleStateHandler(0, 0, 60));
		pipeline.addLast("decoder", new GDecoder());
		pipeline.addLast("encoder", new GEncoder());
		pipeline.addLast("handler", handler);
	}
	
}
