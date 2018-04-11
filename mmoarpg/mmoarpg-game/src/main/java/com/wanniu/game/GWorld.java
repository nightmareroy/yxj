package com.wanniu.game;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.message.BroadcastMessage;
import com.wanniu.core.game.protocol.PomeloHeader;
import com.wanniu.core.game.protocol.PomeloPacket;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.gm.GMClient;
import com.wanniu.core.gm.request.GMHandler;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.login.AuthServer;
import com.wanniu.core.pay.PayClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.DateUtils;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.csharp.protocol.CSharpMessage;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.area.AreaMap;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.auction.AuctionDataManager;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.guildFort.GuildFortCenter;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.ProxyUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.WNProxy;
import com.wanniu.game.player.WNRobot;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.request.entry.LoginQueue;
import com.wanniu.game.request.prepaid.PaySuccessHandler;
import com.wanniu.game.revelry.RevelryManager;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMRequest;
import com.wanniu.gm.handler.GMBaseHandler;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import cn.qeng.common.login.TokenInfo;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import pomelo.area.PlayerHandler.KickPlayerPush;

/**
 * @author agui
 */
public class GWorld extends GGame {

	/**
	 * 开服日期.
	 */
	public static LocalDate OPEN_SERVER_DATE;

	public static int __ACROSS_SERVER_ID = __SERVER_ID;

	public static boolean __NEW = GConfig.getInstance().getBoolean("game.new", false);
	public static boolean __RECOMMEND = GConfig.getInstance().getBoolean("game.recommend", false);
	public static boolean __HOT = GConfig.getInstance().getBoolean("game.hot", false);

	public static int __AREA_ID = GConfig.getInstance().getInt("game.areaId", 0);

	public static String __SERVER_NAME = GConfig.getInstance().get("game.name");

	public static int __PLAYER_LIMIT = GConfig.getInstance().getInt("game.limit", 1000);

	public static int __SHOW = 2;
	/**
	 * 服务器对外的时间
	 */
	public static LocalDateTime __EXTERNAL_TIME = LocalDateTime.now();

	public static boolean ROBOT = GConfig.getInstance().getBoolean("game.tool.debug", false);

	// 所有合服的编号...
	public static Set<Integer> sids = GConfig.getInstance().getSet("game.sid.list");

	public static boolean ROBOTS = true;

	public static final Map<String, WNRobot> Robots = new ConcurrentHashMap<>();
	public static final Map<String, WNProxy> Proxys = new ConcurrentHashMap<>();

	public static int ROBOT_MAX_LEVEL = 39;

	public void setOlAvgTime() {

	}

	static {
		instance = new GWorld();
	}

	public static GWorld getInstance() {
		return (GWorld) instance;
	}

	public boolean isOnline(String rid) {
		return onlinePlayers.containsKey(rid) || Robots.containsKey(rid) || Proxys.containsKey(rid);
	}

	public boolean isLocal(String rid) {
		return onlinePlayers.containsKey(rid) || waitPlayers.containsKey(rid) || Robots.containsKey(rid);
	}

	public WNPlayer getPlayer(String rid) {
		WNPlayer player = (WNPlayer) super.getPlayer(rid);
		if (player == null) {
			player = getRobot(rid);
			if (player == null) {
				player = getProxy(rid);
			}
		}
		return player;
	}

	public static WNRobot getRobot(String rid) {
		return Robots.get(rid);
	}

	public static WNProxy getProxy(String rid) {
		return Proxys.get(rid);
	}

	public PomeloResponse getErrResponse(Exception e) {
		Out.error("处理逻辑异常.", e);
		return new ErrorResponse(e.getMessage());
	}

	public void addPacket(Packet packet) {
		PomeloPacket pak = (PomeloPacket) packet;
		PomeloHeader header = pak.getHeader();
		if ("area.playerHandler.battleEventNotify".equals(header.route_s)) {
			CSharpClient.getInstance().dispatch(new CSharpMessage(pak));
		} else {
			putGlobalRoute(header.route_s, packet);
		}
	}

	public void broadcast(Message msg, int logicServerId) {
		broadcast(msg);
	}

	public void addClassByAnnotation(Class<?> clz) throws Exception {
		super.addClassByAnnotation(clz);
		if (clz.isAnnotationPresent(GMEvent.class)) {
			GMRequest.addHandler((GMBaseHandler) clz.newInstance());
		} else if (clz.getSuperclass() == GMHandler.class) {
			GMClient.getInstance().registerHandler((GMHandler) clz.newInstance());
		}
	}

	@Override
	protected String searchPath() {
		return super.searchPath() + ";" + GMRequest.class.getPackage().getName();
	}

