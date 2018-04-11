package com.wanniu.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.GHandlers;
import com.wanniu.core.game.GServerBootstrap;
import com.wanniu.core.game.LogicDispatcher;
import com.wanniu.core.game.PoolFactory;
import com.wanniu.core.game.message.BroadcastMessage;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.PacketDispatcher;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;

import io.netty.channel.Channel;

/**
 * 游戏环境 -> 存放游戏基础运行环境
 * @author agui
 */
public class GServer {
	
	/** 语言类型 */
	public static enum XLang {
		CN, TW, EN, OTHER
	}

	/** 服务编号 */
	public static int __APP_ID = GConfig.getInstance().getInt("server.appid", 80);
	/** 服务编号 */
	public static int __SERVER_ID = GConfig.getInstance().getServerID();
	/** 服务语言 */
	public static XLang __SERVER_LANG = XLang.CN;
	
	/** 是否测试环境 */
	public static boolean DEBUG = GConfig.getInstance().getBoolean("game.debug");

	/** 其它逻辑分发器 */
	private PacketDispatcher[] 	dispatchers 	= new PacketDispatcher[GConfig.getInstance().getInt("dispatcher.count", 3)];
	private Thread[]			threads			= new Thread[dispatchers.length];

	/** 正在进入游戏的连接 */
	protected ConcurrentHashMap<String, Channel> 	loginChannels;

	/** 异步业务执行器 */
    protected Executor 								ansycExec;
    public void ansycExec(Runnable command) {
    	ansycExec.execute(command);
    }

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
	{
		String lang = GConfig.getInstance().getServerLang();
		if ("tw".equalsIgnoreCase(lang)) {
			Locale.setDefault(Locale.TAIWAN);
			__SERVER_LANG = XLang.TW;
		} else if ("en".equalsIgnoreCase(lang)) {
			Locale.setDefault(Locale.ENGLISH);
			__SERVER_LANG = XLang.EN;
		} else {
			Locale.setDefault(Locale.CHINA);
			__SERVER_LANG = XLang.CN;
		}
	}
	
	protected GServer() {
		loginChannels = new ConcurrentHashMap<String, Channel>();
		ansycExec = new ThreadPoolExecutor(
				GConfig.getInstance().getInt("async.thread.min", 1),
				GConfig.getInstance().getInt("async.thread.max", 5), 
				GConfig.getInstance().getInt("async.thread.idle", 30), TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new PoolFactory("异步服务"));
	}

	private void initDispatcher() {
		for (int module = 0; module < dispatchers.length; module++) {
			dispatchers[module] = new LogicDispatcher();
			threads[module] = new Thread(dispatchers[module], "dispatcher-module.0x" + Integer.toHexString(module));
			threads[module].start();
		}

		threads[0].setName("默认逻辑处理器");

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
		
		initDispatcher();

		GServerBootstrap.getInstance().start();
		
		Out.info("ENV -> ", __SERVER_LANG, "    DEBUG -> ", DEBUG);
		Out.info("游戏已成功启动运行喽，O(∩_∩)O~");
		
		closeProcess();
		
		GSystem.free();
	}

	public BroadcastMessage stopMessage() {
		return null;
	}

	/** 关闭程序时保存在线玩家的数据 */
	private void closeProcess() {
		Runtime.getRuntime().addShutdownHook(new Thread("程序维护") {
			public void run() {
				Out.info("正在进行安全停服中...");
				for (PacketDispatcher dispatcher : dispatchers) {
					dispatcher.stop();
				}
				BroadcastMessage stopMsg = stopMessage();
				if (stopMsg != null) {
					
				}
				// 保存角色数据
				GServer.getInstance().sync();
				Out.info("服务器已安全停止，可以继续执行后续的工作了，O(∩_∩)O~");
			}
		});
	}
	
	/** 分发消息处理 */
    public void addPacket(Packet packet){
		short type = packet.getHeader().getType();
		int module = type >> 8;
		if (module < dispatchers.length) {
			dispatchers[module].add(packet);
		} else {
			dispatchers[0].add(packet);
		}
    }

	private final Map<String, PacketDispatcher> Handlers = new HashMap<String, PacketDispatcher>();
	public void addGlobalRoute(String route, PacketDispatcher dispatcher) {
		Handlers.put(route, dispatcher);
	}
	public void putGlobalRoute(String route, Packet packet) {
		PacketDispatcher dispatcher = Handlers.get(route);
		if(dispatcher != null) {
			dispatcher.add(packet);
		} else {
			Out.error("未实现的协议::::::::::::::::::::::::::::::::::::::::::::::::::::::" + route);
		}
	}
    /**
     * 注册客户端请求处理句柄
     */
	public void registerHandler(NetHandler handler) {
		short type = handler.getType();
		int module = type >> 8;
		if (module < dispatchers.length) {
			dispatchers[module].registerHandler(handler);
		} else {
			dispatchers[0].registerHandler(handler);
		}
	}

	public Map<String, Channel> getLoginSessions() {
		return loginChannels;
	}

	public Channel getLoginSession(String uid) {
		return loginChannels.get(uid);
	}

	public void addLoginSession(Channel channel) {
		loginChannels.put(channel.attr(GGlobal._KEY_USER_ID).get(), channel);
	}

	public void onSessionClose(Channel session) {
		String uid = session.attr(GGlobal._KEY_USER_ID).get();
		if (uid != null && loginChannels.get(uid) == session) {
			loginChannels.remove(uid);
		}
		Out.debug(uid, "连接被关闭:" + session.remoteAddress().toString());
	}

	public void sync() {
		
	}
	
}