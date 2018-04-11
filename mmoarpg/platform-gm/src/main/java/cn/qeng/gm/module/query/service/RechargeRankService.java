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
package cn.qeng.gm.module.query.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.query.domain.RechargeRankResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class RechargeRankService {
	private final static Logger logger = LogManager.getLogger(RechargeRankService.class);
	@Autowired
	private MongoClient mongoClient;

	public Page<RechargeRankResult> listRank(List<Integer> sidList) {
		logger.info("查询充值排名.serverIds={}", sidList);
		long startTime = System.nanoTime();
		// 分组条件
		Bson group = Aggregates.group("$id", Accumulators.sum("totalRmb", "$money"), Accumulators.sum("count", 1), Accumulators.last("name", "$name"));
		List<Bson> pipeline = null;
		if (sidList.isEmpty()) {
			pipeline = Arrays.asList(group);
		} else {
			pipeline = Arrays.asList(Aggregates.match(Filters.in("sid", sidList)), group);
		}
		List<RechargeRankResult> result = new ArrayList<>();
		MongoUtils.getCollection(mongoClient, "Recharge").aggregate(pipeline).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				RechargeRankResult rank = new RechargeRankResult();
				rank.setId(t.getString("_id"));
				rank.setName(t.getString("name"));
				rank.setTotalRmb(t.getInteger("totalRmb"));
				rank.setCount(t.getInteger("count"));
				result.add(rank);
			}
		});
		long endTime = System.nanoTime();
		logger.info("MongoDB查询充值排名的时间 time={}", (endTime - startTime) / 100_0000f);
		return new PageImpl<>(result, new PageRequest(0, PageConstant.MAX_SIZE), result.size());
	}
}