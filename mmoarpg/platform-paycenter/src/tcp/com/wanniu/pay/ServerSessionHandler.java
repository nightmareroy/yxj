package com.wanniu.pay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GServer;
import com.wanniu.tcp.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * IO处理器，所有接收到的消息put到队列里，等待处理器分发处理
 * 
 * @author agui
 */
public final class ServerSessionHandler extends ChannelInboundHandlerAdapter {
	private final static Logger logger = LogManager.getLogger(ServerSessionHandler.class);

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		logger.debug("handlerAdded");
		logger.debug("连接被建立，Session:{}", ctx.channel().remoteAddress().toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.debug("exceptionCaught");
		ctx.close();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		logger.debug("handlerRemoved");
		Channel channel = ctx.channel();
		GServer.getInstance().removeChannel(channel);
		logger.debug("连接被关闭:" + channel.remoteAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		logger.debug("channelRead0");
		GServer.getInstance().addPacket((Packet) packet);
	}
}
