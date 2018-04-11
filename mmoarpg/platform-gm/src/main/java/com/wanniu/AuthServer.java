package com.wanniu;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 功能描述：Redis中间件缓存基础服务<br>
 * （依赖jedis和commons-pool）
 * 
 * @author agui
 */
public class AuthServer {

	public static final String K_TOKEN = "/token/";
	public static final String K_PLAYER_SERVERS = "/player/servers/";

	public static final String K_SERVER_LOGIN = "/server/login";

	/** 登录公告订阅KEY */
	public static final String K_LOGIN_ANNOUNCE = "/login/announce";
	/** 登录公告存储KEY */
	public static final String ANNOUNCE = "announcement";

	private static JedisPool RedisPool;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的
		config.setTestOnBorrow(GConfig.getInstance().getBoolean("auth.redis.testonborrow", true));
		// 在return给pool时，是否提前进行validate操作
		config.setTestOnReturn(GConfig.getInstance().getBoolean("auth.redis.testonreturn", true));
		config.setMaxIdle(GConfig.getInstance().getInt("auth.redis.maxidle", 20));
		config.setMaxWaitMillis(GConfig.getInstance().getInt("auth.redis.maxwait", 1000));
		config.setMaxTotal(GConfig.getInstance().getInt("auth.redis.total", 5));

		String redisHost = GConfig.getInstance().get("auth.redis.host", "127.0.0.1");
		int redisPort = GConfig.getInstance().getInt("auth.redis.port", 6379);
		String pwd = GConfig.getInstance().get("auth.redis.password");
		int db = GConfig.getInstance().getInt("auth.redis.db", 0);
		RedisPool = new JedisPool(config, redisHost, redisPort, 2000, pwd != null ? pwd.trim() : null, db);
	}

	protected AuthServer() {}

	private static final Jedis getRedis() {
		return RedisPool.getResource();
	}

	public static final String set(final String key, String value) {
		try (Jedis redis = getRedis()) {
			return redis.set(key, value);
		}
	}

	public static final String get(String key) {
		Jedis redis = getRedis();
		try {
			return redis.get(key);
		} finally {
			release(redis);
		}
	}

	public static final String hget(String key, String field) {
		Jedis redis = getRedis();
		try {
			return redis.hget(key, field);
		} finally {
			release(redis);
		}
	}

	public static final long hset(String key, String field, String value) {
		Jedis redis = getRedis();
		try {
			return redis.hset(key, field, value);
		} finally {
			release(redis);
		}
	}

	public static final String put(String key, String value) {
		Jedis redis = getRedis();
		try {
			return redis.set(key, value);
		} finally {
			release(redis);
		}
	}

	public static final String put(String key, String value, int expired) {
		Jedis redis = getRedis();
		try {
			return redis.setex(key, expired, value);
		} finally {
			release(redis);
		}
	}

	public static final long remove(String key) {
		Jedis redis = getRedis();
		try {
			return redis.del(key);
		} finally {
			release(redis);
		}
	}

	public static final Long hdel(String key, String field) {
		Jedis redis = getRedis();
		try {
			return redis.hdel(key, field);
		} finally {
			release(redis);
		}
	}

	public static final boolean exists(String key) {
		Jedis redis = getRedis();
		try {
			return redis.exists(key);
		} finally {
			release(redis);
		}
	}

	public static final boolean hexists(String key, String field) {
		Jedis redis = getRedis();
		try {
			return redis.hexists(key, field);
		} finally {
			release(redis);
		}
	}

	public static final Set<String> hkeys(String key) {
		Jedis jedis = getRedis();
		try {
			return jedis.hkeys(key);
		} finally {
			release(jedis);
		}
	}

	public static final List<String> hvals(String key) {
		Jedis jedis = getRedis();
		try {
			return jedis.hvals(key);
		} finally {
			release(jedis);
		}
	}

	public static final Map<String, String> hgetAll(String key) {
		Jedis redis = getRedis();
		try {
			return redis.hgetAll(key);
		} finally {
			release(redis);
		}
	}

	public static final long expire(String key, int sec) {
		Jedis redis = getRedis();
		try {
			return redis.expire(key, sec);
		} finally {
			release(redis);
		}
	}

	public static final String rename(String oldKey, String newKey) {
		Jedis redis = getRedis();
		try {
			return redis.rename(oldKey, newKey);
		} finally {
			release(redis);
		}
	}

	public static final void release(Jedis redis) {
		// RedisPool.returnResource(redis);
		redis.close();
	}

	public static final void publish(JSONObject json) {
		publish(K_SERVER_LOGIN, json);
	}

	public static final void publish(String key, JSONObject json) {
		Jedis redis = getRedis();
		try {
			redis.publish(key, json.toJSONString());
		} finally {
			release(redis);
		}
	}

	public static void del(String key) {
		try (Jedis redis = getRedis()) {
			redis.del(key);
		}
	}
}
