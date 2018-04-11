package com.wanniu.gm;

import java.net.InetSocketAddress;

import com.wanniu.GConfig;
import com.wanniu.tcp.protocol.GDecoder;
import com.wanniu.tcp.protocol.GEncoder;
import com.wanniu.util.Out;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端网络服务引导
 * @author agui
 */
public final class GServerBootstrap {

	private static GServerBootstrap instance;

	private ServerBootstrap serverBootstrap;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	private ChannelFuture future;
	
	private GServerBootstrap() { }

	public static GServerBootstrap getInstance() {
		if (instance == null) {
			instance = new GServerBootstrap();
		}
		return instance;
	}

	/**
	 * 调用此方法绑定端口侦听服务，添加编解码过滤器
	 */
	public void start() {
			try {
				bossGroup = new NioEventLoopGroup();
				workerGroup = new NioEventLoopGroup();
				serverBootstrap = new ServerBootstrap();

				serverBootstrap.group(bossGroup, workerGroup);
				serverBootstrap.channel(NioServerSocketChannel.class);
				
				serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
//						pipeline.addLast("idleHandler", new IdleStateHandler(60, 0, 00));
						pipeline.addLast("decoder", new GDecoder());
						pipeline.addLast("encoder", new GEncoder());
						pipeline.addLast("handler", new ServerSessionHandler());
					};
				});
//				serverBootstrap.childHandler(new XCodecFactory(new ServerSessionHandler()));

				serverBootstrap.option(ChannelOption.SO_LINGER, 0)
//				.option(ChannelOption.SO_BACKLOG, 128)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

				String ip = GConfig.getInstance().getLoginHost();
				int port = GConfig.getInstance().getLoginPort();
				
				InetSocketAddress socketAddress = (ip != null && ip.length() > 6) ? new InetSocketAddress(ip, port) : new InetSocketAddress(port);
				future = serverBootstrap.bind(socketAddress).sync();
				Out.info("服务绑定于 -> " + socketAddress);
			} catch (Exception e) {
				shutDown();
				Out.error(e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			} 
	}

	/**
	 * 结束侦听服务
	 */
	public void shutDown() {
		if(future != null) {
			future.cancel(true);
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully().awaitUninterruptibly(10);
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully().awaitUninterruptibly(10);;
		}
	}
}
