package com.wanniu.core.gm;

import com.wanniu.core.GConfig;
import com.wanniu.core.gm.connect.GMBootstrap;
import com.wanniu.core.gm.connect.GMSessionHandler;
import com.wanniu.core.gm.message.GMJoinMessage;
import com.wanniu.core.gm.message.GMPingMessage;
import com.wanniu.core.gm.request.GMHandler;
import com.wanniu.core.tcp.client.ClientWorker;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * GM服务器客户端
 * @author agui
 */
public final class GMClient extends ClientWorker {
	
	private static GMClient instance = new GMClient();

	private GMDispatcher __dispacher__;

	public static GMClient getInstance() {
		return instance;
	}

	private GMClient() {
		__dispacher__ = new GMDispatcher();
	}
	
	public void start() {
		if(GConfig.getInstance().isEnableGm()) {
			this.serverHost = GConfig.getInstance().get("server.gm.host");
			this.serverPort = GConfig.getInstance().getInt("server.gm.port");
			this.bootstrap = new GMBootstrap(new GMSessionHandler(this));
//			new Thread(__dispacher__, String.format("GM<-%s:%d", serverHost, serverPort)).start();
			super.start();
		}
	}

	@Override
	public void ping() {
		add(new GMPingMessage());
	}

	/**
	 * 注册GM服务请求处理句柄
	 */
	public void registerHandler(GMHandler handler) {
		__dispacher__.registerHandler(handler);
	}

	@Override
	public void doStart() {
		// 注册服务器到GM服务器数据包
		add(new GMJoinMessage());
	}

	@Override
	public void handlePacket(Packet packet) {
//		__dispacher__.add(packet);
		__dispacher__.execute(packet);
	}

	public void stop() {
		this.close(session);
	}
	
	public NetHandler getHandler(short type) {
		return __dispacher__.handlers.get(type);
	}

}
