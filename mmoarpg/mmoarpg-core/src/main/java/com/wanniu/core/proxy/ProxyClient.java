package com.wanniu.core.proxy;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.proxy.ProxyType.ProxyMethod;
import com.wanniu.core.proxy.message.ProxyJoinMessage;
import com.wanniu.core.proxy.message.ProxyPingMessage;
import com.wanniu.core.tcp.client.ClientWorker;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.tcp.protocol.Prefix;
import com.wanniu.core.tcp.protocol.RequestMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * proxy服务器客户端
 * 
 * @author agui
 */
public final class ProxyClient extends ClientWorker {

	private static ProxyClient instance = new ProxyClient();

	public static ProxyClient getInstance() {
		return instance;
	}

	private ProxyClient() {
	}

	public void start() {
		if (GConfig.getInstance().isEnableProxy()) {
			this.serverHost = GConfig.getInstance().get("server.proxy.host");
			this.serverPort = GConfig.getInstance().getInt("server.proxy.port");
			this.bootstrap = new ProxyBootstrap(new ProxySessionHandler(this));
			super.start();
		}
	}

	@Override
	public void ping() {
		add(new ProxyPingMessage());
	}

	@Override
	public void doStart() {
		// 注册服务器到proxy服务器数据包
		send(new ProxyJoinMessage());
	}

	public JSONObject request(ProxyMethod type, JSONObject json) {
		Packet pak = request(new RequestMessage() {
			@Override
			protected void write() throws IOException {
				body.writeLong(reqId);
				body.writeShort(type.value);
				body.writeString(json.toJSONString());
			}

			@Override
			public short getType() {
				return ProxyType.REQUEST;
			}
		});

		return JSON.parseObject(pak.getString());
	}
	
	public Packet query(int sid, JSONObject json) {
		Packet pak = request(new RequestMessage() {
			@Override
			protected void write() throws IOException {
				body.writeLong(reqId);
				body.writeInt(sid);
				body.writeString(json.toJSONString());
			}
			@Override
			public short getType() {
				return ProxyType.QUERY;
			}
		});

		return pak;
	}

	@Override
	public void handlePacket(Packet pak) {
		if (pak.getPacketType() == ProxyType.REQUEST || pak.getPacketType() == ProxyType.RESULT) {
			long reqId = pak.getLong();
			response(reqId, pak);
		} else if (pak.getPacketType() == ProxyType.DISPONSE) {
			GGame.getInstance().closeArea(pak.getString());
		} else if (pak.getPacketType() == ProxyType.DIE) {
			GGame.getInstance().onPlayerDie(pak.getString(), pak.getString(), pak.getString());
		} else if (pak.getPacketType() == ProxyType.AREA_RECEIVE) {
			ByteBuf buf = Unpooled.wrappedBuffer(pak.getBytes(Prefix.INT));
			int count = pak.getShort();
			for (int i = 0; i < count; i++) {
				GPlayer player = GGame.getInstance().getPlayer(pak.getString());
				if (player != null && player.getSession() != null) {
					player.getSession().writeAndFlush(buf.slice());
				}
			}
		} else {
			GGame.getInstance().ansycExec(new Runnable() {
				@Override
				public void run() {
					GGame.getInstance().onAcrossReceive(pak);
				}
			});
		}
	}

	public void stop() {
		this.close(session);
	}

}
