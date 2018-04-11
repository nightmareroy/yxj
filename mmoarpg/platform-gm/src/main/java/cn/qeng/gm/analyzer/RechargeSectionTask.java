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

import cn.qeng.gm.module.data.service.DataRechargeService;
import cn.qeng.gm.util.DateUtils;

/**
 * 充值区间统计.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component
public class RechargeSectionTask extends AbstractTask {
	private final static Logger logger = LogManager.getLogger(RechargeSectionTask.class);

	/**
	 * 每晚0点17分45秒开始从mongo.Recharge统计充值区间数据写回mongo
	 */
	@Scheduled(cron = "45 17 0 * * ?")
	public void staticticsYesterdayRecharge() {
		// 默认分析昨日数据
		this.staticticsChargeSection(DateUtils.addDay(new Date(), -1));
	}

	public void staticticsChargeSection(Date today) {
		logger.info("开始统计{}的充值区间数据...", today);
		long start = DateUtils.calStartTime(today).getTime();
		long end = DateUtils.calStartTime(DateUtils.addDay(today, 1)).getTime();
		Bson match = Filters.and(Filters.gte("_timestamp", start), Filters.lt("_timestamp", end));

		HashMap<Integer, HashMap<Integer, Integer>> sections = new HashMap<>();
		MongoCollection<Document> collection = getCollection("Recharge");
		// 以充值的sid和id分组，即某玩家的某天多笔充值记录记做基数1
		Bson group = Aggregates.group(Document.parse("{\"sid\":\"$sid\",\"id\":\"$id\"}"), Accumulators.sum("rmb", "$money"));
		collection.aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				Document _id = (Document) t.get("_id");
				int sid = _id.getInteger("sid");
				String id = _id.getString("id");
				int rmb = t.getInteger("rmb", 0);
				statictics(sid, id, rmb, sections);
			}
		});

		// 将数据插入到统计充值区间的表中
		List<ReplaceOneModel<Document>> requests = new ArrayList<>(sections.size());
		String todayStr = DateUtils.formatyyyyMMdd(today);
		sections.forEach((k, v) -> {
			String _id = todayStr + "-" + k;
			Document data = new Document();
			data.append("_id", _id);
			data.append("sid", k);
			data.append("today", todayStr);
			final int[] section = DataRechargeService.SECTION;
			// { 1, 6, 30, 98, 198, 328, 648, 3240, 5000 };
			for (int i = 0; i < section.length; i++) {
				data.append(String.valueOf(section[i]), v.getOrDefault(section[i], 0));
			}
			System.out.println(data.toJson());
			requests.add(new ReplaceOneModel<>(Filters.eq("_id", _id), data, options));
		});
		this.getCollection("StatisticsRechargeSection").bulkWrite(requests);
		logger.info("统计昨天的充值区间数据完成...");
	}

	private void statictics(int sid, String id, int rmb, HashMap<Integer, HashMap<Integer, Integer>> sections) {
		final int[] section = DataRechargeService.SECTION;
		// { 1, 6, 30, 98, 198, 328, 648, 3240, 5000 };
		for (int i = 0; i < section.length; i++) {
			if (i < section.length - 1) {// 区间有下个区间的时候判断方法
				if (section[i] * 100 <= rmb && rmb < section[i + 1] * 100) {
					HashMap<Integer, Integer> data = sections.computeIfAbsent(sid, key -> new HashMap<>());
					data.put(section[i], data.getOrDefault(section[i], 0) + 1);
					break;
				}
			} else {
				if (rmb >= section[i] * 100) {
					HashMap<Integer, Integer> data = sections.computeIfAbsent(sid, key -> new HashMap<>());
					data.put(section[i], data.getOrDefault(section[i], 0) + 1);
					break;
				}
			}
		}
	}
}