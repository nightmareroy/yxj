package com.wanniu.login.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GServer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.core.redis.GlobalDao;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.StringUtil;
import com.wanniu.login.whitelist.WhitelistManager;

public class AppVO {

	// <logicServerId : ServerVO>
	public Map<Integer, ServerVO> allServers = new ConcurrentHashMap<Integer, ServerVO>();
	// <areaid : AreaVO>
	public Map<Integer, AreaVO> areas = new ConcurrentHashMap<Integer, AreaVO>();

	public int appId;

	public AppVO(int appId) {
		this.appId = appId;
	}

	public void addArea(int areaId, String name) {
		AreaVO area = getArea(areaId);
		area.areaName = name;
		GCache.hset("/arealist/" + this.appId, String.valueOf(areaId), name);
	}

	public void addArea(AreaVO area) {
		areas.put(area.areaId, area);
	}

	public void removeArea(int areaId) {
		AreaVO area = areas.remove(areaId);
		if (area != null) {
			for (int logicServerId : area.areaServers.keySet()) {
				allServers.remove(logicServerId);
				GCache.hremove("/serverlist/" + this.appId, String.valueOf(logicServerId));
			}
			GCache.hremove("/arealist/" + this.appId, String.valueOf(areaId));
		}
	}

	public AreaVO getArea(int areaId) {
		AreaVO area = areas.get(areaId);
		if (area == null) {
			String name = GCache.hget("/arealist/" + this.appId, String.valueOf(areaId));
			if (name != null) {
				area = new AreaVO(areaId);
				area.appId = this.appId;
				area.areaName = name;
				areas.put(area.areaId, area);
			}
			if (area == null) {
				area = new AreaVO(areaId);
				areas.put(areaId, area);
			}
		}
		return area;
	}

	public void addServer(ServerVO server) {
		ServerVO oldServer = allServers.get(server.logicServerId);
		if (oldServer != null) {
			AreaVO area = getArea(oldServer.areaId);
			if (area.areaId != server.areaId) {
				area.remove(server.logicServerId);
			}
		}
		AreaVO area = getArea(server.areaId);
		area.add(server);
		allServers.put(server.logicServerId, server);
	}

	public void remove(int logicServerId) {
		ServerVO server = get(logicServerId);
		if (server != null) {
			AreaVO area = areas.get(server.areaId);
			if (area != null) {
				area.remove(logicServerId);
			}
			allServers.remove(logicServerId);
		}
		GCache.hremove("/serverlist/" + this.appId, String.valueOf(logicServerId));
	}

	public ServerVO get(int logicServerId) {
		return allServers.get(logicServerId);
	}

	public void changeArea(ServerVO server) {
		areas.remove(server.areaId);
		addServer(server);
	}

	/**
	 * 获取公共服务器列表和推荐服列表
	 */
	public JSONArray getServerList(Map<Integer, Integer> histories, Packet pak, String uid) {
		// 是否在白名单中.
		String ip = pak.getIp();
		boolean isWhiteList = WhitelistManager.getInstance().isWhiteList(pak.getIp(), uid);
		Out.debug("白名单判定 uid=", uid, ",ip=", ip, ",isWhiteList=", isWhiteList);

		JSONArray arr = new JSONArray();

		// 所以左边大类...
		ArrayList<AreaVO> allAreaList = new ArrayList<>(areas.values());
		allAreaList.sort((o1, o2) -> o2.areaId - o1.areaId);

		for (AreaVO area : allAreaList) {
			JSONObject json = new JSONObject();
			JSONArray areaServers = new JSONArray();

			ArrayList<ServerVO> allServerList = new ArrayList<>(area.areaServers.values());
			allServerList.sort((o1, o2) -> o2.logicServerId - o1.logicServerId);

			for (ServerVO server : allServerList) {
				if (server.show > 0) {
					JSONArray show = server.toShowJSON();
					// 对内且不在白名单中，显示维护状态
					if (server.show == ServerShow.INNER.value && !isWhiteList) {
						show.set(4, ServerLoad.MAINTAIN.value);
					}

					// 在这个区有几个角色
					int roleCount = histories.getOrDefault(server.logicServerId, 0);
					show.add(roleCount);
					if (roleCount > 0) {
						JSONArray players = new JSONArray();
						String s_players = GlobalDao.hget(String.valueOf(server.logicServerId), uid);
						if (StringUtil.isNotEmpty(s_players)) {
							JSONObject jsPlayers = JSON.parseObject(s_players);
							for (String rid : jsPlayers.keySet()) {
								players.add(jsPlayers.getJSONObject(rid));
							}
							Collections.sort(players, new Comparator<Object>() {
								@Override
								public int compare(Object o1, Object o2) {
									Long t1 = ((JSONObject) o1).getLong("time");
									Long t2 = ((JSONObject) o2).getLong("time");
									if (t1 == t2)
										return 0;
									if (t1 == null)
										return 1;
									if (t2 == null)
										return -1;
									return -t1.compareTo(t2);
								}
							});
						}
						show.add(players);
					}
					areaServers.add(show);
				}
			}
			json.put(area.areaName, areaServers);
			arr.add(json);
		}
		if (pak.getSession().isActive()) {
			GServer.getInstance().addLoginSession(pak.getSession());
		}
		return arr;
	}
}