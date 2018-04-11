package com.wanniu.gm;

import com.wanniu.GServer;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.util.Out;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * IO处理器，所有接收到的消息put到队列里，等待处理器分发处理
 * @author agui
 */
public final class ServerSessionHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Out.debug("handlerAdded");
		Out.debug("连接被建立，Session:" + ctx.channel().remoteAddress().toString());
	}

//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		Out.debug("channelActive");
//		super.channelActive(ctx);
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Out.debug("exceptionCaught");
		ctx.close();
	}

//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		Out.debug("channelReadComplete");
//		super.channelReadComplete(ctx);
//	}
//
//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		Out.debug("channelInactive");
//		super.channelInactive(ctx);
//	}
//
//	@Override
//	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//		Out.debug("channelUnregistered");
//		super.channelUnregistered(ctx);
//	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Out.debug("handlerRemoved");
		Channel channel = ctx.channel();
		GServer.getInstance().removeChannel(channel);
		Out.debug("连接被关闭:" + channel.remoteAddress());
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		Out.debug("channelRead0");
		GServer.getInstance().addPacket((Packet)packet);
	}
		
	
}
