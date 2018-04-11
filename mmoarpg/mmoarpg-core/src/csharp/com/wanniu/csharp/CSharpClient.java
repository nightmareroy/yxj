package com.wanniu.csharp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.GGame;
import com.wanniu.core.GSystem;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.csharp.ice.XmdsManager;
import com.wanniu.csharp.ice.ZoneManager;
import com.wanniu.csharp.message.CSharpJoinMessage;
import com.wanniu.csharp.protocol.CSharpMessage;
import com.wanniu.csharp.protocol.CSharpPacket;

import Pomelo.ZoneManagerPrx;
import Xmds.XmdsManagerPrx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * CSharp战斗服客户端
 * 
 * @author agui
 */
public final class CSharpClient {

	private static final AttributeKey<CSharpNode> NodeKey = AttributeKey.valueOf("__KEY.CSharpNode__");

	private static CSharpClient instance;

	static {
		instance = new CSharpClient();
	}

	private CSharpBootstrap bootstrap = new CSharpBootstrap(new CSharpSessionHandler());

	// csharpServerId : session
	private static ConcurrentHashMap<String, Channel> csharpServers = new ConcurrentHashMap<String, Channel>();
	private static ConcurrentHashMap<String, ZoneManager> zoneManagers = new ConcurrentHashMap<String, ZoneManager>();
	private static ConcurrentHashMap<String, XmdsManager> nbManagers = new ConcurrentHashMap<String, XmdsManager>();

	public static CSharpClient getInstance() {
		return instance;
	}

	private CSharpClient() {}

	public void start() {
		connectAsync(GGame.__CS_NODE);
	}

	public void connectAsync(final CSharpNode node) {
		connectAsync(node, null);
	}

	public void connectAsync(final CSharpNode node, final Runnable cb) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				connect(node, cb);
			}
		}).start();
	}

	public void connect(final CSharpNode node, final Runnable cb) {
		if (!csharpServers.containsKey(node.getNodeId())) {
			Channel channel;
			while ((channel = bootstrap.connect(node.host, node.port)) == null) {
				GSystem.waitMills(5000);
				Out.warn(" reconnect ", node);
			}
			channel.attr(NodeKey).set(node);
			bind(channel);
		}
		if (cb != null) {
			cb.run();
		}
	}

	public static ZoneManagerPrx getZoneManager(String csharpServerId) {
		ZoneManager manager = zoneManagers.get(csharpServerId);
		if (manager != null) {
			return manager.getManager();
		}
		return null;
	}

	public static XmdsManagerPrx getXmdsManager(String csharpServerId) {
		XmdsManager manager = nbManagers.get(csharpServerId);
		if (manager != null) {
			return manager.getManager();
		}
		return null;
	}

	public void bind(Channel channel) {
		CSharpNode node = channel.attr(NodeKey).get();
		String nodeId = node.getNodeId();
		csharpServers.put(nodeId, channel);

		ZoneManager zoneManager = zoneManagers.get(nodeId);
		if (zoneManager != null)
			zoneManager.destory();
		zoneManager = new ZoneManager(node).bind();
		zoneManagers.put(nodeId, zoneManager);

		XmdsManager nbManager = nbManagers.get(nodeId);
		if (nbManager != null)
			nbManager.destory();
		nbManager = new XmdsManager(node).bind();
		nbManagers.put(nodeId, nbManager);

		channel.writeAndFlush(new CSharpJoinMessage(nodeId).getContent()).awaitUninterruptibly(3000);
	}

	public void close(Channel channel) {
		CSharpNode node = channel.attr(NodeKey).get();
		if (node != null) {
			String nodeId = node.getNodeId();
			Out.info(" connect ", node);
			csharpServers.remove(nodeId);
			GGame.getInstance().battleServerClose(nodeId);
			connectAsync(node);
		} else {
			Out.warn(" close ", channel.remoteAddress());
		}
	}

	public static class Watcher {
		public int count;
		public long buf;

		// 每秒统计的信息...
		public Map<Long, WatcherSecond> ws = new ConcurrentHashMap<>();

		public void inrc(ByteBuf buf) {
			if (!GGame.MONITOR) {
				return;
			}

			this.buf += buf.readableBytes();
			this.count++;

			ws.computeIfAbsent((System.currentTimeMillis() / 1000), key -> new WatcherSecond()).inrc(buf);
		}
	}

	public static class WatcherSecond {
		public int count;
		public long buf;

		public void inrc(ByteBuf buf) {
			this.buf += buf.readableBytes();
			this.count++;
		}
	}

	public static Watcher UP = new Watcher(); // 上行数据量
	public static Watcher DOWN = new Watcher(); // 下行数据量

	public void dispatch(CSharpMessage msg) {
		Channel channel = csharpServers.get(msg.getServerId());
		if (channel != null) {
			UP.inrc(msg.getContent());
			channel.writeAndFlush(msg.getContent());
		} else {
			Out.error("dispatch - ", msg.getServerId(), " & ", msg.getHeader().getUid());
		}
	}

	public void handle(final CSharpPacket pak) {
		String rid = pak.getHeader().getUid();
		GPlayer player = GGame.getInstance().getPlayer(rid);
		if (player == null)
			return;
		PomeloPush push = new PomeloPush() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(pak.getBody().array());
			}

			@Override
			public String getRoute() {
				return "area.playerPush.battleEventPush";
			}
		};
		push.getHeader().compressRoute = true;
		DOWN.inrc(push.getContent());
		Channel session = player.getSession();
		if (session != null) {
			session.writeAndFlush(push.getContent());
		}
	}

	/**
	 * GM命令，测试断开战斗服
	 */
	public void gmTestClose() {
		csharpServers.values().forEach(v -> v.close());
	}
}
