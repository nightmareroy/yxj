package com.wanniu.core.game.protocol;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.BufferUtil;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.csharp.protocol.CSharpMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 请求消息解码类，大于MAX_REQUEST_PACKET的包将不予解析
 * @author agui
 */
public class PomeloDecoder extends ByteToMessageDecoder {

	private static int __REQ_MAX_LEN = GConfig.getInstance().getInt("tcp.request.maxlen", GGlobal.__REQUEST_MAX_LEN);
	
	private static final ByteBuf HANDSHAKE;
	/**心跳包*/
	private static final ByteBuf HEARTBEAT;
	static {
		// {"code":200,"sys":{"heartbeat":30},"user":{"hand1":"aaa"}}
		JSONObject res = new JSONObject();
		res.put("code", 200);
		JSONObject sys = new JSONObject();
		sys.put("heartbeat", 30);
		res.put("sys", sys);
		byte[] bs = res.toJSONString().getBytes(GGlobal.UTF_8);
		PomeloHeader head = new PomeloHeader();
		head.setPomeloType(Protocol.TYPE_HANDSHAKE);
		head.setLength(bs.length);
		HANDSHAKE = BufferUtil.getAutoBuffer(PomeloHeader.SIZE + bs.length);
		head.encode(HANDSHAKE);
		HANDSHAKE.writeBytes(bs);
		
		PomeloHeader head_hb = new PomeloHeader();
		head_hb.setPomeloType(Protocol.TYPE_HEARTBEAT);
		head_hb.setLength(0);
		HEARTBEAT = BufferUtil.getAutoBuffer(PomeloHeader.SIZE);
		head_hb.encode(HEARTBEAT);
	}
	
	public PomeloDecoder() {
		
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> messages) throws Exception {
		if(in.readableBytes() < PomeloHeader.SIZE) {
			return;
		}
		in.markReaderIndex();
		
		PomeloPacket packet = new PomeloPacket(ctx.channel());
		PomeloHeader header = packet.getHeader();
		header.decode(in);
		
		int len = header.getLength();
		
		if (len > __REQ_MAX_LEN || len < 0) {
			Channel session = ctx.channel();
			Out.warn(header.route_s, "包体长度错误：", len, session.remoteAddress());
			session.close();
			return;
		}
		
		if (in.readableBytes() < len) {
			in.resetReaderIndex();
			header = null;
			packet = null;
			return;
		}

		ByteBuf body = packet.init();
		body.writeBytes(in, len);
		switch (header.pomelo_type) {
			case Protocol.TYPE_HANDSHAKE: {
//				String json = new String(body.array(), GGlobal.UTF_8);
//				Out.info("TYPE_HANDSHAKE: ", json);
				ctx.channel().writeAndFlush(BufferUtil.getBuffer(HANDSHAKE.array()));
				break;
			}
			case Protocol.TYPE_HANDSHAKE_ACK: {
				Out.debug("TYPE_HANDSHAKE_ACK!!");
				break;
			}
			case Protocol.TYPE_DATA: {
				packet.readyBody();
//				messages.add(packet);
				if (header.isBattlePak()) {
					CSharpClient.getInstance().dispatch(new CSharpMessage(packet));
				} else {
					header.setReceiveTime(System.nanoTime());
					GGame.getInstance().putGlobalRoute(header.route_s, packet);
				}
				break;
			}
			case Protocol.TYPE_HEARTBEAT: {
				Out.debug("TYPE_HEARTBEAT: ", new String(body.array(), GGlobal.UTF_8));
//				ctx.channel().writeAndFlush(BufferUtil.getBuffer(BytesUtil.int2bytes(Protocol.TYPE_HEARTBEAT)));
				ctx.channel().writeAndFlush(BufferUtil.getBuffer(HEARTBEAT.array()));
				break;
			}
			case Protocol.TYPE_KICK: {
				Out.debug("TYPE_KICK: ", new String(body.array(), GGlobal.UTF_8));
				break;
			}
		}
	}
}
