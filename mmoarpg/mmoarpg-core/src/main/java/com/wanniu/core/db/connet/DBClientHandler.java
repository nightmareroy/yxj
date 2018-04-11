package com.wanniu.core.db.connet;

import com.wanniu.core.tcp.client.ClientCallback;
import com.wanniu.core.tcp.client.ClientSessionHandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author agui
 *
 */
@Sharable
public class DBClientHandler extends ClientSessionHandler {


	public DBClientHandler(ClientCallback callback) {
		super(callback);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// cause.printStackTrace();
		ctx.channel().close();
		super.exceptionCaught(ctx, cause);
	}

}
