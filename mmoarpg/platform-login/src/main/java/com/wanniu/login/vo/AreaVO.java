package com.wanniu.login.vo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AreaVO {

	public int appId; // appId
	public int areaId; // 区Id
	public String areaName = "NEW"; // 区名称

	public Map<Integer, ServerVO> areaServers = new ConcurrentHashMap<Integer, ServerVO>();

	public AreaVO(int areaId) {
		this.areaId = areaId;
		areaName += this.areaId;
	}

	public AreaVO(int areaId, String areaName) {
		this.areaId = areaId;
		this.areaName = areaName;
	}

	public void add(ServerVO server) {
		String name = server.name;
		for (ServerVO s : areaServers.values()) {
			if (s.name.equals(name) && s.logicServerId != server.logicServerId) {
				s.name = name + s.logicServerId;
				server.name = name + server.logicServerId;
			}
		}
		areaServers.put(server.logicServerId, server);
	}

	public void remove(int logicServerId) {
		areaServers.remove(logicServerId);
	}

	public String toString() {
		return this.appId + ":" + this.areaId + "-" + this.areaName;
	}

}
