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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wanniu.util.DateUtil;

import cn.qeng.gm.module.data.domain.OnlineResult;
import cn.qeng.gm.module.data.domain.OnlineResult.Online;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class OnlineService {
	private final static Logger logger = LogManager.getLogger(OnlineService.class);

	@Autowired
	private MongoClient mongoClient;

	/**
	 * 在线人数获取
	 */
	public OnlineResult getOnlineResult(LocalDate today, List<Integer> sidList) {
		if (today == null) {
			today = LocalDate.now();
		}

		Date now = DateUtil.format(today.format(DateTimeFormatter.ofPattern(DateUtil.F_yyyyMMdd)), DateUtil.F_yyyyMMdd);

		long startTime1 = System.nanoTime();

		// 分组求和语句
		Bson group = Aggregates.group("$minute", Accumulators.sum("number", "$pcount"));
		Map<Integer, Integer> yesterDayData = new HashMap<>();// 存放从mongo获取的昨日数据
		Map<Integer, Integer> toDayData = new HashMap<>();// 存放从mongo获取的今日数据
		{// 当前日期的前一天数据
			Date startTime = DateUtil.getDateBeforeIn0(now, 1).getTime();
			Date endTime = DateUtil.getDateAfterIn0(now, 0).getTime();
			Bson match = Filters.and(Filters.gte("_timestamp", startTime.getTime()), Filters.lte("_timestamp", endTime.getTime()));
			if (!sidList.isEmpty()) {
				match = Filters.and(match, Filters.in("sid", sidList));
			}
			MongoUtils.getCollection(mongoClient, "OnlineCount").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
				@Override
				public void apply(Document t) {
					yesterDayData.put(t.getInteger("_id"), t.getInteger("number"));
				}
			});
		}
		{// 当前的数据
			Date startTime = DateUtil.getDateBeforeIn0(now, 0).getTime();
			Date endTime = DateUtil.getDateAfterIn0(now, 1).getTime();
			Bson match = Filters.and(Filters.gte("_timestamp", startTime.getTime()), Filters.lte("_timestamp", endTime.getTime()));
			if (!sidList.isEmpty()) {
				match = Filters.and(match, Filters.in("sid", sidList));
			}
			MongoUtils.getCollection(mongoClient, "OnlineCount").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
				@Override
				public void apply(Document t) {
					toDayData.put(t.getInteger("_id"), t.getInteger("number"));
				}
			});
		}

		// 获取昨天
		OnlineResult result = new OnlineResult(today);
		// 每5分钟一次将从mongo中获取的数据拼装到result中，如果没有数据，则默认为0人
		for (int i = 0; i < 24 * 60; i = i + 5) {
			String localTime = String.format("%02d", i / 60) + "时" + String.format("%02d", i % 60) + "分";
			int todayData = toDayData.get(i) == null ? 0 : toDayData.get(i);
			int yesterData = yesterDayData.get(i) == null ? 0 : yesterDayData.get(i);
			result.getOnlineData().add(new Online(result.getDate().toString(), localTime, todayData, yesterData));
		}
		long endTime1 = System.nanoTime();
		logger.info("MongoDB查询实时在线玩家 time={}", (endTime1 - startTime1) / 100_0000f);
		return result;
	}
}