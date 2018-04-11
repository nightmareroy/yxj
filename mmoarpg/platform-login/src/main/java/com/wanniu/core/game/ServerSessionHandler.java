package com.wanniu.core.game;

import com.wanniu.core.GServer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;

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
		Out.debug("连接被建立，Session:" + ctx.channel().remoteAddress().toString());
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Out.debug("exceptionCaught");
		Channel session = ctx.channel();
		// XIPBlacks.getInstance().exceptionIp(session);
		Out.warn(session.remoteAddress() + "未验证时发生的异常");
		ctx.close();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel session = ctx.channel();
		GServer.getInstance().onSessionClose(session);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		GServer.getInstance().addPacket((Packet)packet);
	}
		
	
}
