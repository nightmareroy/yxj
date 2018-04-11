package com.wanniu.core.game;

import java.io.IOException;

import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;

/**
 * IO处理器，所有接收到的消息put到队列里，等待处理器分发处理
 * 
 * @author agui
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public final class ServerSessionHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Out.info("连接被建立，Session:", ctx.channel().remoteAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// IO异常，只输出概要信息就可以了...
		if (cause instanceof IOException || cause instanceof DecoderException) {
			Out.debug("Netty try IOException||DecoderException.", ctx.channel().remoteAddress(), cause.getMessage());
		} else {
			Out.error("Netty try exception.", ctx.channel().remoteAddress(), cause);
		}
		ctx.close();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel session = ctx.channel();
		Out.info("连接被关闭:", session.remoteAddress());
		try {
			if (session.attr(GGlobal.__KEY_PLAYER) != null) {
				GPlayer player = session.attr(GGlobal.__KEY_PLAYER).get();
				if (player != null && player.getSession() == session) {
					player.doLogout(false);
				}
			}
		} finally {
			GGame.getInstance().onSessionClose(session);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		GGame.getInstance().addPacket((Packet) packet);
	}
}