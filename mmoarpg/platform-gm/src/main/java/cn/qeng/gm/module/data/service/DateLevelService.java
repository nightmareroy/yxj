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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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

import cn.qeng.gm.module.data.domain.DataLevelVO;
import cn.qeng.gm.util.DateUtils;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class DateLevelService {
	private final static Logger logger = LogManager.getLogger(DateLevelService.class);

	@Autowired
	private MongoClient mongoClient;

	/**
	 * 角色等级分布获取
	 */
	public List<DataLevelVO> getLevelResult(Date today, List<Integer> sidList) {
		List<DataLevelVO> result = new ArrayList<>();
		Bson group = Aggregates.group("$level", Accumulators.sum("totalCount", 1));

		List<? extends Bson> pipeline;
		// 没有选择日期，全部...
		if (today == null) {
			if (sidList.isEmpty()) {
				pipeline = Arrays.asList(group);
			} else {
				pipeline = Arrays.asList(Aggregates.match(Filters.in("sid", sidList)), group);
			}
		}
		// 有日期，只要那一天的创建的角色
		else {
			HashSet<String> roleIds = getCreateRoleIdSet(today, sidList);

			if (sidList.isEmpty()) {
				pipeline = Arrays.asList(Aggregates.match(Filters.in("id", roleIds)), group);
			} else {
				pipeline = Arrays.asList(Aggregates.match(Filters.and(Filters.in("sid", sidList), Filters.in("id", roleIds))), group);
			}

			DataLevelVO data = new DataLevelVO();
			data.setLevel(1);
			data.setCount(roleIds.size());
			result.add(data);
		}

		long startTime1 = System.nanoTime();
		MongoUtils.getCollection(mongoClient, "RoleUpgrade").aggregate(pipeline).forEach(new Block<Document>() {
			@Override
			public void apply(Document document) {
				DataLevelVO data = new DataLevelVO();
				data.setLevel(document.getInteger("_id"));
				data.setCount(document.getInteger("totalCount", 0));
				result.add(data);
			}
		});
		long endTime1 = System.nanoTime();
		logger.info("MongoDB查询等级分布时间 time={}", (endTime1 - startTime1) / 100_0000f);
		Collections.sort(result, (o1, o2) -> o1.getLevel() - o2.getLevel());
		return result;
	}

	// 获取所有次日登录的角色ID...
	private HashSet<String> getCreateRoleIdSet(Date today, List<Integer> sidList) {
		// 分组条件
		long start = DateUtils.calStartTime(today).getTime();
		long end = DateUtils.calStartTime(DateUtils.addDay(today, 1)).getTime();
		Bson match = Filters.and(Filters.gte("_timestamp", start), Filters.lt("_timestamp", end));
		if (!sidList.isEmpty()) {
			match = Filters.and(match, Filters.in("sid", sidList));
		}
		HashSet<String> result = new HashSet<>();
		MongoUtils.getCollection(mongoClient, "CreatePlayer").distinct("id", match, String.class).forEach(new Block<String>() {
			@Override
			public void apply(String id) {
				result.add(id);
			}
		});
		return result;
	}
}