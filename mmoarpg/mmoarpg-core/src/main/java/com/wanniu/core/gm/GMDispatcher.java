package com.wanniu.core.gm;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

/**
 * GM相关数据包分发及处理
 * @author agui
 */
class GMDispatcher implements Runnable {

	private static final int HIGH_WATER = 500;
	
	/** 处理消息队列 */
	private static BlockingQueue<Packet> 	__QUEUE__ = new LinkedBlockingQueue<Packet>();

	/** 所有处理类 */
	Map<Short, NetHandler> 				handlers = new ConcurrentHashMap<Short, NetHandler>();

	public GMDispatcher() {
	}

	/**
	 * 注册登陆服务请求处理句柄
	 * 
	 * @param handler
	 */
	public void registerHandler(NetHandler handler) {
		if (handlers.containsKey(handler.getType())) {
			Out.warn("register more gm handler : 0x", Integer.toHexString(handler.getType()));
		}
		handlers.put(handler.getType(), handler);
	}

	public void add(Packet mo) {
		__QUEUE__.add(mo);
		if (__QUEUE__.size() > HIGH_WATER) {
			// 添加报警方法
			Out.info("GM服务处理队列太长: " , __QUEUE__.size());
		}
	}

	public final void run() {
		while (true) {
			try {
				Packet msg = __QUEUE__.take();
				execute(msg);
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

	public void execute(Packet action) {
		NetHandler handler = handlers.get(action.getPacketType());
		if (handler != null) {
			handler.execute(action);
		} else {
			Out.warn("GM系统未找到对应的消息处理句柄：" , action.getHeader().getTypeHexString());
		}
	}

}
