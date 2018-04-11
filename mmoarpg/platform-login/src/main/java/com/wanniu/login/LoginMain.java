package com.wanniu.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GSystem;
import com.wanniu.core.http.HttpServer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.login.announce.AnnounceMsgHandler;
import com.wanniu.login.whitelist.WhitelistManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 登录服务器启动入口.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class LoginMain {

	private static final String REDIS_KEY_ANNOUNCE = "/login/announce";
	private static final String REDIS_KEY_LOGIN = "/server/login";
	private static final String REDIS_KEY_CLEAR_WHITELIST = "/clear/whitelist";

	public static void main(String[] args) {
		server_main(args);
		startHttpServer();
	}

	/**
	 * 1.加载配置文件.<br>
	 * 2.加载公告.<br>
	 * 3.Redis订阅.<br>
	 */
	public static void server_main(String[] args) {
		GConfig.getInstance().init();
		// 加载登录公告
		AnnounceServer.getInstance().init();
		// redis订阅
		subscribe();
		// 启动登录端口...
		LoginServer.getInstance().start();
	}

	public static void subscribe() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Jedis subscriberJedis = null;
					try {
						subscriberJedis = GCache.getRedis();
						subscriberJedis.subscribe(new JedisPubSub() {
							@Override
							public void onMessage(String channel, String message) {
								try {
									switch (channel) {
									case REDIS_KEY_ANNOUNCE:// 登录公告变更
										AnnounceServer.getInstance().init();
										break;
									case REDIS_KEY_LOGIN:// 区服信息变化
										LoginServer.getInstance().distatcher(JSON.parseObject(message));
										break;
									case REDIS_KEY_CLEAR_WHITELIST:// 清理缓存的白名单
										WhitelistManager.getInstance().clearAll();
										break;
									default:
										break;
									}

								} catch (Exception e) {
									Out.error("redis onMessage error. channel=", channel, ",message=", message, e);
								}
							}

							@Override
							public void onSubscribe(String channel, int subscribedChannels) {
								Out.info(subscribedChannels, " subscribedChannels onSubscribe: ", channel);
							}

							@Override
							public void onUnsubscribe(String channel, int subscribedChannels) {
								Out.warn(subscribedChannels, " subscribedChannels onUnsubscribe: ", channel);
							}

						}, REDIS_KEY_LOGIN, REDIS_KEY_ANNOUNCE, REDIS_KEY_CLEAR_WHITELIST);
					} catch (Exception e) {
						Out.error("auth redis closed!!!", e.toString());
						if (subscriberJedis != null) {
							GCache.release(subscriberJedis);
						}
						GSystem.waitMills(1000);
					}
				}
			}
		}).start();
	}

	public static void addArea(int areaId, String areaName) {
		GConfig.getInstance().init();
		JSONObject json = new JSONObject();
		json.put("type", 0x105);
		json.put("appId", 80);
		json.put("areaId", areaId);
		json.put("areaName", areaName);
		GCache.publish(json);
		Out.info("已执行！！！");
		System.exit(1);
	}

	public static void removeArea(int areaId) {
		GConfig.getInstance().init();
		JSONObject json = new JSONObject();
		json.put("type", 0x106);
		json.put("appId", 80);
		json.put("areaId", areaId);
		GCache.publish(json);
		Out.info("已执行！！！");
		System.exit(1);
	}

	public static void removeServer(int logicServerId) {
		GConfig.getInstance().init();
		JSONObject json = new JSONObject();
		json.put("type", 0x107);
		json.put("appId", 80);
		json.put("logicServerId", logicServerId);
		GCache.publish(json);
		Out.info("已执行！！！");
		System.exit(1);
	}

	/**
	 * 启动Http侦听服务
	 */
	public static void startHttpServer() {
		AnnounceMsgHandler handler = new AnnounceMsgHandler("/announce");
		HttpServer httpServer = new HttpServer();
		httpServer.addHandler(handler);
		Thread t = new Thread("announce") {
			@Override
			public void run() {
				try {
					httpServer.run(GConfig.getInstance().getInt("httpserver.port"));
				} catch (Exception e) {
					Out.error(e);
				}
			}
		};
		t.start();
	}
}
