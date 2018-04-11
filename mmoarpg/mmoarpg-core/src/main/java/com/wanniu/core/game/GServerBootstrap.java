package com.wanniu.core.game;

import java.net.InetSocketAddress;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.protocol.PomeloDecoder;
import com.wanniu.core.game.protocol.PomeloEncoder;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.GBootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务端网络服务引导
 * 
 * @author agui
 */
public final class GServerBootstrap extends GBootstrap {

	private static GServerBootstrap instance;

	private ServerBootstrap serverBootstrap;

	private EventLoopGroup bossGroup = Loop;
	private EventLoopGroup workerGroup = Loop;

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
							Out.info(ctx.channel().remoteAddress(), " idle close!!!");
							ctx.channel().attr(GGlobal.__KEY_SESSION_TIMEOUT).set(true);
							ctx.close();
						}

					});
					pipeline.addLast("decoder", new PomeloDecoder());
					pipeline.addLast("encoder", new PomeloEncoder());
					pipeline.addLast("handler", new ServerSessionHandler());
				};
			});

			serverBootstrap
					// SO_LINGER
					// Socket参数，关闭Socket的延迟时间，默认值为-1，表示禁用该功能。-1表示socket.close()方法立即返回，但OS底层会将发送缓冲区全部发送到对端。0表示socket.close()方法立即返回，OS放弃发送缓冲区的数据直接向对端发送RST包，对端收到复位错误。非0整数值表示调用socket.close()方法的线程被阻塞直到延迟时间到或发送缓冲区中的数据发送完毕，若超时，则对端会收到复位错误。
					// .option(ChannelOption.SO_LINGER, 0)
					// .option(ChannelOption.SO_BACKLOG, 128)
					// SO_REUSEADDR
					// Socket参数，地址复用，默认值False
					.option(ChannelOption.SO_REUSEADDR, true)
					// TCP_NODELAY
					// TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
					.childOption(ChannelOption.TCP_NODELAY, true)
					// SO_KEEPALIVE
					// Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
					// .childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 4 * 64 * 1024)//
					.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 4 * 32 * 1024)//
			;

			String ip = GConfig.getInstance().getGameHost();
			int port = GConfig.getInstance().getGamePort();
			InetSocketAddress socketAddress = (ip != null && ip.length() > 6) ? new InetSocketAddress(ip, port) : new InetSocketAddress(port);
			serverBootstrap.bind(socketAddress).sync();

			Out.info("服务绑定于 -> ", socketAddress);
		} catch (Exception e) {
			Out.error(e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
		}
	}

}
