package com.wanniu.tcp.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.wanniu.GSystem;
import com.wanniu.tcp.protocol.Message;
import com.wanniu.util.Out;

import io.netty.channel.Channel;

/**
 * 客户端连接执行者
 * @author agui
 */
public abstract class ClientWorker implements Runnable, ClientCallback {

	private static final int __WARN_COUNT__ = 3000;

	protected volatile boolean disConnect = true;

	protected String serverHost;
	protected int serverPort;

	private BlockingQueue<Message> __QUEUE__ = new LinkedBlockingQueue<Message>();

	protected ClientBootstrap bootstrap;
	
	private Channel session;

	protected String name;
	
	public ClientWorker(String name) {
		this.name = name;
	}

	/**
	 * 添加消息到队里中， 并检查队列大小
	 */
	public void add(Message message) {
		__QUEUE__.add(message);
		if (__QUEUE__.size() > __WARN_COUNT__) {
			// 添加报警方法
			Out.warn(name + "发送队列太长: " + __QUEUE__.size());
		}
	}

	public void start() {
		if (bootstrap.connect(serverHost, serverPort) == null) {
			Out.error(String.format("无法连接%s -> %s:%d", name, serverHost, serverPort));
		}
		new Thread(this, String.format("%s->%s:%d", name, serverHost, serverPort)).start();
	}

	public void restart() {
		bootstrap.quit(session);
		while (bootstrap.connect(serverHost, serverPort) == null) {
			Out.warn(String.format("无法重连%s -> %s:%d", name, serverHost, serverPort));
			GSystem.waitSeconds(5);
		}
	}

	public void run() {
		GSystem.waitSeconds(5);
		while (true) {
			try {
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
		Out.info(name + "注册成功,开始发送信息");
		this.session = session;
		__QUEUE__.clear();
		disConnect = false;
		doStart();
	}

	public void close() {
		Out.warn(String.format("%s关闭了 - %s:%d", name, serverHost, serverPort));
		Out.info(String.format("开始重连%s -> %s:%d", name, serverHost, serverPort));
		restart();
		Out.info(String.format("成功重连%s -> %s:%d", name, serverHost, serverPort));
	}

}
