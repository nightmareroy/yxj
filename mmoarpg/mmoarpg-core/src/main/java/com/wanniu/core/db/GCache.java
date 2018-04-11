package com.wanniu.core.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.GConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

/**
 * 功能描述：Redis中间件缓存基础服务<br>
 * （依赖jedis和commons-pool）
 * 
 * @author agui
 */
public class GCache {

	private static int REDIS_CONN_COUTN = GConfig.getInstance().getInt("server.redis.total", 5);

	private static JedisPool RedisPool;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的
		config.setTestOnBorrow(GConfig.getInstance().getBoolean("server.redis.testonborrow", true));
		// 在return给pool时，是否提前进行validate操作
		config.setTestOnReturn(GConfig.getInstance().getBoolean("server.redis.testonreturn", true));
		config.setMaxIdle(GConfig.getInstance().getInt("server.redis.maxidle", 20));
		config.setMaxWaitMillis(GConfig.getInstance().getInt("server.redis.maxwait", 1000));
		config.setMaxTotal(REDIS_CONN_COUTN);

		String redisHost = GConfig.getInstance().get("server.redis.host", "127.0.0.1");
		int redisPort = GConfig.getInstance().getInt("server.redis.port", 6379);
		String pwd = GConfig.getInstance().get("server.redis.password");
		int db = GConfig.getInstance().getInt("server.redis.db", 0);
		int timeout = GConfig.getInstance().getInt("server.redis.timeout", 2000);
		RedisPool = new JedisPool(config, redisHost, redisPort, timeout, pwd != null ? pwd.trim() : null, db);
	}

	protected GCache() {}

	// private static Set<String> use_redis = new ConcurrentSkipListSet<>();
	// public static String getTraceInfo() {
	// StringBuffer sb = new StringBuffer();
	// StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
	// for (StackTraceElement e : stacks) {
	// if (e.getClassName().startsWith("com.wanniu.game")) {
	// sb.append(e.getClassName()).append(".").append(e.getMethodName());
	// break;
	// }
	// }
	// return sb.toString();
	// }

	public static final Jedis getRedis() {
		// if (GGame.DEBUG) {
		// String trace = getTraceInfo();
		// if (use_redis.contains(trace)) {
		// Out.error(trace, "线程中有redis连接未释放");
		// new Exception().printStackTrace();
		// }
		// if (use_redis.size() > REDIS_CONN_COUTN) {
		// Out.error("======================================================================");
		// for (String name : use_redis) {
		// Out.error(name);
		// }
		// Out.error("======================================================================");
		// }
		// use_redis.add(trace);
		// }
		return RedisPool.getResource();
	}

	public static final void release(Jedis redis) {
		// RedisPool.returnResource(redis);
		if (redis != null)
			redis.close();
		// if (GGame.DEBUG) {
		// use_redis.remove(getTraceInfo());
		// }
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
		try {
			return redis.hget(key, field);
		} finally {
			release(redis);
		}
	}

	public static final long hincr(String key, String field, long v) {
		Jedis redis = getRedis();
		try {
			return redis.hincrBy(key, field, v);
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

	public static final String hmset(String key, Map<String, String> map) {
		Jedis redis = getRedis();
		try {
			return redis.hmset(key, map);
		} finally {
			release(redis);
		}
	}

	public static final Long zadd(String key, double score, String member) {
		try (Jedis redis = getRedis()) {
			return redis.zadd(key, score, member);
		}
	}

	public static final Long zadd(String key, Map<String, Double> scoreMembers) {
		try (Jedis redis = getRedis()) {
			return redis.zadd(key, scoreMembers);
		}
	}

	public static Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		try (Jedis redis = getRedis()) {
			return redis.zrevrangeWithScores(key, start, end);
		}
	}

	public static Set<String> zrevrange(String key, long start, long end) {
		try (Jedis redis = getRedis()) {
			return redis.zrevrange(key, start, end);
		}
	}

	public static Long zrevrank(String key, String member) {
		try (Jedis redis = getRedis()) {
			return redis.zrevrank(key, member);
		}
	}

	public static Double zscore(String key, String member) {
		try (Jedis redis = getRedis()) {
			return redis.zscore(key, member);
		}
	}

	public static Long zrem(String key, String member) {
		try (Jedis redis = getRedis()) {
			return redis.zrem(key, member);
		}
	}

	public static Long del(String key) {
		try (Jedis redis = getRedis()) {
			return redis.del(key);
		}
	}

	public static final long hsetnx(String key, String field, String value) {
		Jedis redis = getRedis();
		try {
			return redis.hsetnx(key, field, value);
		} finally {
			release(redis);
		}
	}

	public static final void destroy() {
		if (RedisPool != null) {
			RedisPool.destroy();
			RedisPool = null;
		}
	}
}
