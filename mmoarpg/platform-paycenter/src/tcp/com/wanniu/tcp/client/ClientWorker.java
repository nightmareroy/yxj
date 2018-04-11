package com.wanniu.tcp.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GSystem;
import com.wanniu.tcp.protocol.Message;

import io.netty.channel.Channel;

/**
 * 客户端连接执行者
 * 
 * @author agui
 */
public abstract class ClientWorker implements Runnable, ClientCallback {
	private final static Logger logger = LogManager.getLogger(ClientWorker.class);
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
			logger.warn(name + "发送队列太长: " + __QUEUE__.size());
		}
	}

	public void start() {
		if (bootstrap.connect(serverHost, serverPort) == null) {
			logger.error(String.format("无法连接%s -> %s:%d", name, serverHost, serverPort));
		}
		new Thread(this, String.format("%s->%s:%d", name, serverHost, serverPort)).start();
	}

	public void restart() {
		bootstrap.quit(session);
		while (bootstrap.connect(serverHost, serverPort) == null) {
			logger.warn(String.format("无法重连%s -> %s:%d", name, serverHost, serverPort));
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
				logger.error(e);
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
		logger.info(name + "注册成功,开始发送信息");
		this.session = session;
		__QUEUE__.clear();
		disConnect = false;
		doStart();
	}

	public void close() {
		logger.warn(String.format("%s关闭了 - %s:%d", name, serverHost, serverPort));
		logger.info(String.format("开始重连%s -> %s:%d", name, serverHost, serverPort));
		restart();
		logger.info(String.format("成功重连%s -> %s:%d", name, serverHost, serverPort));
	}
}
