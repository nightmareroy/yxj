package com.wanniu.pay;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GConfig;
import com.wanniu.tcp.protocol.GDecoder;
import com.wanniu.tcp.protocol.GEncoder;

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
 * 
 * @author agui
 */
public final class GServerBootstrap {
	private final static Logger logger = LogManager.getLogger(GServerBootstrap.class);
	private static GServerBootstrap instance;

	private ServerBootstrap serverBootstrap;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private ChannelFuture future;

	private GServerBootstrap() {}

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
					// pipeline.addLast("idleHandler", new IdleStateHandler(60, 0, 00));
					pipeline.addLast("decoder", new GDecoder());
					pipeline.addLast("encoder", new GEncoder());
					pipeline.addLast("handler", new ServerSessionHandler());
				};
			});

			serverBootstrap
					// SO_REUSEADDR
					// Socket参数，地址复用，默认值False
					.option(ChannelOption.SO_REUSEADDR, true)
					// TCP_NODELAY
					// TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
					.childOption(ChannelOption.TCP_NODELAY, true);

			int port = GConfig.getInstance().getLoginPort();
			InetSocketAddress socketAddress = new InetSocketAddress(port);
			future = serverBootstrap.bind(socketAddress).sync();
			logger.info("服务绑定于 -> {}" + socketAddress);
		} catch (Exception e) {
			shutDown();
			logger.error(e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
		}
	}

	/**
	 * 结束侦听服务
	 */
	public void shutDown() {
		if (future != null) {
			future.cancel(true);
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully().awaitUninterruptibly(10);
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully().awaitUninterruptibly(10);
			;
		}
	}
}
