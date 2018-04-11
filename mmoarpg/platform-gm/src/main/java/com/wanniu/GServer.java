package com.wanniu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.gm.GHandlers;
import com.wanniu.gm.GServerBootstrap;
import com.wanniu.gm.LogicDispatcher;
import com.wanniu.gm.message.BroadcastMessage;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.util.Out;
import com.wanniu.vo.ServerVo;

import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.service.ServerService;
import io.netty.channel.Channel;

/**
 * 游戏环境 -> 存放游戏基础运行环境
 * @author agui
 */
public class GServer {

	public static final int __APP_ID = GConfig.getInstance().getInt("app.id", 0);

	/** 默认客户端逻辑性请求处理分发器 */
	private LogicDispatcher 					defaultDispatcher;
	/** 默认客户端逻辑处理线程 */
	private Thread								defaultHandleThread;

	/** 正在进入游戏的连接 */
	protected ConcurrentHashMap<Integer, Channel> 	channels;
	
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
		if(defaultHandleThread != null) return;
		
		initDispatcher();

		GServerBootstrap.getInstance().start();
		
		GSystem.free();
	}

	public BroadcastMessage stopMessage() {
		return null;
	}
	
	/** 分发消息处理 */
    public void addPacket(Packet packet){
		defaultDispatcher.add(packet);
    }

    /**
     * 注册客户端请求处理句柄
     */
	public void registerHandler(NetHandler handler) {
		Out.info("gm handler:", handler.getClass().getName());
		defaultDispatcher.registerHandler(handler);
	}

	public Map<Integer, Channel> getChannels() {
		return channels;
	}

	public Channel getChannel(int serverId) {
		return channels.get(serverId);
	}

	public void setState(Server server, ServerVo.State state) {
		server.setState(state.value);
	}
	
	public void addChannel(Channel channel) {
		Integer sid = channel.attr(GGlobal._KEY_SERVER_ID).get();
		channels.put(sid, channel);
		Server server = ServerService.getServer(sid);
		if (server != null) {
			setState(server, ServerVo.State.SMOOTH);
			Out.info(server.getServerName(), "开启了...");
		}
	}

	public void removeChannel(Channel channel) {
		Integer sid = channel.attr(GGlobal._KEY_SERVER_ID).get();
		if (sid != null) {
			channels.remove(sid);
			Server server = ServerService.getServer(sid);
			if (server != null) {
				setState(server, ServerVo.State.MAINTAIN);
				Out.info(server.getServerName(), "关闭了...");
			}
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