package com.wanniu.core.pay;

import com.wanniu.core.GConfig;
import com.wanniu.core.pay.connect.PayBootstrap;
import com.wanniu.core.pay.connect.PaySessionHandler;
import com.wanniu.core.pay.message.PayJoinMessage;
import com.wanniu.core.pay.message.PayPingMessage;
import com.wanniu.core.pay.request.PayHandler;
import com.wanniu.core.tcp.client.ClientWorker;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * 充值服务器客户端
 * 
 * @author lxm
 */
public final class PayClient extends ClientWorker {

	private static PayClient instance = new PayClient();

	private PayDispatcher __dispacher__;

	public static PayClient getInstance() {
		return instance;
	}

	private PayClient() {
		__dispacher__ = new PayDispatcher();
	}

	@Override
	public void ping() {
		add(new PayPingMessage());
	}

	public void start() {
		if (GConfig.getInstance().isEnablePay()) {
			this.serverHost = GConfig.getInstance().get("server.pay.host");
			this.serverPort = GConfig.getInstance().getInt("server.pay.port");
			this.bootstrap = new PayBootstrap(new PaySessionHandler(this));
			new Thread(__dispacher__, String.format("GM<-%s:%d", serverHost, serverPort)).start();
			super.start();
		}
	}

	/**
	 * 注册充值服务请求处理句柄
	 */
	public void registerHandler(PayHandler handler) {
		__dispacher__.registerHandler(handler);
	}

	@Override
	public void doStart() {
		// 注册服务器到GM服务器数据包
		add(new PayJoinMessage());
	}

	@Override
	public void handlePacket(Packet packet) {
		__dispacher__.add(packet);
	}

	public void stop() {
		this.close(session);
	}

	public NetHandler getHandler(short type) {
		return __dispacher__.handlers.get(type);
	}

}
