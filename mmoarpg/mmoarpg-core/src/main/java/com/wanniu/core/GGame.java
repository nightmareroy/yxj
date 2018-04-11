package com.wanniu.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.db.GDao;
import com.wanniu.core.db.connet.DBClient;
import com.wanniu.core.game.BattleDispatcher;
import com.wanniu.core.game.GHandlers;
import com.wanniu.core.game.LogicDispatcher;
import com.wanniu.core.game.PoolFactory;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.message.BroadcastMessage;
import com.wanniu.core.game.protocol.PomeloHeader;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.gm.GMClient;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.pay.PayClient;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.tcp.PacketDispatcher;
import com.wanniu.core.tcp.client.ClientWorker;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.StringUtil;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.csharp.CSharpClient.WatcherSecond;
import com.wanniu.csharp.CSharpNode;

import io.netty.channel.Channel;

/**
 * 游戏环境 -> 存放游戏基础运行环境
 * 
 * @author agui
 */
public class GGame {
	/** 游戏世界的时间 */
	public static volatile long APP_TIME = System.currentTimeMillis();

	/** 应用编号 */
	public static int __APP_ID = GConfig.getInstance().getAppID();
	/** 服务编号 */
	public static int __SERVER_ID = GConfig.getInstance().getGameID();

	/** 战斗服节点 */
	public static CSharpNode __CS_NODE = new CSharpNode("game-" + __SERVER_ID, GConfig.getInstance().get("battle.ice.host"), GConfig.getInstance().getInt("battle.fastStream.port"), GConfig.getInstance().getInt("battle.ice.port"));

	/** 服务语言 */
	public static XLang __SERVER_LANG = XLang.CN;

	/** 是否跨服 */
	public static boolean GATE_ENABLE = false;

	/** 是否测试环境 */
	public static boolean DEBUG = GConfig.getInstance().getBoolean("game.debug");

	/** 是否进行协议监控 */
	public static boolean MONITOR = GConfig.getInstance().getBoolean("game.monitor");

	/** 允许的最大在线玩家数 */
	public static int PLAYER_LIMIT = GConfig.getInstance().getPlayerLimit();

	/** 客户端战斗性请求处理分发器 */
	private BattleDispatcher battleDispatcher;
	/** 客户端战斗处理线程 */
	private Thread battleHandleThread;
	/** 默认客户端逻辑性请求处理分发器 */
	private PacketDispatcher defaultDispatcher;

	/** 其它逻辑分发器 */
	private PacketDispatcher[] dispatchers = new PacketDispatcher[GConfig.getInstance().getInt("game.dispatcher.count", 3)];
	private Thread[] threads = new Thread[dispatchers.length];

	/** 本服所有在线玩家 */
	protected ConcurrentHashMap<String, GPlayer> onlinePlayers;
	/** 等待离线的玩家 */
	protected ConcurrentHashMap<String, GPlayer> waitPlayers;
	/** 正在进入游戏的连接 */
	protected ConcurrentHashMap<Integer, Channel> loginPlayers;

	/** 服务启动时间 */
	protected long startServerTime = System.currentTimeMillis();

	protected final Map<String, PacketDispatcher> handlers = new HashMap<String, PacketDispatcher>();

	/** 异步业务执行器 */
	protected Executor ansycExec;

	public void ansycExec(final Runnable command) {
		ansycExec.execute(new Runnable() {
			@Override
			public void run() {
				try {
					command.run();
				} catch (Exception e) {
					Out.error(e);
				}
			}
		});
	}

	/** 单例 */
	protected static GGame instance;

	/** 获取单例 */
	public static GGame getInstance() {
		if (instance == null) {
			synchronized (GGame.class) {
				if (instance == null) {
					instance = new GGame();
				}
			}
		}
		return instance;
	}

	{
		String lang = GConfig.getInstance().getGameLang();
		if ("vn".equalsIgnoreCase(lang)) {
			Locale.setDefault(Locale.ENGLISH);
			__SERVER_LANG = XLang.VN;
		} else {
			Locale.setDefault(Locale.CHINA);
			__SERVER_LANG = XLang.CN;
		}
	}

