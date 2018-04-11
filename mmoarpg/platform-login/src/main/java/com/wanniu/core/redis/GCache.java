package com.wanniu.core.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

/**
 * 功能描述：Redis中间件缓存基础服务<br>
 * （依赖jedis和commons-pool）
 * @author agui
 */
public class GCache {

	private static JedisPool RedisPool;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的
		config.setTestOnBorrow(GConfig.getInstance().getBoolean("server.redis.testonborrow", true));
		// 在return给pool时，是否提前进行validate操作
		config.setTestOnReturn(GConfig.getInstance().getBoolean("server.redis.testonreturn", true));
		config.setMaxIdle(GConfig.getInstance().getInt("server.redis.maxidle", 20));
		config.setMaxWaitMillis(GConfig.getInstance().getInt("server.redis.maxwait", 1000));
		config.setMaxTotal(GConfig.getInstance().getInt("server.redis.total", 5));

		String redisHost = GConfig.getInstance().get("server.redis.host", "127.0.0.1");
		int redisPort = GConfig.getInstance().getInt("server.redis.port", 6379);
		String pwd = GConfig.getInstance().get("server.redis.password");
		int db = GConfig.getInstance().getInt("server.redis.db", 0);
		RedisPool = new JedisPool(config, redisHost, redisPort, 2000, pwd != null ? pwd.trim() : null, db);
	}

	protected GCache() {}

	public static final Jedis getRedis() {
		return RedisPool.getResource();
	}
	
	/**
	 * 字符串转字节数组
	 */
	public static final byte[] encode(String utf) {
		return SafeEncoder.encode(utf);
	}

	/**
	 * 字节数组转字符串
	 */
	public static final String decode(byte[] body) {
		if (body == null || body.length == 0)
			return null;
		return SafeEncoder.encode(body);
	}

	public static final String get(String key) {
		Jedis redis = getRedis();
		try {
			return redis.get(key);
		} finally {
			release(redis);
		}
	}

	public static final byte[] get(byte[] key) {
		Jedis redis = getRedis();
		try {
			return redis.get(key);
		} finally {
			release(redis);
		}
	}

	public static final String hget(String key, String field) {
		Jedis redis = getRedis();
		try{
			return redis.hget(key, field);
		} finally {
			release(redis);
		}
	}

	public static final byte[] hget(String key, byte[] field) {
		Jedis redis = getRedis();
		try {
			return redis.hget(encode(key), field);
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

	public static final long hset(String key, String field, byte[] value) {
		Jedis redis = getRedis();
		try {
			return redis.hset(encode(key), encode(field), value);
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

	public static final String put(String key, byte[] value) {
		Jedis redis = getRedis();
		try {
			return redis.set(encode(key), value);
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

	public static final String put(String key, byte[] value, int expired) {
		Jedis redis = getRedis();
		try {
			return redis.setex(encode(key), expired, value);
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

	public static final long remove(byte[] key) {
		Jedis redis = getRedis();
		try {
			return redis.del(key);
		} finally {
			release(redis);
		}
	}

	public static final long hremove(String key, String field) {
		Jedis redis = getRedis();
		try {
			return redis.hdel(key, field);
		} finally {
			release(redis);
		}
	}

	public static final long hremove(String key, byte[] field) {
		Jedis redis = getRedis();
		try {
			return redis.hdel(encode(key), field);
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

	public static final boolean exists(byte[] key) {
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

	public static final boolean hexists(String key, byte[] field) {
		Jedis redis = getRedis();
		try {
			return redis.hexists(encode(key), field);
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

	public static final long expire(byte[] key, int sec) {
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

	public static final String rename(byte[] oldKey, byte[] newKey) {
		Jedis redis = getRedis();
		try {
			return redis.rename(oldKey, newKey);
		} finally {
			release(redis);
		}
	}

	public static final void release(Jedis jedis) {
//		RedisPool.returnResource(jedis);
		jedis.close();
	}
	
	public static final void destroy() {
		if (RedisPool != null) {
			RedisPool.destroy();
			RedisPool = null;
		}
	}

	public static final void publish(JSONObject json) {
		Jedis redis = getRedis();
		redis.publish("/server/login", json.toJSONString());
		release(redis);
	}
	
}
