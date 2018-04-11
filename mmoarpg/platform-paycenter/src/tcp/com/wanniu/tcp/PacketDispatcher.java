package com.wanniu.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GConfig;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

/**
 * 逻辑数据包处理
 * 
 * @author agui
 */
public abstract class PacketDispatcher implements Runnable {
	private final static Logger logger = LogManager.getLogger(PacketDispatcher.class);
	private static final int __CAPACITY__ = GConfig.getInstance().getInt("client.dispatcher.capacity", 5000);

	private static final int __WARN_COUNT__ = __CAPACITY__ * 2 / 3;

	private static final boolean __MONITOR_ENABLE__ = GConfig.getInstance().getBoolean("client.monitor.enable", true);

	/** 存放Request对象的阻塞队列，包括客户端的请求消息及数据库线程的返回结果消息 */
	private final BlockingQueue<Packet> __QUEUE__ = new LinkedBlockingQueue<Packet>(__CAPACITY__);

	/** 当前分发器的所有处理类 */
	protected final Map<Short, NetHandler> handlers = new HashMap<Short, NetHandler>();

	/** 是否运行 */
	private boolean __running__ = true;

	/**
	 * 构造
	 */
	public PacketDispatcher() {

	}

	/**
	 * 注册处理类
	 * 
	 * @param dependKey
	 * @param handler
	 */
	public void registerHandler(NetHandler handler) {
		handlers.put(handler.getType(), handler);
	}

	/**
	 * 向消息队列尾部添加一条请求处理的消息，调用此方法的线程不会阻塞 发生队列满等其它异常情况时，请求的消息将丢弃，同时输出error日志
	 * 
	 * @param packet
	 * @return
	 */
	public boolean add(Packet packet) {
		if (__QUEUE__.size() > __WARN_COUNT__) {
			logger.info("队列偏大 -> " + __QUEUE__.size());
		}
		if (__QUEUE__.offer(packet)) {
			return true;
		} else {
			logger.error("队列过大，丢弃了请求：" + packet.getHeader().getTypeHexString());
		}
		return false;
	}

	/**
	 * 设置关闭
	 */
	public void stop() {
		__running__ = false;
	}

	public final void run() {
		Packet packet = null;
		long sTime = 0L;
		while (__running__) {
			try {
				packet = __QUEUE__.take();
				if (__MONITOR_ENABLE__) {
					sTime = System.currentTimeMillis();
					execute(packet);
					if (System.currentTimeMillis() - sTime > 100) {
						logger.warn("处理句柄【{}】耗时 -> {}", packet.getHeader().getTypeHexString(), System.currentTimeMillis() - sTime);
					}
				} else {
					execute(packet);
				}
			} catch (Exception e) {
				logger.error("处理句柄【{}】出错 -> {}", packet.getHeader().getTypeHexString(), e.toString(), e);
			}
		}
		__QUEUE__.clear();
	}

	/**
	 * 处理请求包
	 * 
	 * @param action
	 */
	public abstract void execute(Packet action);

}
