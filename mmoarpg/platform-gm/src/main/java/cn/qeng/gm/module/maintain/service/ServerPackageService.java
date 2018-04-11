/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.maintain.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.maintain.domain.ServerConfig;
import cn.qeng.gm.module.maintain.domain.ServerConfigRepository;
import cn.qeng.gm.module.maintain.domain.ServerPackage;
import cn.qeng.gm.module.maintain.domain.ServerPackageRepository;
import cn.qeng.gm.websocket.WebSocketPackageHander;
import redis.clients.jedis.JedisPubSub;

/**
 * 版本包业务逻辑处理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class ServerPackageService {
	private final static Logger logger = LogManager.getLogger(ServerPackageService.class);

	private static final String TYPE_GAMESERVER = "gameserver";
	private static final String TYPE_BATTLESERVER = "battleserver";

	@Autowired
	private ServerPackageRepository serverPackageRepository;
	@Autowired
	private RedisManager redisManager;
	@Autowired
	private ServerConfigRepository serverConfigRepository;

	private String localHost;
	private Executor ansycExec;

	@PostConstruct
	public void init() throws Exception {
		localHost = this.getLocalHost();
		logger.info("本机IP:{}", localHost);
		ansycExec = Executors.newFixedThreadPool(4);

		// 订阅RPC响应.
		new Thread(() -> {

			while (true) {// 死死的订着这个Redis...
				logger.info("开始监听 rpc.request ...");
				redisManager.getPigRedis().subscribe(new JedisPubSub() {
					@Override
					public void onMessage(String channel, String message) {
						ansycExec.execute(() -> {
							try {
								JSONObject map = JSON.parseObject(message);
								String opcode = map.getString("opcode");
								switch (opcode) {
								case "download":
									handleDownload(map);
									break;
								case "stop":
									handleStopResult(map);
									break;
								case "update":
									handleUpdateResult(map);
									break;
								case "start":
									handleStartResult(map);
									break;
								default:
									break;
								}
							} catch (Exception e) {
								logger.error("", e);
							}
						});
					}
				}, "rpc.response");
				logger.info("Redis订阅异外退了...");
				// 出现异常情况等1秒再链接...
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}

		}, "pig-rpc").start();
	}

	private String getLocalHost() throws Exception {
		String localHost = InetAddress.getLocalHost().getHostAddress();
		if ("127.0.0.1".equals(localHost)) {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						localHost = ip.getHostAddress();
						if (!"127.0.0.1".equals(localHost)) {
							return localHost;
						}
					}
				}
			}
		}
		return localHost;
	}

	public Page<ServerPackage> getAllServerPackage(int page) {
		return serverPackageRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "id")));
	}

	public ServerPackage getServerPackage(int id) {
		return serverPackageRepository.findOne(id);
	}

	public void upload(HttpServletRequest request, CommonsMultipartFile GFile, CommonsMultipartFile BFile) throws IllegalStateException, IOException {
		ServerPackage sp = new ServerPackage();

		{// GameServer包必不可少...
			sp.setGameserver(GFile.getOriginalFilename());
			File gameserverFile = new File("/data/download/" + sp.getGameserver());
			GFile.transferTo(gameserverFile);
		}

		{// BattleServer包必不可少...
			sp.setBattleserver(BFile.getOriginalFilename());
			File battleserverFile = new File("/data/download/" + sp.getBattleserver());
			BFile.transferTo(battleserverFile);
		}

		try {
			Process proc = Runtime.getRuntime().exec("/bin/bash", null, new File("/bin"));
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true)) {
				out.println("chmod 777 -R /data/download/*");
			}
		} catch (Exception e) {
			logger.warn("添加权限异常.", e);
		}

		SessionUser user = SessionManager.getSessionUser(request);
		sp.setName(user.getName());
		sp.setUsername(user.getUsername());
		sp.setCreateTime(new Date());
		serverPackageRepository.save(sp);
	}

	/**
	 * 记录一条普通日志.
	 */
	public void logInfo(String log) {
		logger.info(log);
		this.sendLog("info", log);
	}

	/**
	 * 记录一条错误日志.
	 */
	public void logError(String log) {
		logger.error(log);
		this.sendLog("error", log);
	}

	private void sendLog(String level, String log) {
		// 如果有当前正在查看聊天监控的WEB
		if (!WebSocketPackageHander.socketSessionMap.isEmpty()) {
			Map<String, String> config = new HashMap<>();
			config.put("level", level);
			config.put("log", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + "-" + log);
			WebSocketMessage<String> msg = new TextMessage(JSON.toJSONBytes(config));
			for (WebSocketSession s : WebSocketPackageHander.socketSessionMap.values()) {
				if (s.isOpen()) {
					try {
						s.sendMessage(msg);
					} catch (Exception e) {}
				}
			}
		}
	}

	/**
	 * 一键更新这些区服...
	 */
	public void update(int[] serverIds, int packageId) {
		ServerPackage sp = this.getServerPackage(packageId);
		// 发出下载，后续任务采用
		for (int sid : serverIds) {
			this.sendDownloadRequest(sid, TYPE_GAMESERVER, sp);
		}
	}

	/**
	 * 发送下载请求.
	 */
	public void sendDownloadRequest(int sid, String type, ServerPackage sp) {
		Map<String, Object> json = new HashMap<>();
		json.put("opcode", "download");
		json.put("type", type);
		json.put("sid", sid);// 区服编号
		json.put("packageId", sp.getId());
		json.put("url", "http://" + localHost + ":9527/");

		ServerConfig config = serverConfigRepository.findOne(sid);
		if (config == null) {
			logger.warn("未找到配置，忽略...");
			return;
		}

		if (TYPE_GAMESERVER.equals(type)) {
			json.put("targetHost", config.getGameHost());// GameServer所在机器
			json.put("fileName", sp.getGameserver());
		} else {
			json.put("targetHost", config.getBattleHost());// 战斗服所在机器
			json.put("fileName", sp.getBattleserver());
		}
		redisManager.getPigRedis().publish("rpc.request", JSON.toJSONString(json));
	}

	public void handleDownload(JSONObject map) {
		int sid = map.getIntValue("sid");
		String type = map.getString("type");
		// 下载成功
		if (map.getBooleanValue("result")) {
			int packageId = map.getIntValue("packageId");
			// GameServer下载成功
			if (TYPE_GAMESERVER.equals(type)) {
				this.logInfo("[" + sid + "]GameServer下载成功...");
				ServerPackage sp = this.getServerPackage(packageId);
				this.sendDownloadRequest(sid, TYPE_BATTLESERVER, sp);
			}
			// BattleServer下载成功
			else {
				this.logInfo("[" + sid + "]BattleServer下载成功...");
				this.sendStopRequest(sid, TYPE_GAMESERVER, packageId);
			}
		}
		// 下载失败
		else {
			this.logError("[" + sid + "]" + type + "下载失败...");
		}
	}

	private void sendStopRequest(int sid, String type, int packageId) {
		Map<String, Object> json = new HashMap<>();
		json.put("opcode", "stop");
		json.put("sid", sid);// 区服编号
		json.put("type", type);//
		json.put("packageId", packageId);//

		ServerConfig config = serverConfigRepository.findOne(sid);
		if (TYPE_GAMESERVER.equals(type)) {
			json.put("targetHost", config.getGameHost());// GameServer所在机器
		} else {
			json.put("targetHost", config.getBattleHost());// 战斗服所在机器
			json.put("port", config.getBattleFastPort());// 战斗服端口，用这个来找PID
		}

		redisManager.getPigRedis().publish("rpc.request", JSON.toJSONString(json));
	}

	public void handleStopResult(JSONObject map) {
		int sid = map.getIntValue("sid");
		String type = map.getString("type");
		// 停止成功
		if (map.getBooleanValue("result")) {
			int packageId = map.getIntValue("packageId");
			if (TYPE_GAMESERVER.equals(type)) {
				this.logInfo("[" + sid + "]GameServer停止成功...");
				this.sendStopRequest(sid, TYPE_BATTLESERVER, packageId);
			} else {
				this.logInfo("[" + sid + "]BattleServer停止成功...");
				// 大于是要更新后重启，等于0就是停服
				if (packageId > 0) {
					this.sendUpdateRequest(sid, TYPE_BATTLESERVER, packageId);
				}
			}
		}
		// 停止失败
		else {
			this.logError("[" + sid + "]" + type + "停止失败...");
		}
	}

	/**
	 * 发送更新请求.
	 */
	private void sendUpdateRequest(int sid, String type, int packageId) {
		Map<String, Object> json = new HashMap<>();
		json.put("opcode", "update");
		json.put("sid", sid);// 区服编号
		json.put("type", type);//
		json.put("packageId", packageId);//

		ServerConfig config = serverConfigRepository.findOne(sid);
		json.put("config", buildConfig(config));

		ServerPackage sp = this.getServerPackage(packageId);
		if (TYPE_GAMESERVER.equals(type)) {
			json.put("targetHost", config.getGameHost());// GameServer所在机器
			json.put("fileName", sp.getGameserver());
		} else {
			json.put("targetHost", config.getBattleHost());// 战斗服所在机器
			json.put("fileName", sp.getBattleserver());
		}

		redisManager.getPigRedis().publish("rpc.request", JSON.toJSONString(json));
	}

	private Map<String, String> buildConfig(ServerConfig config) {
		Map<String, String> result = new HashMap<>();

		result.put("game.name", String.valueOf(config.getServerName()));
		result.put("game.areaId", String.valueOf(config.getAreaId()));
		result.put("game.id", String.valueOf(config.getId()));
		result.put("game.pubhost", String.valueOf(config.getPubhost()));
		result.put("game.port", String.valueOf(config.getPort()));
		result.put("game.debug", String.valueOf(config.isDebug()));
		result.put("use.template.file", config.getTemplate());

		result.put("server.redis.host", String.valueOf(config.getRedisHost()));
		result.put("server.redis.port", String.valueOf(config.getRedisPort()));
		result.put("server.redis.password", String.valueOf(config.getRedisPassword()));
		result.put("server.redis.db", String.valueOf(config.getRedisIndex()));

		result.put("battle.ice.host", String.valueOf(config.getBattleHost()));
		result.put("battle.fastStream.port", String.valueOf(config.getBattleFastPort()));
		result.put("battle.ice.port", String.valueOf(config.getBattleIcePort()));

		// 合服列表...
		List<Integer> sidList = ServerService.getSidList(config.getId());
		if (!sidList.isEmpty()) {
			StringBuilder sb = new StringBuilder(512);
			sidList.forEach(v -> sb.append(v).append(","));
			sb.deleteCharAt(sb.length() - 1);
			result.put("game.sid.list", sb.toString());
		}
		return result;
	}

	public void handleUpdateResult(JSONObject map) {
		int sid = map.getIntValue("sid");
		String type = map.getString("type");

		// 更新成功
		if (map.getBooleanValue("result")) {
			int packageId = map.getIntValue("packageId");

			// 游戏服更新成功...
			if (TYPE_GAMESERVER.equals(type)) {
				this.logInfo("[" + sid + "]GameServer更新成功...");
				this.sendStartRequest(sid, TYPE_BATTLESERVER);
			}
			// 战斗服更新成功
			else {
				this.logInfo("[" + sid + "]BattleServer更新成功...");
				// 再更新游戏服
				this.sendUpdateRequest(sid, TYPE_GAMESERVER, packageId);
			}
		}
		// 更新失败
		else {
			this.logError("[" + sid + "]" + type + "更新失败...");
		}
	}

	private void sendStartRequest(int sid, String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("opcode", "start");
		json.put("sid", sid);// 区服编号
		json.put("type", type);//

		ServerConfig config = serverConfigRepository.findOne(sid);
		if (TYPE_GAMESERVER.equals(type)) {
			json.put("targetHost", config.getGameHost());// GameServer所在机器
		} else {
			json.put("targetHost", config.getBattleHost());// 战斗服所在机器
			json.put("port", config.getBattleFastPort());// 战斗服端口，用这个来找PID
		}

		redisManager.getPigRedis().publish("rpc.request", JSON.toJSONString(json));
	}

	public void handleStartResult(JSONObject map) {
		int sid = map.getIntValue("sid");
		String type = map.getString("type");

		// 启动成功
		if (map.getBooleanValue("result")) {

			// 游戏服启动成功...
			if (TYPE_GAMESERVER.equals(type)) {
				this.logInfo("[" + sid + "]GameServer启动成功...");
				// GG
			}
			// 战斗服启动成功
			else {
				this.logInfo("[" + sid + "]BattleServer启动成功...");
				this.sendStartRequest(sid, TYPE_GAMESERVER);
			}
		}
		// 启动失败
		else {
			this.logError("[" + sid + "]" + type + "启动失败...");
		}
	}

	public void delete(int id) {
		ServerPackage sp = this.getServerPackage(id);
		if (sp != null) {
			serverPackageRepository.delete(sp);
			// 包也删除掉...
			this.deleteFile("/data/download/" + sp.getGameserver());
			this.deleteFile("/data/download/" + sp.getBattleserver());
		}
	}

	private void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public ServerPackage getServerPackageByMaxId() {
		return serverPackageRepository.findAll(new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "id"))).getContent().get(0);
	}

	public void restart(int[] serverIds) {
		ServerPackage sp = this.getServerPackageByMaxId();
		// 发出下载，后续任务采用
		for (int sid : serverIds) {
			this.sendDownloadRequest(sid, TYPE_GAMESERVER, sp);
		}
	}

	public void stop(int[] serverIds) {
		// 发出下载，后续任务采用
		for (int sid : serverIds) {
			this.sendStopRequest(sid, TYPE_GAMESERVER, 0);
		}
	}
}