package cn.qeng.gm.module.maintain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.AuthServer;
import com.wanniu.GServer;

import cn.qeng.gm.config.GameManagerInitializer;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.domain.ServerRepository;

/**
 * 服务器业务逻辑类.
 *
 * @author 小流氓(176543888@qq.com)
 */
@Service
public class ServerService {
	private final static Logger logger = LogManager.getLogger(ServerService.class);

	private static final Map<Integer, Server> servers = new HashMap<>();
	private static LinkedHashMap<String, List<Server>> caches = new LinkedHashMap<>();
	private static LinkedHashMap<String, List<Server>> cachesx = new LinkedHashMap<>();
	private static ServerRepository serverRepository;

	@Autowired
	public void setServerRepository(ServerRepository serverRepository) {
		ServerService.serverRepository = serverRepository;
	}

	@Autowired
	private GameManagerInitializer gameManagerInitializer;

	@PostConstruct
	public void init() {
		logger.info("加载服务器列表到内存...");
		serverRepository.findAll().forEach(v -> servers.put(v.getId(), v));
		resetCalServerByGroupCaches();

		gameManagerInitializer.init();
	}

	/**
	 * 所以服务器列表添加进返回视图.
	 */
	public void buildServerList(ModelAndView view) {
		view.addObject("servers", caches);
	}

	/**
	 * 所以服务器列表添加进返回视图.(不显示已合并的区服)
	 */
	public void buildServerListx(ModelAndView view) {
		view.addObject("servers", cachesx);
	}

	/**
	 * 重新生成分组缓存.
	 */
	public synchronized static void resetCalServerByGroupCaches() {
		final int size = 500;
		{
			LinkedHashMap<String, List<Server>> result = new LinkedHashMap<>();

			List<Server> serverList = new ArrayList<>(servers.values());
			serverList.sort((s1, s2) -> s1.getId() - s2.getId());
			serverList.stream().filter(v -> v.getAreaId() > 0).forEach((v) -> {
				int x = v.getId() / size;
				String key = Math.max(x * size, 1) + "-" + ((x + 1) * size - 1);
				result.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
			});
			caches = result;
		}
		{
			LinkedHashMap<String, List<Server>> result = new LinkedHashMap<>();
			List<Server> serverList = new ArrayList<>(servers.values());
			serverList.sort((s1, s2) -> s1.getId() - s2.getId());
			serverList.stream().filter(v -> v.getAreaId() > 0 && v.getMaster() == 0).forEach((v) -> {
				int x = v.getId() / size;
				String key = Math.max(x * size, 1) + "-" + ((x + 1) * size - 1);
				result.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
			});
			cachesx = result;
		}
	}

	public static Collection<Server> getAllServer() {
		return servers.values();
	}

	public static Server getServer(Integer id) {
		return servers.get(id);
	}

	public static void saveServer(Server server) {
		serverRepository.save(server);
	}

	public static void addServer(int sid, Server server) {
		servers.put(sid, server);

		// 如果是大区需要发布0x105
		if (server.getAreaId() == 0) {
			JSONObject json = new JSONObject();
			json.put("type", 0x105);
			json.put("appId", server.getAppId());
			json.put("areaId", server.getId());
			json.put("areaName", server.getServerName());
			AuthServer.publish(json);
		}

	}

	public Page<Server> getAllServers(int page) {
		List<Server> servers = new ArrayList<>(getAllServer());
		return new PageImpl<>(servers, new PageRequest(page, PageConstant.MAX_SIZE), servers.size());
	}

	/**
	 * 添加一个新的大区.
	 */
	public boolean addServer(int serverId, String serverName, String describe) {
		Server server = servers.get(serverId);
		if (server == null) {
			server = new Server();
			server.setId(serverId);
			server.setServerName(serverName);
			server.setDescribe(describe);
			server.setAppId(GServer.__APP_ID);
			serverRepository.save(server);

			logger.info("添加一个新的区服.serverId={},serverName={},describe={}", serverId, serverName, describe);
			ServerService.addServer(serverId, server);
			return true;
		}
		return false;
	}

	public static void deleteServer(int id) {
		Server server = serverRepository.findOne(id);
		if (server != null) {
			serverRepository.delete(server);

			JSONObject json = new JSONObject();
			json.put("appId", server.getAppId());
			if (server.getAreaId() == 0) {
				json.put("type", 0x106);
				json.put("areaId", server.getId());
				AuthServer.publish(json);
			} else {
				servers.remove(id);
				resetCalServerByGroupCaches();

				json.put("type", 0x107);
				json.put("logicServerId", server.getId());
				AuthServer.publish(json);
			}
		}
	}

	public static List<Integer> getSidList(int id) {
		List<Integer> result = new ArrayList<>();
		for (Server server : servers.values()) {
			// 大区跳过...
			if (server.getAreaId() == 0) {
				continue;
			}

			// 本服或主服是此ID的都算...
			if (server.getId() == id || server.getMaster() == id) {
				result.add(server.getId());
			}
		}
		return result;
	}

	public List<Integer> getSidList(String sidList) {
		Set<Integer> result = new HashSet<>();
		for (Integer sid : JSON.parseArray(sidList, Integer.class)) {
			Server server = ServerService.getServer(sid);
			if (server.getMaster() > 0) {
				result.add(server.getMaster());
			} else {
				result.add(sid);
			}
		}
		return new ArrayList<>(result);
	}
}