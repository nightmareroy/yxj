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
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.module.data.domain.RemainResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class RemainService {
	private final static Logger logger = LogManager.getLogger(RemainService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	@Autowired
	protected MongoClient mongoClient;

	/**
	 * 获取七日留存
	 */
	public List<RemainResult> getRemain(LocalDate start, LocalDate end, List<Integer> sidList) {

		Bson match = Filters.and(Filters.gte("date", formatter.format(start)), Filters.lte("date", formatter.format(end)));
		if (!sidList.isEmpty()) {
			match = Filters.and(match, Filters.in("sid", sidList));
		}
		long startTime = System.nanoTime();

		Bson group = Aggregates.group("$date", Accumulators.sum("createNum", "$createNum"), Accumulators.sum("day2", "$day2"), Accumulators.sum("day3", "$day3"), //
				Accumulators.sum("day4", "$day4"), Accumulators.sum("day5", "$day5"), Accumulators.sum("day6", "$day6"), //
				Accumulators.sum("day7", "$day7"));

		List<RemainResult> result = new ArrayList<>();
		MongoUtils.getCollection(mongoClient, "StatisticsRemain").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				RemainResult r = new RemainResult();
				r.setDate(t.getString("_id"));
				r.setCreateNum(t.getInteger("createNum", -1));
				r.setRemain2(t.getInteger("day2", -1));
				r.setRemain3(t.getInteger("day3", -1));
				r.setRemain4(t.getInteger("day4", -1));
				r.setRemain5(t.getInteger("day5", -1));
				r.setRemain6(t.getInteger("day6", -1));
				r.setRemain7(t.getInteger("day7", -1));
				result.add(r);
			}
		});
		long endTime = System.nanoTime();
		logger.info("Mongo查询一次留存 exec={}", (endTime - startTime) / 100_0000F);
		return result;
	}
}
