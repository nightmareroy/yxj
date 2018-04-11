package com.wanniu.core.redis;

import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.wanniu.core.logfs.Out;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class Redis {
	public static final int DEFAULT_TIMEOUT = 5000;
	public static final int DEFAULT_DATABASE = 0;
	private JedisPool pool;

	public Redis(String host, int port) {
		this(host, port, DEFAULT_DATABASE);
	}

	public Redis(String host, int port, int database) {
		this(host, port, DEFAULT_TIMEOUT, null, database);
	}

	public Redis(String host, int port, String password) {
		this(host, port, DEFAULT_TIMEOUT, password, DEFAULT_DATABASE);
	}

	public Redis(String host, int port, String password, int dbIndex) {
		this(host, port, DEFAULT_TIMEOUT, password, dbIndex);
	}

	/**
	 * 初始化Redis辅助类.
	 * 
	 * @param host IP
	 * @param port 端口
	 * @param index 库Index
	 */
	public Redis(String host, int port, int timeout, String password, int index) {
		// 通过config配置连接池参数
		this(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL, host, port, timeout, password, index);
	}

	/** @param connection 连接数 */
	public Redis(int connection, String host, int port, int timeout, String password, int database) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(connection);
		config.setMaxIdle(connection);
		pool = new JedisPool(config, host, port, timeout, password, database);
		Out.info("Redis 连接信息:host=", host, ",port=", port, ",database=", database, ",timeout=", timeout, "ms,password=", password);
		ping();
	}

	public void ping() {
		try (Jedis j = pool.getResource()) {
			String ret = j.ping();
			if (ret.equals("PONG")) {
				Out.info("Redis服务器连接正常");
			} else {
				Out.error("Redis服务器连接异常");
			}
		}
	}

	public void del(String key) {
		try (Jedis j = pool.getResource()) {
			j.del(key);
		}
	}

	// ------------------------------------------订阅/发布------------------------------------
	/**
	 * 将信息 message 发送到指定的频道 channel 。
	 * 
	 * @version >= 2.0.0 时间复杂度： O(N+M)，其中 N 是频道channel 的订阅者数量，而 M
	 *          则是使用模式订阅(subscribed patterns)的客户端的数量。
	 * @return 接收到信息 message 的订阅者数量。
	 */
	public long publish(String channel, String message) {
		try (Jedis j = pool.getResource()) {
			long ret = j.publish(channel, message);
			return ret;
		}
	}

	/**
	 * 订阅给定的一个或多个频道的信息。
	 * 
	 * @version >= 2.0.0 时间复杂度： O(N)，其中 N 是订阅的频道的数量。
	 * @return 接收到的信息(请参见下面的代码说明)。
	 */
	public void subscribe(JedisPubSub jedisPubSub, String... channel) {
		try (Jedis j = pool.getResource()) {
			j.subscribe(jedisPubSub, channel);
		}
	}

	/**
	 * 订阅一个或多个符合给定模式的频道。
	 * 
	 * 每个模式以 * 作为匹配符，比如 it* 匹配所有以 it 开头的频道( it.news 、 it.blog 、 it.tweets 等等)，
	 * news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类。
	 * 
	 * @version >= 2.0.0 时间复杂度： O(N)， N 是订阅的模式的数量。
	 * @return 接收到的信息
	 */
	public void psubscribe(JedisPubSub jedisPubSub, String... channel) {
		try (Jedis j = pool.getResource()) {
			j.psubscribe(jedisPubSub, channel);
		}
	}

	/**
	 * 获取当前数据库里的Keys的数量.
	 * 
	 * @return Keys的数量
	 */
	public long dbSize() {
		try (Jedis j = pool.getResource()) {
			long ret = j.dbSize();
			return ret;
		}
	}

	/** 退出客户端 */
	public String quit() {
		try (Jedis j = pool.getResource()) {
			String ret = j.quit();
			return ret;
		}
	}

	public void destory() {
		pool.destroy();
	}

	public void close() {
		pool.close();
	}

	/**
	 * 返回一个jedis实例，用于自己实现pipeline、multi、watch等
	 * 
	 * 注意：必须调用{@link Redis#returnJedis} 返回资源
	 */
	public Jedis getJedis() {
		return pool.getResource();
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public String get(final String key) {
		try (Jedis j = pool.getResource()) {
			return j.get(key);
		}
	}

	public String get(final String key, String defaullt) {
		try (Jedis j = pool.getResource()) {
			String s = j.get(key);
			if (s == null) {
				return defaullt;
			}
			return s;
		}
	}

	public int scard(final String key) {
		try (Jedis j = pool.getResource()) {
			Long value = j.scard(key);
			return value == null ? 0 : value.intValue();
		}
	}

	public Long scardx(final String key) {
		try (Jedis j = pool.getResource()) {
			return j.scard(key);
		}
	}

	public void sadd(final String key, String... members) {
		try (Jedis j = pool.getResource()) {
			j.sadd(key, members);
		}
	}

	public Boolean sismember(final String key, String member) {
		try (Jedis j = pool.getResource()) {
			return j.sismember(key, member);
		}
	}

	public Long hincrBy(final String key, final String field, final long value) {
		try (Jedis j = pool.getResource()) {
			return j.hincrBy(key, field, value);
		}
	}

	public Map<String, String> hgetAll(String key) {
		try (Jedis j = pool.getResource()) {
			return j.hgetAll(key);
		}
	}

	public void hset(final String key, final String field, final String value) {
		try (Jedis j = pool.getResource()) {
			j.hset(key, field, value);
		}
	}

	public void hmset(final String key, final Map<String, String> hash) {
		try (Jedis j = pool.getResource()) {
			j.hmset(key, hash);
		}
	}

	public Long hdel(final String key, final String... fields) {
		try (Jedis j = pool.getResource()) {
			return j.hdel(key, fields);
		}
	}

	public Object eval(String script) {
		try (Jedis j = pool.getResource()) {
			return j.eval(script);
		}
	}

	public Set<String> smembers(final String key) {
		try (Jedis j = pool.getResource()) {
			return j.smembers(key);
		}
	}

	public Boolean hexists(final String key, final String field) {
		try (Jedis j = pool.getResource()) {
			return j.hexists(key, field);
		}
	}

	public Long hsetnx(final String key, final String field, final String value) {
		try (Jedis j = pool.getResource()) {
			return j.hsetnx(key, field, value);
		}
	}

	public String hget(final String key, final String field) {
		try (Jedis j = pool.getResource()) {
			return j.hget(key, field);
		}
	}

	public String save() {
		try (Jedis j = pool.getResource()) {
			return j.save();
		}
	}
}