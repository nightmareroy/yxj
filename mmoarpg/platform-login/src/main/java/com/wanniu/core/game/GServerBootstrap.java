package com.wanniu.core.game;

import java.net.InetSocketAddress;

import com.wanniu.core.GConfig;
import com.wanniu.core.game.protocol.PomeloDecoder;
import com.wanniu.core.game.protocol.PomeloEncoder;
import com.wanniu.core.logfs.Out;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务端网络服务引导
 * 
 * @author agui
 */
public final class GServerBootstrap {

	private static GServerBootstrap instance;

	private ServerBootstrap serverBootstrap;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

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
					pipeline.addLast("idleHandler", new IdleStateHandler(60, 0, 0) {

						@Override
						protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
							Out.debug(ctx.channel(), " idle...");
							ctx.close();
						}

					});
					pipeline.addLast("decoder", new PomeloDecoder());
					pipeline.addLast("encoder", new PomeloEncoder());
					pipeline.addLast("handler", new ServerSessionHandler());
				};
			});
			// serverBootstrap.childHandler(new XCodecFactory(new ServerSessionHandler()));

			serverBootstrap
					// SO_REUSEADDR
					// Socket参数，地址复用，默认值False
					.option(ChannelOption.SO_REUSEADDR, true)
					// TCP_NODELAY
					// TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
					.childOption(ChannelOption.TCP_NODELAY, true);

			String ip = GConfig.getInstance().getLoginHost();
			int port = GConfig.getInstance().getLoginPort();
			InetSocketAddress socketAddress = (ip != null && ip.length() > 6) ? new InetSocketAddress(ip, port) : new InetSocketAddress(port);
			serverBootstrap.bind(socketAddress).sync();

			Out.info("服务绑定于 -> " + socketAddress);
		} catch (Exception e) {
			shutDown();
			Out.error(e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
		}
	}

	/**
	 * 结束侦听服务
	 */
	public void shutDown() {
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
	}
}
