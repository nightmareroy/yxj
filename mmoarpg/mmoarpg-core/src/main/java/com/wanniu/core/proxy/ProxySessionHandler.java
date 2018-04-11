package com.wanniu.core.proxy;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.client.ClientCallback;
import com.wanniu.core.tcp.client.ClientSessionHandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author agui
 */
@Sharable
public class ProxySessionHandler extends ClientSessionHandler {

	public ProxySessionHandler(ClientCallback callback) {
		super(callback);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Out.error("连接出现异常，Session:"
				, ctx.channel().remoteAddress(), "; Exception:"
				, cause.getMessage());
//		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
	
}