package com.wanniu.core;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.FileUtil;
import com.wanniu.core.util.PropertiesUtil;

/**
 * 游戏配置类
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

	/**
	 * 初始化
	 */
	public synchronized void init() {
		if (!inited) {
			inited = true;
			LinkedList<String> exts = new LinkedList<String>();
			File confServer = new File("conf/server.conf");
			build(exts, confServer);
			while(!exts.isEmpty()) {
				String properties = exts.poll();
				confServer = new File(properties);
				if(properties.endsWith("properties")) {
					load(confServer);
				} else {
					build(exts, confServer);
				}
				System.out.format("加载扩展配置：%s\n", confServer.getAbsolutePath());
			}
			Out.info("已加载系统参数 -> " + configs.size());
		}
		GSystem.open();
	}
	
	private void build(LinkedList<String> exts, File confServer) {
		if (confServer != null && confServer.exists() && confServer.isFile()) {
			List<String> confs = FileUtil.readLines(confServer);
			for (String conf : confs) {
				String cs = conf.trim();
				if (cs.length()==0 || cs.startsWith("#")) {
					continue;
				}
				int index = conf.indexOf("=");
				if(index > 0) {
					String key = conf.substring(0, index).trim();
					String value = conf.substring(index + 1).trim();
					if(key.equals("include") && !exts.contains(value)) {
						exts.add(value);
						continue;
					}
					if(!configs.containsKey(key)) {
						configs.put(key, value);
					} else {
						System.err.format("%s -> 配置重复，%s中的配置无效!\n", key, confServer.getName());
					}
				}
			}
		}
	}

	/**
	 * 重加载所有配置
	 */
	public synchronized void reinit() {
		configs.clear();
		inited = false;
		init();
	}

	/**
	 * 加载配置
	 * 
	 * @param configFile
	 *            配置文件名（完整路径）
	 */
	private void load(File configFile) {
		Map<String, String> confs = PropertiesUtil.loadProperties(configFile);
		for(Map.Entry<String, String> entry : confs.entrySet()) {
			if(!configs.containsKey(entry.getKey())) {
				configs.put(entry.getKey(), entry.getValue());
			} else {
				Out.warn(entry.getKey()+" -> 配置重复,game.properties中的配置无效!");
			}
		}
	}

	/**
	 * 获取游戏服务IP地址
	 */
	public String getLoginHost() {
		return get("server.host");
	}

	/**
	 * 获得服务器侦听端口
	 */
	public int getLoginPort() {
		return getInt("server.port");
	}

	/**
	 * 获取服务ID
	 */
	public int getServerID() {
		return getInt("server.id");
	}
	
	/**
	 * 获取服务语言
	 */
	public String getServerLang() {
		return get("server.lang");
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

}
