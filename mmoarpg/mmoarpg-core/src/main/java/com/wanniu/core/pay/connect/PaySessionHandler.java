package com.wanniu.core.pay.connect;

import com.wanniu.core.logfs.Out;
import io.netty.channel.ChannelHandler.Sharable;
import com.wanniu.core.tcp.client.ClientCallback;
import com.wanniu.core.tcp.client.ClientSessionHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author agui
 */
@Sharable
public class PaySessionHandler extends ClientSessionHandler {

	public PaySessionHandler(ClientCallback callback) {
		super(callback);
	}

//	@Override
//	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//		Out.error("GM服务器连接被关闭:" + e.getChannel().getRemoteAddress().toString());
//		super.channelDisconnected(ctx, e);
//	}
//
//	@Override
//	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//		Out.info("建立与GM服务器的连接:" +
//				e.getChannel().getLocalAddress().toString() +"->" + e.getChannel().getRemoteAddress().toString());
//		super.channelOpen(ctx, e);
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Out.error("连接出现异常，Session:"
				, ctx.channel().remoteAddress(), "; Exception:"
				, cause.getMessage());
//		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
	
}