	protected GGame() {

		onlinePlayers = new ConcurrentHashMap<String, GPlayer>(512);
		waitPlayers = new ConcurrentHashMap<String, GPlayer>();
		loginPlayers = new ConcurrentHashMap<Integer, Channel>();
		ansycExec = new ThreadPoolExecutor(GConfig.getInstance().getInt("async.thread.min", 1), GConfig.getInstance().getInt("async.thread.max", 5), GConfig.getInstance().getInt("async.thread.idle", 30), TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PoolFactory("异步服务"));

		long initialDelay = GConfig.getInstance().getAutoSaveDelay();
		GSystem.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				Out.debug("定时持久化用户数据.......");
				Collection<GPlayer> players = onlinePlayers.values();
				for (GPlayer player : players) {
					if (player.getUid() != null) {
						try {
							player.sync();
							if (++count % 200 == 0) {
								GSystem.waitSeconds(1);
							}
						} catch (Exception e) {
							Out.error(e);
						}
					}
				}
			}
		}, initialDelay, GConfig.getInstance().getAutoSaveInterval(), TimeUnit.SECONDS);
	}

	private void initDispatcher() {
		for (int module = 0; module < dispatchers.length; module++) {
			dispatchers[module] = new LogicDispatcher();
			threads[module] = new Thread(dispatchers[module], "logic-line" + Integer.toHexString(module));
			threads[module].start();
		}
		defaultDispatcher = dispatchers[0];
		threads[0].setName("logic-default");

		externalDispatcher();

		String searchPath = searchPath();
		if (searchPath != null) {
			String[] paths = searchPath.split(";");
			for (String path : paths) {
				GHandlers.init(path);
			}
		}

		int totalHandler = 0;
		for (int module = 0; module < dispatchers.length; module++) {
			int handlerCount = dispatchers[module].getHandlerCount();
			System.out.println(threads[module].getName() + " handler count : " + handlerCount);
			if (DEBUG || Out.isEnableDebug()) {
				dispatchers[module].echo();
			}
			totalHandler += handlerCount;
		}
		System.out.println("total handler count : " + totalHandler + ", glabal count : " + handlers.size());
	}

	/** 扩展分发器 */
	protected void externalDispatcher() {

	}

	protected String searchPath() {
		return instance.getClass().getPackage().getName();
	}

	/** 启动 */
	public void start() {

		initDispatcher();

		onBeginStart();

		{
			DBClient.getInstance().start();
			PayClient.getInstance().start();
			ProxyClient.getInstance().start();
			CSharpClient.getInstance().start();
			GMClient.getInstance().start();
		}

		onWorkerWatch();

		addCloseProcess();

		onAfterStart();
	}

	private final Set<ClientWorker> workers = new CopyOnWriteArraySet<ClientWorker>();

	public void onWorkerBefore(ClientWorker worker) {
		Out.info("on worker before : ", worker.getName());
		workers.add(worker);
	}

	protected void onWorkerWatch() {
		while (workers.size() > 0) {
			for (ClientWorker worker : workers) {
				Out.info(worker.getName(), " is waiting...");
			}
			GSystem.waitMills(3000);
		}
	}

	public void onWorkerReady(ClientWorker worker) {
		Out.info("on worker ready : ", worker.getName());
		workers.remove(worker);
	}

	protected void onBeginStart() {
		Out.info("begin start...");
	}

	protected void onAfterStart() {
		Out.info("after start...");
		final int maintain_player_time = GConfig.getInstance().getInt("game.player.maintain", 60000);
		GSystem.addFixedRateJob(new Runnable() {
			@Override
			public void run() {
				if (waitPlayers.size() > 0) {
					long maintain_time = System.currentTimeMillis() - maintain_player_time;
					Collection<GPlayer> wPlayers = waitPlayers.values();
					for (GPlayer player : wPlayers) {
						if (player.getLogoutTime() == null || player.getLogoutTime().getTime() < maintain_time) {
							try {
								onRemoveWaitPlayer(player);
							} catch (Exception e) {
								Out.error(e);
								removeWaitPlayer(player);
							}
						}
					}
				}
			}
		}, maintain_player_time, maintain_player_time);

		if (GGame.MONITOR) {
			GSystem.addFixedRateJob(new Runnable() {
				@Override
				public void run() {
					echoMonitor();
				}
			}, 1000, 1000);
		}
	}

	private void removeWaitPlayer(GPlayer player) {
		try {
			waitPlayers.remove(player.getId());
			// player.sync();
		} catch (Exception ex) {
			Out.error(ex);
		} finally {
			player.free();
			player.endWatch();
		}
	}

	protected void onRemoveWaitPlayer(GPlayer player) {
		Out.debug("maintain remove wait : ", player.getName());
		removeWaitPlayer(player);
	}

	public void onSessionClose(Channel channel) {}

	protected void onCloseGame() {
		Out.info("close game...");
	}

	protected BroadcastMessage stopMessage() {
		return null;
	}

	/** 关闭程序时保存在线玩家的数据 */
	private void addCloseProcess() {
		Runtime.getRuntime().addShutdownHook(new Thread("程序维护") {
			public void run() {
				Out.info("正在进行安全停服中...");
				try {
					if (battleDispatcher != null) {
						battleDispatcher.stop();
					}
					for (PacketDispatcher dispatcher : dispatchers) {
						dispatcher.stop();
					}
					// 通知维护
					BroadcastMessage stopMsg = stopMessage();
					if (stopMsg != null) {
						for (GPlayer player : onlinePlayers.values()) {
							player.receive(stopMsg);
						}
					}
					onCloseGame();
				} finally {
					try {
						sync();
					} finally {
						while (GDao.size() > 0) {
							Out.info(String.format("等待数据保存到数据库，还有【%d】条记录！", GDao.size()));
							GSystem.waitMills(500);
						}
					}
				}
				Out.info("服务器已安全停止，可以继续执行后续的工作了，O(∩_∩)O~");
				Out.shutdown();
			}
		});
	}

	/** 分发消息处理 */
	public void addPacket(Packet packet) {
		short type = packet.getHeader().getType();
		int module = type >> 8;
		if (module < dispatchers.length) {
			dispatchers[module].add(packet);
		} else {
			defaultDispatcher.add(packet);
		}
	}

	public void addBattlePacket(Packet packet) {
		battleDispatcher.add(packet);
	}

	public void registerBattleHandler(NetHandler handler) {
		if (battleDispatcher == null) {
			battleDispatcher = new BattleDispatcher();
			battleHandleThread = new Thread(battleDispatcher, "战斗逻辑处理器");
			battleHandleThread.start();
		}
		battleDispatcher.registerHandler(handler);
	}

	public void addDefaultPacket(Packet packet) {
		defaultDispatcher.add(packet);
	}

	public void registerDefaultHandler(NetHandler handler) {
		defaultDispatcher.registerHandler(handler);
	}

	public void addGlobalRoute(String route, PacketDispatcher dispatcher) {
		if (handlers.containsKey(route)) {
			Out.error("more register handler : ", route);
		}
		handlers.put(route, dispatcher);
	}

	public void putGlobalRoute(String route, Packet packet) {
		PacketDispatcher dispatcher = handlers.get(route);
		if (dispatcher != null) {
			dispatcher.add(packet);
		} else {
			Out.error("未实现的协议::::::::::::::::::::::::::::::::::::::::::::::::::::::" + route);
		}
	}

	public void addClassBySearchPath(String className) {
		try {
			Class<?> clz = Class.forName(className);
			if (clz.isAnnotationPresent(GClientEvent.class)) {
				registerHandler((NetHandler) clz.newInstance());
			}
			addClassByAnnotation(clz);
		} catch (Exception e) {
			Out.error(e);
		}
	}

	protected void addClassByAnnotation(Class<?> clz) throws Exception {

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
			defaultDispatcher.registerHandler(handler);
		}
	}

	public void addPlayer(GPlayer player) {
		this.onlinePlayers.put(player.getId(), player);
		this.waitPlayers.remove(player.getId());
	}

	public GPlayer getPlayer(String rid) {
		return this.onlinePlayers.containsKey(rid) ? this.onlinePlayers.get(rid) : this.waitPlayers.get(rid);
	}

	public GPlayer getPlayerByName(String name) {
		if (StringUtil.isEmpty(name))
			return null;
		for (Map.Entry<String, GPlayer> node : onlinePlayers.entrySet()) {
			GPlayer player = node.getValue();
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	/** 根据账号查找玩家 */
	public GPlayer getPlayerByUid(String uid) {
		if (StringUtil.isEmpty(uid))
			return null;

		for (GPlayer p : onlinePlayers.values()) {
			if (p.getUid().equals(uid)) {
				return p;
			}
		}

		for (GPlayer p : waitPlayers.values()) {
			if (p.getUid().equals(uid)) {
				return p;
			}
		}

		return null;
	}

	public void removePlayer(GPlayer player) {
		this.onlinePlayers.remove(player.getId());
	}

	public Map<String, GPlayer> getOnlinePlayers() {
		return onlinePlayers;
	}

	public Map<Integer, Channel> getLoginPlayers() {
		return loginPlayers;
	}

	public Channel getLoginSession(int id) {
		return loginPlayers.get(id);
	}

	public Map<String, GPlayer> getWaitPlayers() {
		return waitPlayers;
	}

	public void addWaitPlayer(GPlayer player) {
		waitPlayers.put(player.getId(), player);
	}

	/**
	 * 获得角色在线人数
	 */
	public int getOnlineCount() {
		return onlinePlayers.size();
	}

	/**
	 * 服务器是否满员
	 */
	public boolean isFull() {
		return onlinePlayers.size() >= PLAYER_LIMIT;
	}

	/**
	 * 获取服务器开启的时间
	 */
	public long getStartServerTime() {
		return startServerTime;
	}

	public PomeloResponse getErrResponse(Exception e) {
		Out.error("GGame getErrResponse().", e);
		return null;
	}

	/** 全服持久化处理（安全停服时也会触发） */
	protected void sync() {
		for (GPlayer player : onlinePlayers.values()) {
			player.free();
		}
		for (GPlayer player : waitPlayers.values()) {
			player.free();
		}
	}

	public void broadcast(Message msg) {
		for (GPlayer player : onlinePlayers.values()) {
			player.receive(msg);
		}
	}

	public void closeArea(String instanceId) {

	}

	public void onAcrossReceive(Packet packet) {
		Out.info("across handle packet...", packet.getHeader().getTypeHexString());
	}

	public void onPlayerDie(String instanceId, String diePlayerId, String hitPlayerId) {

	}

	/**
	 * 战斗服关闭
	 * 
	 * @param serverId 战斗服ID
	 */
	public void battleServerClose(String serverId) {

	}

	/**
	 * 战斗服开启
	 * 
	 * @param serverId 战斗服ID
	 */
	public void battleServerStart(String serverId) {

	}

	/**
	 * 战斗服时间推送
	 * 
	 * @param eventType 事件类型
	 * @param msg 消息结构
	 */
	public void battleServerEvent(String eventType, String msg) {

	}

	public void echoMonitor() {
		// System.out.println(Thread.currentThread().getName());
		if (CSharpClient.UP.count > 0 && CSharpClient.DOWN.count > 0) {
			// System.out.println("::: csharp :::");
			// Out.error("\tup:" + CSharpClient.UP.buf + " / " + CSharpClient.UP.count + " =
			// " + (CSharpClient.UP.buf / CSharpClient.UP.count));
			// Out.error("\tdown:" + CSharpClient.DOWN.buf + " / " + CSharpClient.DOWN.count
			// + " = " + (CSharpClient.DOWN.buf / CSharpClient.DOWN.count));
			Long t = System.currentTimeMillis() / 1000 - 3;
			{
				WatcherSecond w = CSharpClient.DOWN.ws.remove(t);
				if (w != null) {
					Out.error("CSharp stat down:", w.buf, " / ", w.count + " = ", (w.buf / w.count));
				}
			}
		}
		// System.out.println("::: handler :::");
		// for (Map.Entry<String, PacketDispatcher> entry : handlers.entrySet()) {
		// GameHandler.Watcher watcher = ((GameHandler)
		// entry.getValue().getHandler(entry.getKey())).watcher;
		// watcher.echo();
		// }
		//
		// long totalBuf = GPlayer.Buf;
		// long totalCount = GPlayer.Count;
		// for (GPlayer player : onlinePlayers.values()) {
		// totalBuf += player.watcher.buf;
		// totalCount += player.watcher.count;
		// // System.out.println("\t" + player.watcher.buf + " / " +
		// player.watcher.count);
		// }
		// if (totalCount > 0) {
		// System.out.println("::: player :::");
		// System.out.println("\t" + totalBuf + " / " + totalCount + " = " + totalBuf /
		// totalCount);
		// }
	}

	public void handleCopyPacket(GPlayer player, Integer count, PomeloHeader header) {}
}