	// 战斗服关闭
	public void battleServerClose(String serverId) {
		AreaMap areas = AreaManager.getInstance().getAreaMap();
		for (Area area : areas.values()) {
			if (area.getServerId().equals(serverId)) {
				area.bsClose = true;
				AreaManager.getInstance().closeArea(area.instanceId);
				Out.info(serverId, " 战斗服关闭  : ", area.instanceId, " : ", area.areaId, " : ", area.prop.name);
			}
		}
	}

	// 战斗服开启
	public void battleServerStart(String serverId) {
		AreaMap areas = AreaManager.getInstance().getAreaMap();
		for (Area area : areas.values()) {
			if (area.getServerId().equals(serverId)) {
				// TODO 判断是否存在实例
				// CSharpClient.getZoneManager(serverId);
				// area.bsClose = false;
				areas.remove(area.instanceId);
			}
		}
	}

	/*****************************************
	 * All of events from battleServerEvent
	 *****************************************/
	public void battleServerEvent(String eventType, String msg) {
		Out.debug(eventType, " : ", msg);
		switch (eventType) {
		case "areaEvent":
		case "zoneEvent": {
			AreaManager.getInstance().areaBattleServerEvent(JSON.parseObject(msg));
			return;
		}
		case "playerEvent": {
			AreaManager.getInstance().playerBattleServerEvent(JSON.parseObject(msg));
			return;
		}
		case "taskEvent": {
			AreaManager.getInstance().taskBattleServerEvent(JSON.parseObject(msg));
			return;
		}
		}
	}

	private JSONObject newServerJSON() {
		JSONObject json = new JSONObject();
		json.put("appId", __APP_ID);
		json.put("logicServerId", __SERVER_ID);
		json.put("acrossServerId", __ACROSS_SERVER_ID);
		return json;
	}

	private void updateLoginState(int load) {
		JSONObject json = newServerJSON();
		json.put("type", 0x103);
		json.put("logicServerId", __SERVER_ID);
		json.put("load", load);
		AuthServer.publish(json);
	}

	// 完成启动对内开放
	public void onAfterStart() {
		super.onAfterStart();
		afterGame();
		if (!GConfig.getInstance().isEnableGm()) {
			syncServerInfo();
		}
	}

	public void syncServerInfo() {
		JSONObject json = newServerJSON();
		json.put("type", 0x101);
		json.put("proto", "game");
		json.put("name", __SERVER_NAME);
		json.put("areaId", __AREA_ID);
		json.put("recommend", __RECOMMEND);
		json.put("new", __NEW);
		json.put("hot", __HOT);
		json.put("olLimit", __PLAYER_LIMIT);
		json.put("show", __SHOW);
		json.put("host", GConfig.getInstance().getGamePubHost());
		json.put("port", GConfig.getInstance().getGamePort());
		json.put("sidList", GWorld.sids);// 合服列表.
		AuthServer.publish(json);
	}

	// 对外开放
	private void afterGame() {

		PayClient.getInstance().registerHandler(new PaySuccessHandler());

		AreaManager.getInstance().init();

		// 每分钟统计一次在线人数用于Redis订阅发布，登录服需要此数据.
		JobFactory.addFixedRateJob(() -> {
			int onlineCount = getOnlineCount();
			updateLoginState(onlineCount);
			Out.info("在线人数 : ", onlinePlayers.size(), "/", onlineCount, " - ", waitPlayers.size());
			LogReportService.getInstance().ansycReportOnline(onlinePlayers.size(), Robots.size());
		}, 60000, 60000);

		// 每5分钟一次上报BI
		JobFactory.addFixedRateJob(() -> {
			BILogService.getInstance().ansycReportOnline(onlinePlayers.size());
		}, 60000, 5L * 60 * 1000);
	}

	public void onCloseGame() {
		super.onCloseGame();
		updateLoginState(-1);
		AreaManager.getInstance().onCloseGame();
		PlayerPOManager.onCloseGame();
		GuildFortCenter.getInstance().onCloseGame();
		GuildDao.onCloseGame();
		ConsignmentLineService.getInstance().onCloseGame();
		AuctionDataManager.getInstance().onCloseGame();
		
		// 对机器人也要进行回收
		this.disponseRobots();
	}

	private void disponseRobots() {
		for (WNRobot robot : Robots.values()) {
			GameDao.freeName(robot.getName());
			GCache.expire(robot.getId(), 5 * 60);
			Out.info("停机维护时，回收机器人资源 robotId=", robot.getId(), ",robotName=", robot.getName());
		}
	}

	@Override
	protected BroadcastMessage stopMessage() {
		return new BroadcastMessage() {
			@Override
			protected void write() throws IOException {
				KickPlayerPush.Builder data = KickPlayerPush.newBuilder();
				data.setS2CReasonType(KickReason.SERVER_SHUT_DOWN.value);
				body.writeBytes(data.build().toByteArray());
			}

			@Override
			public String getRoute() {
				return "area.playerPush.kickPlayerPush";
			}
		};
	}

