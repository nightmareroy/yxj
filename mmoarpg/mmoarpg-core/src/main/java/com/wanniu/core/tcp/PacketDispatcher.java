package com.wanniu.core.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.request.GameHandler;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Header;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.tcp.server.IPBlacks;

/**
 * 逻辑数据包处理
 * 
 * @author agui
 */
public abstract class PacketDispatcher implements Runnable {
	// 心跳包的协议结尾...
	private static final String GET_SYSTIME_REQUEST = "getSysTimeRequest";
	private static final int __CAPACITY__ = GConfig.getInstance().getInt("client.dispatcher.capacity", 100000);

	private static final int __WARN_COUNT__ = __CAPACITY__ * 2 / 3;

	private static final boolean __MONITOR_ENABLE__ = GConfig.getInstance().getBoolean("client.monitor.enable", true);

	/** 存放Request对象的阻塞队列，包括客户端的请求消息及数据库线程的返回结果消息 */
	private final BlockingQueue<Packet> __QUEUE__ = new LinkedBlockingQueue<Packet>(__CAPACITY__);

	/** 当前分发器的所有处理类 */
	protected final Map<Short, NetHandler> handlers = new HashMap<Short, NetHandler>();
	protected final Map<String, NetHandler> s_handlers = new HashMap<String, NetHandler>();

	public int getHandlerCount() {
		return handlers.size() + s_handlers.size();
	}

	public void echo() {
		for (Short key : handlers.keySet()) {
			System.out.println("\t" + Integer.toHexString(key.intValue()));
		}
		for (Map.Entry<String, NetHandler> key : s_handlers.entrySet()) {
			System.out.println("\t" + (key.getValue().getType() != 0 ? "0x" + Integer.toHexString(key.getValue().getType()) + " : " : "") + key.getKey());
		}
	}

	/** 是否运行 */
	private boolean __running__ = true;

	public NetHandler getHandler(String route) {
		return s_handlers.get(route);
	}

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
			GGame.getInstance().addGlobalRoute(handle.value(), this);
			s_handlers.put(handle.value(), handler);
			((GameHandler) handler).watcher.handlerName = handle.value();
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
				Out.info("队列偏大 -> ", __QUEUE__.size());
			}
			if (__QUEUE__.offer(packet)) {
				return true;
			} else {
				Out.error("队列过大，丢弃了请求：", packet.getHeader().getTypeHexString());
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
		while (true) {
			try {
				packet = __QUEUE__.take();
				if (__MONITOR_ENABLE__) {
					// 开始执行的时间...
					long startExecuteTime = System.nanoTime();

					Header header = packet.getHeader();
					execute(packet);

					// 执行结束的时间...
					long endExecuteTime = System.nanoTime();

					float delay = (startExecuteTime - header.getReceiveTime()) / 100_0000F;
					float exec = (endExecuteTime - startExecuteTime) / 100_0000F;

					String protocal = header.getTypeHexString();
					if (Out.isEnableDebug() || !protocal.endsWith(GET_SYSTIME_REQUEST)) {
						GPlayer player = packet.getPlayer();
						Out.info("handle ", protocal, ", delay=", delay, " ms, exec=", exec, " ms, playerId=", player == null ? 0 : player.getId());
					}
				} else {
					execute(packet);
				}
			} catch (Exception e) {
				Out.error(String.format("处理句柄【%s】出错 -> %s", packet.getHeader().getTypeHexString(), e.toString()), e);
				IPBlacks.getInstance().exceptionIp(packet.getSession());
			}
		}
	}

	/**
	 * 处理请求包
	 * 
	 * @param action
	 */
	public abstract void execute(Packet action);

}
