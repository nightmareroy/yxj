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
package cn.qeng.gm.module.data.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.module.data.domain.DailyResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 每日报表
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class DailyService {
	private final static Logger logger = LogManager.getLogger(DailyService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	@Autowired
	protected MongoClient mongoClient;

	public List<DailyResult> getDailyStatistics(LocalDate start, LocalDate end, List<Integer> sidList) {
		MongoCollection<Document> collection = MongoUtils.getCollection(mongoClient, "DailyDataReport");
		Bson match = Filters.and(Filters.gte("today", formatter.format(start)), Filters.lte("today", formatter.format(end)));
		if (!sidList.isEmpty()) {
			match = Filters.and(match, Filters.in("sid", sidList));
		}
		// 日期，活跃角色，新增角色，平均在线，最高在线， 充值人数，充值次数，充值金额，
		Bson group = Aggregates.group("$today", Accumulators.sum("loginIdNum", "$loginIdNum"), Accumulators.sum("createIdNum", "$createIdNum"), //
				Accumulators.sum("maxOnlineNum", "$maxOnlineNum"), Accumulators.avg("avgOnlineNum", "$avgOnlineNum"), //
				Accumulators.sum("rechargeRmb", "$rechargeRmb"), Accumulators.sum("rechargeNum", "$rechargeNum"), Accumulators.sum("rechargeCount", "$rechargeCount"));

		long startTime = System.nanoTime();
		List<DailyResult> result = new ArrayList<>();
		collection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				DailyResult rs = new DailyResult();
				rs.setToday(doc.getString("_id"));
				rs.setLoginIdNum(doc.getInteger("loginIdNum", 0));
				rs.setCreateIdNum(doc.getInteger("createIdNum", 0));
				rs.setMaxOnlineNum(doc.getInteger("maxOnlineNum", 0));
				rs.setAvgOnlineNum(doc.getDouble("avgOnlineNum").intValue());
				rs.setRechargeRmb(doc.getInteger("rechargeRmb", 0));
				rs.setRechargeNum(doc.getInteger("rechargeNum", 0));
				rs.setRechargeCount(doc.getInteger("rechargeCount", 0));
				result.add(rs);
			}
		});
		long endTime = System.nanoTime();
		logger.info("Mongo查询一次每日报表 exec={}", (endTime - startTime) / 100_0000F);
		return result;
	}
}