	public void onRemoveWaitPlayer(GPlayer player) {
		WNPlayer wnPlayer = (WNPlayer) player;
		if (wnPlayer.soloManager.isBusy() || wnPlayer.arenaManager.isInArena()) {
			return;
		}

		Area area = wnPlayer.getArea();
		// 追加条件判断是否从游戏中移除
		if (area != null && !area.isClose() && !area.isNormal()) {
			return;
		}

		super.onRemoveWaitPlayer(player);
	}

	public void onSessionClose(Channel channel) {
		LoginQueue.remove(channel);

		String token = channel.attr(GGlobal.__KEY_TOKEN).get();
		if (token != null) {
			String uid = channel.attr(GGlobal.__KEY_USER_ID).get();
			int second = 5 * GGlobal.TIME_MINUTE_SECOND;
			Attribute<Boolean> timeout = channel.attr(GGlobal.__KEY_SESSION_TIMEOUT);
			if (timeout.get() != null && timeout.get()) {
				second = 90 * GGlobal.TIME_MINUTE_SECOND;
			}

			// 修正Token时间
			TokenInfo tokenInfo = channel.attr(GGlobal.__KEY_TOKEN_INFO).get();
			AuthServer.put(token, JSON.toJSONString(tokenInfo), second);
			Out.info("Session关闭，重写Token uid=", uid, ",token=", token, ",second=", second);
		}
	}

	public void onAcrossReceive(Packet pak) {
		Out.debug("onAcrossReceive :: ", pak.getHeader().getTypeHexString(), " : ", pak.remaing());
		if (pak.getPacketType() == ProxyType.PLAYER_EVENT) {
			ProxyUtil.onAcrossPlayerEvent(pak);
		} else if (pak.getPacketType() == ProxyType.PLAYER_RECEIVE) {
			String playerId = pak.getString();
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null) {
				Channel channel = player.getSession();
				channel.writeAndFlush(pak.getBody());
			}
		} else if (pak.getPacketType() == ProxyType.TEAM) {
			ProxyUtil.onAcrossTeamEvent(pak);
		} else if (pak.getPacketType() == ProxyType.CHANGE_AREA) {
			ProxyUtil.onAcrossChangeArea(pak);
		} else if (pak.getPacketType() == ProxyType.PLAYER_DATA) {
			String playerId = pak.getString();
			WNProxy proxy = Proxys.get(playerId);
			if (proxy != null) {
				proxy.from(pak);
			}
		} else {
			super.onAcrossReceive(pak);
		}
	}

	public void closeArea(String instanceId) {
		AreaUtil.closeArea(instanceId);
	}

	public void onPlayerDie(String instanceId, String diePlayerId, String hitPlayerId) {
		Area area = AreaUtil.getArea(instanceId);
		if (area != null) {
			WNPlayer player = getPlayer(diePlayerId);
			area.pushRelive(player);
		}
	}

	/**
	 * 获得角色在线人数
	 */
	public int getOnlineCount() {
		return onlinePlayers.size() + Robots.size();
	}

	/**
	 * 重置开服日期
	 */
	public static void resetOpenServerDate(LocalDate newOpenDate) {
		if (GWorld.OPEN_SERVER_DATE == null || !newOpenDate.isEqual(GWorld.OPEN_SERVER_DATE)) {
			Out.info("重置开服日期:", newOpenDate.format(DateUtils.F_YYYYMMDD));
			GWorld.OPEN_SERVER_DATE = newOpenDate;
			RevelryManager.getInstance().onResetOpenServerDate(GWorld.OPEN_SERVER_DATE);
		}
	}

	public static void resetExternalTime(String externalTime) {
		LocalDateTime time = LocalDateTime.parse(externalTime, DateUtils.F_YYYYMMDDHHMMSS);
		if (!time.isEqual(GWorld.__EXTERNAL_TIME)) {
			GWorld.__EXTERNAL_TIME = time;
			Out.info("重置对外时间:", externalTime);
		}
	}

	public int max_error_count = 3;

	@Override
	public void handleCopyPacket(GPlayer player, Integer count, PomeloHeader header) {
		if (count >= max_error_count) {
			Out.info("复制封包次数超出了上限,准备T人... playerId=", player.getId(), " count=", count);
			// 封号1分钟
			if (player instanceof WNPlayer) {
				PlayerPO po = PlayerUtil.getPlayerBaseData(player.getId());
				po.freezeTime = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(), 5);
				po.freezeReason = "网络波动";
				// 异步上报.
				LogReportService.getInstance().ansycReportPacketMonitor(po, count, header.getType(), header.route_s);
			}
			player.getSession().close();
		}
	}
}