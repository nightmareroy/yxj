package com.wanniu.core.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {
	
	private HttpServerHandler httpServerHandler;
	
	
	public HttpServer(){
		this.httpServerHandler = new HttpServerHandler();
	}

	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	public void run(int port) throws Exception {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							ch.pipeline().addLast(httpServerHandler);
						}
					});
			ChannelFuture f = b.bind(port).sync();
			
			System.out.println("Http Server start Success! " +"http://127.0.0.1:" + port + '/');
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public void addHandler(HttpServerMsgHandler handler){
		httpServerHandler.addHandler(handler);
	}
	
//	public static void main(String[] args) {
//		HttpServer http=new HttpServer();
//		try {
//			http.run(8080);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
