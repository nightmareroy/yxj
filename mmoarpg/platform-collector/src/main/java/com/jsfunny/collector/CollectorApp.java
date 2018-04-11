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
package com.jsfunny.collector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsfunny.collector.core.Redis;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import redis.clients.jedis.JedisPubSub;

/**
 * 采集器应用启动入口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class CollectorApp {
	private static final Logger logger = LogManager.getLogger(CollectorApp.class);
	protected static final UpdateOptions options = new UpdateOptions();

	static {
		options.upsert(true);
	}

	private static MongoClient mongoClient;
	private static MongoDatabase database;
	private static Executor ansycExec;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(CollectorApp.class.getClassLoader().getResourceAsStream("application.properties"));

		String redishost = prop.getProperty("publish.redis.host", "127.0.0.1");
		int redisport = Integer.parseInt(prop.getProperty("publish.redis.port", "6379"));
		String password = prop.getProperty("publish.redis.password", null);
		int dbIndex = Integer.parseInt(prop.getProperty("publish.redis.db", "10"));

		mongoClient = new MongoClient(new MongoClientURI(prop.getProperty("mongo.url", "mongodb://127.0.0.1:12580/")));
		database = mongoClient.getDatabase("yxj");

		ansycExec = Executors.newFixedThreadPool(8);

		// 死死的订着这个Redis...
		while (true) {
			Redis redis = new Redis(redishost, redisport, password, dbIndex);
			redis.psubscribe(new JedisPubSub() {
				@Override
				public void onPMessage(String pattern, String channel, String message) {
					ansycExec.execute(() -> {
						try {
							save(channel, message);
						} catch (Exception e) {
							logger.error(e);
						}
					});
				}
			}, "data.*");
		}
	}

	public static void save(String channel, String message) throws ParseException {
		int index = channel.lastIndexOf(".");
		if (index > 0) {
			logger.info("channel={},message={}", channel, message);
			String collectionName = channel.substring(index + 1);

			DBObject replacement = BasicDBObject.parse(message);

			// date 转化为 _timestamp
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = sdf.parse(replacement.removeField("date").toString());// 事发时间
			replacement.put("_timestamp", now.getTime());

			MongoCollection<DBObject> collection = null;
			if (replacement.containsField("_one_day")) {
				sdf = new SimpleDateFormat("yyyyMMdd");
				collection = database.getCollection(collectionName + sdf.format(now), DBObject.class);
			} else {
				collection = database.getCollection(collectionName, DBObject.class);
			}

			String _id = replacement.get("_id").toString();
			collection.replaceOne(Filters.eq("_id", _id), replacement, options);
		}
	}
}