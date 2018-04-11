package com.wanniu.core.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.wanniu.core.GConfig;
import com.wanniu.core.GServer;
import com.wanniu.core.game.request.GClientEvent;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * 逻辑数据包处理
 * @author agui
 */
public abstract class PacketDispatcher implements Runnable {

	private static final int __CAPACITY__ = GConfig.getInstance().getInt("client.dispatcher.capacity", 5000);

	private static final int __WARN_COUNT__ = __CAPACITY__ * 2 / 3;

	private static final boolean __MONITOR_ENABLE__ = GConfig.getInstance().getBoolean("client.monitor.enable", true);

	/** 存放Request对象的阻塞队列，包括客户端的请求消息及数据库线程的返回结果消息 */
	private final BlockingQueue<Packet> __QUEUE__ = new LinkedBlockingQueue<Packet>(__CAPACITY__);

	/** 当前分发器的所有处理类 */
	protected final Map<Short, NetHandler> handlers = new HashMap<Short, NetHandler>();
	protected final Map<String, NetHandler> s_handlers = new HashMap<String, NetHandler>();

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
		if (handler.getClass().isAnnotationPresent(GClientEvent.class)) {
			GClientEvent handle = handler.getClass().getAnnotation(GClientEvent.class);
			GServer.getInstance().addGlobalRoute(handle.value(), this);
			s_handlers.put(handle.value(), handler);
		} else {
			handlers.put(handler.getType(), handler);
		}
	}


	/**
	 * 向消息队列尾部添加一条请求处理的消息，调用此方法的线程不会阻塞 发生队列满等其它异常情况时，请求的消息将丢弃，同时输出error日志
	 * 
	 * @param packet
	 * @return
	 */
	public boolean add(Packet packet) {
		if (__running__) {
			if (__QUEUE__.size() > __WARN_COUNT__) {
				Out.info("队列偏大 -> " + __QUEUE__.size());
			}
			if (__QUEUE__.offer(packet)) {
				return true;
			} else {
				Out.error("队列过大，丢弃了请求：" + packet.getHeader().getTypeHexString());
				return false;
			}
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
		while (true) {
			try {
				packet = __QUEUE__.take();
				if (__MONITOR_ENABLE__) {
					sTime = System.currentTimeMillis();
					execute(packet);
					if (System.currentTimeMillis() - sTime > 100) {
						Out.warn(String.format("处理句柄【%s】耗时 -> %s", packet.getHeader().getTypeHexString(), System.currentTimeMillis() - sTime));
					}
				} else {
					execute(packet);
				}
			} catch (Exception e) {
				Out.error(String.format("处理句柄【%s】出错 -> %s", packet.getHeader().getTypeHexString(), e.toString()), e);
			}
		}
	}

	/**
	 * 处理请求包
	 * @param action
	 */
	public abstract void execute(Packet action);
	
}
