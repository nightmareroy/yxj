package com.wanniu.login;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GServer;
import com.wanniu.core.GSystem;
import com.wanniu.core.game.protocol.PomeloHeader;
import com.wanniu.core.game.protocol.PomeloPacket;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.core.tcp.protocol.NetHandler;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.DateUtil;
import com.wanniu.login.vo.AppVO;
import com.wanniu.login.vo.AreaVO;
import com.wanniu.login.vo.ServerLoad;
import com.wanniu.login.vo.ServerShow;
import com.wanniu.login.vo.ServerVO;

public class LoginServer extends GServer {

	public static Map<Integer, AppVO> Apps = new ConcurrentHashMap<Integer, AppVO>();

	static {
		instance = new LoginServer();
	}

	public static LoginServer getInstance() {
		return (LoginServer) instance;
	}

	@Override
	public void start() {
		super.start();
		GSystem.addFixedRateJob(new Runnable() {
			@Override
			public void run() {
				long currTime = System.currentTimeMillis();
				for (AppVO app : Apps.values()) {
					for (ServerVO server : app.allServers.values()) {
						if (server.load != ServerLoad.MAINTAIN.value && currTime - server.pingtime > 61000) {
							server.setLoad(ServerLoad.MAINTAIN.value);
							Out.warn(server.name, " last ping [", DateUtil.format(new Date(server.pingtime), DateUtil.F_FULL), "] 变为维护状态了！");
						}
					}
				}
			}
		}, 15000, 30000);
	}

	public void distatcher(JSONObject json) {
		int appId = json.getIntValue("appId");
		int logicServerId = json.containsKey("logicServerId") ? json.getIntValue("logicServerId") : 0;
		AppVO app = getApp(appId);
		if (app == null) {
			app = new AppVO(appId);
			Apps.put(appId, app);
			List<String> servers = GCache.hvals("/serverlist/" + appId);
			if (servers != null) {
				for (String server : servers) {
					try {
						app.addServer(JSON.parseObject(server, ServerVO.class));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		int type = json.getIntValue("type");
		if (type != 0x103) {
			Out.info(String.format("Message. Msg(%s): %s", Integer.toHexString(json.getInteger("type")), json.toJSONString()));
		}
		switch (type) {
		case 0x101: { // 注册游戏服(257)
			ServerVO server = app.get(logicServerId);
			int areaId = json.containsKey("areaId") ? json.getIntValue("areaId") : 0;
			if (server != null && server.areaId != areaId) {
				app.remove(logicServerId);
			}
			if (server == null) {
				server = new ServerVO();
			}
			server.appId = appId;
			server.areaId = areaId;
			server.logicServerId = logicServerId;
			server.name = json.getString("name");
			server.host = json.getString("host");
			server.port = json.getIntValue("port");
			if (json.containsKey("olLimit")) {
				server.olLimit = json.getIntValue("olLimit");
			}
			if (json.containsKey("recommend")) {
				server.isRecommend = json.getBooleanValue("recommend");
			}
			if (json.containsKey("new")) {
				server.isNew = json.getBooleanValue("new");
			}
			if (json.containsKey("hot")) {
				server.isHot = json.getBooleanValue("hot");
			}
			if (json.containsKey("show")) {
				server.setShow(json.getIntValue("show"));
			}
			server.setOlCount(server.olCount);
			server.setLoad(Math.max(server.load, ServerLoad.SMOOTH.value));
			JSONArray array = json.getJSONArray("sidList");
			if (array != null) {
				if (server.sidList == null) {
					server.sidList = new HashSet<>();
				}
				for (Object a : array) {
					Integer sid = (Integer) a;
					server.sidList.add(sid);
				}
			}
			app.addServer(server);
			GCache.hset("/serverlist/" + appId, String.valueOf(server.logicServerId), JSON.toJSONString(server));
			Out.info("游戏服接入：", server);

			// 修正从服配置与主服一致...
			if (server.sidList != null) {
				for (Integer sid : server.sidList) {
					if (sid == server.logicServerId) {
						continue;
					}

					// 修正从服登录IP与端口...
					ServerVO s = app.get(sid);
					if (s == null) {
						Out.warn("修正从服登录IP与端口未找到从服", sid);
						continue;
					}

					s.appId = appId;
					s.areaId = areaId;
					s.host = server.host;
					s.port = server.port;
					s.olLimit = server.olLimit;
					s.setShow(server.show);
					s.setOlCount(server.olCount);
					s.setLoad(Math.max(server.load, ServerLoad.SMOOTH.value));
					app.addServer(s);
					GCache.hset("/serverlist/" + appId, String.valueOf(s.logicServerId), JSON.toJSONString(s));
					Out.info("游戏服接入：", s);
				}
			}
			break;
		}
		case 0x102: { // 开放游戏服(258)
			ServerVO server = app.get(logicServerId);
			if (server == null)
				return;
			int show = json.containsKey("show") ? json.getIntValue("show") : ServerShow.OUTER.value;
			server.setShow(show);
			break;
		}
		case 0x103: { // 游戏服负载状态(259)
			ServerVO server = app.get(logicServerId);
			if (server == null)
				return;
			int onlineCount = json.getIntValue("load");
			server.setOlCount(onlineCount);

			// 合服同步...
			if (server.sidList != null) {
				for (Integer sid : server.sidList) {
					// 本服
					if (logicServerId == sid) {
						continue;
					}
					// 从服...
					ServerVO s = app.get(sid);
					if (s == null) {
						continue;
					}
					s.setOlCount(onlineCount);
				}
			}
			break;
		}
		case 0x104: { // 修改服务器所在区(260)
			ServerVO server = app.get(logicServerId);
			server.areaId = json.getIntValue("areaId");
			app.changeArea(server);
			break;
		}
		case 0x105: { // 注册新区(261)
			app.addArea(json.getIntValue("areaId"), json.getString("areaName"));
			break;
		}
		case 0x106: { // 移除区(262)
			app.removeArea(json.getIntValue("areaId"));
			break;
		}
		case 0x107: { // 移除逻辑服新区(263)
			app.remove(logicServerId);
			break;
		}
		case 0x108: { // 修改服务器IP(264)
			ServerVO server = app.get(logicServerId);
			if (server == null)
				return;
			server.host = json.getString("ip");
			server.port = json.getIntValue("port");
			break;
		}
		default:
			Out.warn("未定义的类型：0x", Integer.toHexString(json.getIntValue("type")), json.toJSONString());
			return;
		}
	}

	public AppVO getApp(int appId) {
		AppVO app = Apps.get(appId);
		if (app == null) {
			Map<String, String> areas = GCache.hgetAll("/arealist/" + appId);
			if (areas != null) {
				app = new AppVO(appId);
				for (Map.Entry<String, String> entry : areas.entrySet()) {
					app.addArea(new AreaVO(Integer.valueOf(entry.getKey()), entry.getValue()));
				}
				List<String> servers = GCache.hvals("/serverlist/" + appId);
				if (servers != null) {
					for (String server : servers) {
						try {
							app.addServer(JSON.parseObject(server, ServerVO.class));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Apps.put(appId, app);
			}
		}
		return app;
	}

	public void addPacket(Packet packet) {
		PomeloPacket pak = (PomeloPacket) packet;
		PomeloHeader header = pak.getHeader();
		putGlobalRoute(header.route_s, packet);
	}

	public void registerHandler(NetHandler handler) {
		super.registerHandler(handler);
	}
}
