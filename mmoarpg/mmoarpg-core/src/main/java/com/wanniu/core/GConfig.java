package com.wanniu.core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.FileUtil;
import com.wanniu.core.util.PropertiesUtil;
import com.wanniu.core.util.StringUtil;

/**
 * 游戏配置类
 * 
 * @author agui
 */
public final class GConfig {

	private static final String __0x__ = "0x";

	/** 属性列表 */
	private Map<String, String> configs;

	/** 单例 */
	private static GConfig instance = new GConfig();

	/** 是否已经初始化 */
	private boolean inited;

	/**
	 * 私有构造
	 */
	private GConfig() {
		configs = new HashMap<String, String>();
	}

	/**
	 * 获取单例
	 */
	public static GConfig getInstance() {
		return instance;
	}

	private boolean isEnableProxy, isEnableDB, isEnablePay;

	/**
	 * 初始化
	 */
	public synchronized void init(boolean isOnlyConfigLog4j) {
		if (!inited) {
			inited = true;
			LinkedList<String> exts = new LinkedList<String>();
			File confServer = new File("conf/server.conf");
			build(exts, confServer);
			while (!exts.isEmpty()) {
				String properties = exts.poll();
				confServer = new File(properties);
				if (!confServer.exists()) {
					System.err.println("缺少配置文件：" + properties);
					System.exit(0);
				}
				if (properties.endsWith("properties")) {
					load(confServer);
				} else {
					build(exts, confServer);
				}
				System.out.format("加载扩展配置：%s\n", confServer.getAbsolutePath());
			}
			isEnableProxy = getBoolean("server.proxy.enable");
			isEnableDB = getBoolean("server.db.enable");
			isEnablePay = getBoolean("server.pay.enable");
			Out.info("已加载系统参数 -> ", configs.size());
		}
		GSystem.open(isOnlyConfigLog4j);
	}

	private void build(LinkedList<String> exts, File confServer) {
		if (confServer != null && confServer.exists() && confServer.isFile()) {
			List<String> confs = FileUtil.readLines(confServer);
			for (String conf : confs) {
				String cs = conf.trim();
				if (cs.length() == 0 || cs.startsWith("#")) {
					continue;
				}
				int index = conf.indexOf("=");
				if (index > 0) {
					String key = conf.substring(0, index).trim();
					String value = conf.substring(index + 1).trim();
					if (key.equals("include") && !exts.contains(value)) {
						exts.add(value);
						continue;
					}
					if (configs.containsKey(key)) {
						System.err.format("%s -> 配置重复，已使用%s中的配置!\n", key, confServer.getName());
					}
					configs.put(key, value);
				}
			}
		}
	}

	/**
	 * 重加载所有配置
	 */
	public synchronized void reinit(boolean isOnlyConfigLog4j) {
		configs.clear();
		inited = false;
		init(isOnlyConfigLog4j);
	}

	/**
	 * 加载配置
	 * 
	 * @param configFile 配置文件名（完整路径）
	 */
	private void load(File configFile) {
		Map<String, String> confs = PropertiesUtil.loadProperties(configFile);
		for (Map.Entry<String, String> entry : confs.entrySet()) {
			if (!configs.containsKey(entry.getKey())) {
				configs.put(entry.getKey(), entry.getValue());
			} else {
				Out.warn(entry.getKey(), " -> 配置重复,game.properties中的配置无效!");
			}
		}
	}

	/**
	 * 获取游戏服务公网IP地址
	 */
	public String getGamePubHost() {
		return exists("game.pubhost") ? get("game.pubhost") : getGameHost();
	}

	/**
	 * 获取游戏服务IP地址
	 */
	public String getGameHost() {
		return get("game.host");
	}

	/**
	 * 获得服务器侦听端口
	 */
	public int getGamePort() {
		return getInt("game.port");
	}

	/**
	 * 是否使用GM服务器
	 */
	public boolean isEnableGm() {
		return getBoolean("server.gm.enable");
	}

	/**
	 * 是否使用Proxy服务器
	 */
	public boolean isEnableProxy() {
		return isEnableProxy;
	}

	/**
	 * 是否使用DB服务器
	 */
	public boolean isEnableDB() {
		return isEnableDB;
	}

	/**
	 * 是否使用充值服务器
	 */
	public boolean isEnablePay() {
		return isEnablePay;
	}

	/**
	 * 获取DB服务IP地址
	 */
	public String getDBHost() {
		return get("server.db.host");
	}

	/**
	 * 获得DB服侦听端口
	 */
	public int getDBPort() {
		return getInt("server.db.port");
	}

	/**
	 * 服务器最大在线人数
	 */
	public int getPlayerLimit() {
		return getInt("game.player.limit", 2000);
	}

	/**
	 * 自动保存玩家数据的延迟时间
	 */
	public int getAutoSaveDelay() {
		return getInt("auto.save.delay", 555);
	}

	/**
	 * 自动保存玩家数据的时间间隔
	 */
	public int getAutoSaveInterval() {
		return getInt("auto.save.interval", 555);
	}

	/**
	 * 获取应用ID
	 */
	public int getAppID() {
		return getInt("game.appid");
	}

	/**
	 * 获取服务ID
	 */
	public int getGameID() {
		return getInt("game.id");
	}

	/**
	 * 获取服务语言
	 */
	public String getGameLang() {
		return get("game.lang");
	}

	/**
	 * 设置系统参数
	 */
	public void put(String key, String value) {
		configs.put(key, value);
	}

	/**
	 * 返回某个系统参数值
	 */
	public String get(String key) {
		return configs.get(key);
	}

	/**
	 * 是否存在系统参数值
	 */
	public boolean exists(String key) {
		return configs.containsKey(key);
	}

	/**
	 * 返回某个系统参数值
	 */
	public String get(String key, String def) {
		return configs.containsKey(key) ? configs.get(key) : def;
	}

	public long getLong(String key) {
		String value = configs.get(key);
		if (value.startsWith(__0x__)) {
			return Long.parseLong(value.replace(__0x__, ""), 16);
		}
		return Long.parseLong(value);
	}

	public int getInt(String key) {
		String value = configs.get(key);
		if (value.startsWith(__0x__)) {
			return Integer.parseInt(value.replace(__0x__, ""), 16);
		}
		return Integer.parseInt(value);
	}

	public int getInt(String key, int def) {
		return configs.containsKey(key) ? getInt(key) : def;
	}

	public byte getByte(String key) {
		return Byte.parseByte(configs.get(key));
	}

	public short getShort(String key) {
		String value = configs.get(key);
		if (value.startsWith(__0x__)) {
			return Short.parseShort(value.replace(__0x__, ""), 16);
		}
		return Short.parseShort(value);
	}

	public short getShort(String key, short def) {
		return configs.containsKey(key) ? getShort(key) : def;
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(configs.get(key));
	}

	public boolean getBoolean(String key, boolean def) {
		return configs.containsKey(key) ? getBoolean(key) : def;
	}

	public Set<Integer> getSet(String key) {
		Set<Integer> result = new HashSet<>();
		String sidList = configs.getOrDefault(key, "");
		if (StringUtil.isNotEmpty(sidList)) {
			for (String sid : sidList.split(",")) {
				result.add(Integer.parseInt(sid));
			}
		}
		if (result.isEmpty()) {
			result.add(this.getGameID());
		}
		return result;
	}
}