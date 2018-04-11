package com.wanniu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.pay.GHandlers;
import com.wanniu.pay.GServerBootstrap;
import com.wanniu.pay.LogicDispatcher;
import com.wanniu.pay.message.BroadcastMessage;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

import io.netty.channel.Channel;

/**
 * 游戏环境 -> 存放游戏基础运行环境
 * 
 * @author agui
 */
public class GServer {
	private final static Logger logger = LogManager.getLogger(GServer.class);
	/** 默认客户端逻辑性请求处理分发器 */
	private LogicDispatcher defaultDispatcher;
	/** 默认客户端逻辑处理线程 */
	private Thread defaultHandleThread;

	/** 正在进入游戏的连接 */
	protected ConcurrentHashMap<Integer, Channel> channels;

	/** 单例 */
	protected static GServer instance;

	/** 获取单例 */
	public static GServer getInstance() {
		if (instance == null) {
			synchronized (GServer.class) {
				if (instance == null) {
					instance = new GServer();
				}
			}
		}
		return instance;
	}

	protected GServer() {
		channels = new ConcurrentHashMap<Integer, Channel>();
	}

	private void initDispatcher() {

		defaultDispatcher = new LogicDispatcher();
		defaultHandleThread = new Thread(defaultDispatcher, "默认逻辑处理器");
		defaultHandleThread.start();

		externalDispatcher();

		GHandlers.init(handlerPath());
	}

	/** 扩展分发器 */
	protected void externalDispatcher() {

	}

	protected String handlerPath() {
		return instance.getClass().getPackage().getName();
	}

	/** 启动 */
	public void start() {
		if (defaultHandleThread != null)
			return;

		initDispatcher();

		GServerBootstrap.getInstance().start();

		GSystem.free();
	}

	public BroadcastMessage stopMessage() {
		return null;
	}

	/** 分发消息处理 */
	public void addPacket(Packet packet) {
		defaultDispatcher.add(packet);
	}

	/**
	 * 注册客户端请求处理句柄
	 */
	public void registerHandler(NetHandler handler) {
		logger.info("gm handler:{}", handler.getClass().getName());
		defaultDispatcher.registerHandler(handler);
	}

	public Map<Integer, Channel> getChannels() {
		return channels;
	}

	public Channel getChannel(int serverId) {
		return channels.get(serverId);
	}

	public void addChannel(Channel channel) {
		Integer sid = channel.attr(GGlobal._KEY_SERVER_ID).get();
		channels.put(sid, channel);
		logger.info("{} 开启了...", sid);
	}

	public void removeChannel(Channel channel) {
		Integer sid = channel.attr(GGlobal._KEY_SERVER_ID).get();
		if (sid != null) {
			channels.remove(sid);
			logger.info("{} 关闭了...", sid);
		}
	}

	public void shutdown() {
		defaultDispatcher.stop();
		defaultDispatcher.add(new Packet(null));
		channels.clear();
		GServerBootstrap.getInstance().shutDown();
		instance = null;
	}

}