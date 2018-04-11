package com.wanniu.core.tcp.client;

import com.wanniu.core.tcp.protocol.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 回调服务器的反馈信息
 * @author agui
 */
public class ClientSessionHandler extends ChannelInboundHandlerAdapter {

	private final ClientCallback callback;

	public ClientSessionHandler(ClientCallback callback) {
		this.callback = callback;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		callback.close();
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		callback.handlePacket((Packet) msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		super.channelWritabilityChanged(ctx);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		callback.bind(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		callback.close();
	}

}