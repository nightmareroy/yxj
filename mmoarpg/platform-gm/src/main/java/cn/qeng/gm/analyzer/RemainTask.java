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
package cn.qeng.gm.analyzer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.util.DateUtils;

/**
 * 留存率统计
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component
public class RemainTask extends AbstractTask {
	private final static Logger logger = LogManager.getLogger(RemainTask.class);

	/**
	 * 每晚1点1分10秒开始从mongo统计留存率写回mongo
	 */
	@Scheduled(cron = "45 0/30 * * * ?")
	public void staticticsChargeSection() {
		logger.info("开始统计用户留存数据...");
		// 更新今日数据
		try {
			updateRemain(new Date());
		} catch (Exception e) {
			logger.error("staticticsChargeSection updateRemain today error:", e.getMessage());
		}
		logger.info("统计用户留存数据结束...");
	}

	/**
	 * 每天2点33分10秒开始修正昨日留存率数据
	 */
	@Scheduled(cron = "10 33 2 * * ?")
	public void updateRemainYesterDay() {
		// 修正昨日数据
		try {
			updateRemain(DateUtils.addDay(new Date(), -1));
		} catch (Exception e) {
			logger.error("staticticsChargeSection updateRemain yesterDay error:", e);
		}
		logger.info("统计用户留存数据结束...");
	}

	private void updateRemain(Date date) {
		updateRemain(date, 2);// 次日留存
		updateRemain(date, 3);// 3日留存
		updateRemain(date, 4);// 4日留存
		updateRemain(date, 5);// 5日留存
		updateRemain(date, 6);// 6日留存
		updateRemain(date, 7);// 7日留存
	}

	public void updateRemain(Date today, int day) {
		String dateSuffix = DateUtils.formatyyyyMMdd(today);
		// 那天注册的人，在今天登录...
		MongoCollection<Document> createRoleCollection = getCollection("CreatePlayer");
		MongoCollection<Document> roleLoginCollection = getCollection("Login" + dateSuffix);

		// 通用条件...
		long start = DateUtils.calStartTime(DateUtils.addDay(today, -day + 1)).getTime();
		long end = DateUtils.calStartTime(DateUtils.addDay(today, -day + 2)).getTime();
		Bson match = Filters.and(Filters.gte("_timestamp", start), Filters.lt("_timestamp", end));

		// 分组
		Bson group = Aggregates.group("$sid", Accumulators.addToSet("list", "$id"));

		MongoCollection<Document> retentionCollection = getCollection("StatisticsRemain");// 留存率集合
		// 处理结果集...
		createRoleCollection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				// 这些人在今天登录的有多少...
				List<?> createRoleList = t.get("list", List.class);

				if (!createRoleList.isEmpty()) {
					int sid = t.getInteger("_id");
					String _id = dateSuffix + "-" + sid;// 日期+区服（20160523-1002）

					AtomicInteger count = new AtomicInteger(0);
					roleLoginCollection.distinct("id", Filters.in("id", createRoleList), String.class).forEach(new Block<String>() {
						@Override
						public void apply(String t) {
							count.incrementAndGet();
						}
					});

					Document summary = retentionCollection.find(Filters.eq("_id", _id)).first();
					if (summary == null) {
						summary = new Document();
						summary.append("_id", _id);
						summary.append("sid", sid);
						summary.append("date", dateSuffix);
						summary.append("day" + day, count.get());
						summary.append("createNum", createRoleList.size());
						retentionCollection.insertOne(summary);
					} else {
						retentionCollection.updateOne(Filters.eq("_id", _id), new Document("$set", new Document("day" + day, count.get())));

						// 创建人数不等 更新一下此属性...
						if (summary.getInteger("createNum") != createRoleList.size()) {
							retentionCollection.updateOne(Filters.eq("_id", _id), new Document("$set", new Document("createNum", createRoleList.size())));
						}
					}
				}
			}
		});
	}
}