package com.wanniu.tcp.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.wanniu.util.Out;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端引导
 * @author agui
 */
public class ClientBootstrap {
	
	private Bootstrap boots;
	protected Channel session;

	public ClientBootstrap(ChannelInitializer<SocketChannel> factory) {
		boots = new Bootstrap();
		boots.channel(NioSocketChannel.class);
		boots.option(ChannelOption.TCP_NODELAY, true);
		boots.option(ChannelOption.SO_KEEPALIVE, true);
		boots.handler(factory);
		boots.group(new NioEventLoopGroup(2));
	}

	public Channel connect(String host, int port) {
		try {
			ChannelFuture future = boots.connect(new InetSocketAddress(host, port));
			future.awaitUninterruptibly(10, TimeUnit.SECONDS);

			if (!future.isSuccess()) {
				future.cause().printStackTrace();
				return null;
			}
			session = future.channel();
			return future.channel();
		} catch (Exception e) {
			Out.error(e);
			return null;
		}
	}

	public void quit(Channel session) {
		if (session != null) {
			session.close();
		}
	}
}
