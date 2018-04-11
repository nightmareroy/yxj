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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mongodb.client.model.ReplaceOneModel;

import cn.qeng.gm.util.DateUtils;

/**
 * 每日数据报表任务.
 *
 * @author 小流氓(mingkai.zhou@mokun.net)
 */
@Component
public class DailyDataReportTask extends AbstractTask {
	private final static Logger logger = LogManager.getLogger(DailyDataReportTask.class);

	/**
	 * 任务每半小时执行一次
	 */
	@Scheduled(cron = "23 0/30 * * * ?")
	public void statistics() {
		logger.info("每日数据报表分析开始...");
		this.analyze(new Date());
		logger.info("每日数据报表分析完成...");
	}

	/**
	 * 修复一下昨天的数据.
	 * <p>
	 * 0点10分20秒
	 */
	@Scheduled(cron = "20 10 0 * * ?")
	public void fixYesterday() {
		logger.info("修复昨天数据报表分析开始...");
		this.analyze(DateUtils.addDay(new Date(), -1));
		logger.info("修复昨天数据报表分析完成...");
	}

	public void analyze(Date today) {
		String todayStr = DateUtils.formatyyyyMMdd(today);
		logger.info("分析数据,{}", todayStr);
		// 通用条件...
		long start = DateUtils.calStartTime(today).getTime();
		long end = DateUtils.calStartTime(DateUtils.addDay(today, 1)).getTime();
		Bson match = Filters.and(Filters.gte("_timestamp", start), Filters.lt("_timestamp", end));

		Map<String, Document> result = new HashMap<>();

		// 活跃玩家数：当日登录人数
		analyzeLogin(match, todayStr, result);

		// 新增注册人数：当日注册玩家数
		analyzeCreateRole(match, todayStr, result);

		// 平均在线 统计每记录点的在线人数，然后平均
		// 最高在线 取当日最高同时在线人数
		this.analyzeOnline(match, todayStr, result);

		// 充值人数：充值玩家数量（去重）
		// 充值次数：所有的订单数量
		// 充值总金额 ：这个我就不解释了
		this.analyzeRecharge(match, todayStr, result);

		List<ReplaceOneModel<Document>> requests = new ArrayList<>(result.size());
		result.forEach((k, v) -> requests.add(new ReplaceOneModel<>(Filters.eq("_id", k), v, options)));
		this.getCollection("DailyDataReport").bulkWrite(requests);
	}

	private void analyzeLogin(Bson match, String todayStr, Map<String, Document> result) {
		MongoCollection<Document> collection = this.getCollection("Login" + todayStr);
		Bson group = Aggregates.group("$sid", Accumulators.addToSet("idList", "$id"), Accumulators.addToSet("uidList", "$uid"), Accumulators.addToSet("macList", "$mac"), Accumulators.addToSet("ipList", "$ip"));
		collection.aggregate(Arrays.asList(group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				int sid = doc.getInteger("_id");
				Document data = buildData(result, todayStr, sid);
				data.append("loginIdNum", doc.get("idList", List.class).size());
				data.append("loginUidNum", doc.get("uidList", List.class).size());
				data.append("loginMacNum", doc.get("macList", List.class).size());
				data.append("loginIpNum", doc.get("ipList", List.class).size());
			}
		});
	}

	private void analyzeRecharge(Bson match, String todayStr, Map<String, Document> result) {
		MongoCollection<Document> collection = this.getCollection("Recharge");
		// 充值人数：充值玩家数量（去重）
		// 充值次数：所有的订单数量
		// 充值总金额 ：这个我就不解释了
		Bson group = Aggregates.group("$sid", Accumulators.sum("totalRmb", "$money"), Accumulators.sum("totalCount", 1), Accumulators.addToSet("list", "$id"));
		collection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				int sid = t.getInteger("_id");
				Document data = buildData(result, todayStr, sid);
				data.append("rechargeRmb", t.getInteger("totalRmb", 0));// 充值金额
				data.append("rechargeNum", t.get("list", List.class).size());// 充值人数
				data.append("rechargeCount", t.getInteger("totalCount", 0));// 充值次数
			}
		});
	}

	private void analyzeOnline(Bson match, String todayStr, Map<String, Document> result) {
		MongoCollection<Document> collection = this.getCollection("OnlineCount");
		Bson group = Aggregates.group("$sid", Accumulators.max("max_online", "$pcount"), Accumulators.avg("avg_online", "$pcount"));
		collection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				int sid = t.getInteger("_id");
				Document data = buildData(result, todayStr, sid);
				data.append("avgOnlineNum", t.getDouble("avg_online").intValue());// 平均在线人数
				data.append("maxOnlineNum", t.getInteger("max_online"));// 最大在线人数
			}
		});
	}

	/**
	 * 计算今日创角数量.
	 */
	private void analyzeCreateRole(Bson match, String todayStr, Map<String, Document> result) {
		MongoCollection<Document> collection = this.getCollection("CreatePlayer");
		Bson group = Aggregates.group("$sid", Accumulators.addToSet("idList", "$id"), Accumulators.addToSet("uidList", "$uid"), Accumulators.addToSet("macList", "$mac"), Accumulators.addToSet("ipList", "$ip"));
		collection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				int sid = doc.getInteger("_id");
				Document data = buildData(result, todayStr, sid);
				data.append("createIdNum", doc.get("idList", List.class).size());
				data.append("createUidNum", doc.get("uidList", List.class).size());
				data.append("createMacNum", doc.get("macList", List.class).size());
				data.append("createIpNum", doc.get("ipList", List.class).size());
			}
		});
	}

	private Document buildData(Map<String, Document> result, String todayStr, int sid) {
		return result.computeIfAbsent(todayStr + "-" + sid, key -> {
			Document data = new Document();
			data.put("_id", key);
			data.put("month", todayStr.substring(0, 6));
			data.put("today", todayStr);
			data.put("sid", sid);
			return data;
		});
	}
}