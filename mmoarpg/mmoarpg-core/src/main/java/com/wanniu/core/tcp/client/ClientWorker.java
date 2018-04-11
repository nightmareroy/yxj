package com.wanniu.core.tcp.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GSystem;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.tcp.protocol.RequestMessage;

import io.netty.channel.Channel;

/**
 * 客户端连接执行者
 * @author agui
 */
public abstract class ClientWorker implements Runnable, ClientCallback {

	private static final int __WARN_COUNT__ = GConfig.getInstance().getInt("game.worker.threshold", 10000);

	protected final BlockingQueue<Message> __QUEUE__ = new LinkedBlockingQueue<Message>();
	
	protected String serverHost;
	protected int serverPort;

	protected ClientBootstrap bootstrap;
	
	protected Channel session;
	protected volatile boolean disconnect = true;
	
	protected String name;
	
	public String getName() {
		return name;
	}
	
	public ClientWorker() {
		this.name = this.getClass().getSimpleName();
	}

	/**
	 * 添加消息到队里中， 并检查队列大小
	 */
	public void add(Message message) {
		__QUEUE__.add(message);
		if (size() > __WARN_COUNT__) {
			// 添加报警方法
			Out.warn(name, "发送队列太长: ", __QUEUE__.size());
		}
	}

	public int size() {
		return __QUEUE__.size();
	}

	public void start() {
		GGame.getInstance().onWorkerBefore(this);
		bind(bootstrap.connect(serverHost, serverPort));
		new Thread(this, String.format("%s->%s:%d", name, serverHost, serverPort)).start();
	}

	public void run() {
//		GSystem.waitSeconds(5);
		while (true) {
			try {
				if (disconnect) {
					String connName = Thread.currentThread().getName();
					Out.info("开始重连", connName);
					while ((session = bootstrap.connect(serverHost, serverPort)) == null) {
						Out.warn("无法重连", connName);
						GSystem.waitSeconds(5);
					}
					Out.info("成功重连", connName);
					bind(session);
				}
				Message msg = __QUEUE__.poll(10, TimeUnit.SECONDS);
				if (msg == null) {
					// 发送ping
					ping();
				} else {
					send(msg);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

	protected void send(Message msg) {
		if (session != null) {
			session.writeAndFlush(msg.getContent());
		}
	}

	public abstract void doStart();

	public void bind(Channel session) {
		if (session != null) {
			this.session = session;
			__QUEUE__.clear();
			doStart();
			disconnect = false;
			GGame.getInstance().onWorkerReady(this);
			Out.info(name, "注册成功,开始发送信息...");
		}
	}

	public void close(Channel session) {
		if (this.session == session) {
			disconnect = true;
			Out.error(name, " close!!!");
		}
	}

	public Packet request(RequestMessage req) {
		return bootstrap.request(req);
	}
	
	public void response(long reqId, Packet pak) {
		bootstrap.response(reqId, pak);
	}
	
}
