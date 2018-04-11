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
package cn.qeng.gm.module.monitor.service;

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

import cn.qeng.gm.module.monitor.domain.MoneyStat;
import cn.qeng.gm.module.monitor.domain.PacketStat;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class ShareService {
	private final static Logger logger = LogManager.getLogger(ShareService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	@Autowired
	protected MongoClient mongoClient;

	/**
	 * 获取货币所使用分布的统计数据.
	 */
	public List<MoneyStat> getMoneyUseDataByRoleId(LocalDate today, String type, String roleId) {
		List<MoneyStat> result = new ArrayList<>();
		// 分组条件
		Bson match = Filters.and(Filters.eq("id", roleId), Filters.eq("operate", 1), Filters.eq("type", type));
		Bson group = Aggregates.group("$origin", Accumulators.sum("totalRmb", "$value"), Accumulators.sum("totalCount", 1));
		long startTime = System.nanoTime();
		MongoUtils.getCollection(mongoClient, "MoneyFlow" + today.format(formatter)).aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				MoneyStat stat = new MoneyStat();
				stat.setReason(t.getInteger("_id"));
				stat.setTotalNum((int) MongoUtils.getLong(t, "totalRmb"));
				stat.setCount(t.getInteger("totalCount", 0));
				result.add(stat);
			}
		});
		logger.info("MongoDB统计个人货币消耗分布情况所需时间 time={} ms", (System.nanoTime() - startTime) / 100_0000f);
		return result;
	}

	/**
	 * 获取封包所使用分布的统计数据.
	 */
	public List<PacketStat> getPacketDataByRoleId(String roleId) {
		List<PacketStat> result = new ArrayList<>();
		// 分组条件
		Bson match = Filters.eq("id", roleId);
		Bson group = Aggregates.group("$route", Accumulators.sum("totalCount", "$count"));
		long startTime = System.nanoTime();
		MongoUtils.getCollection(mongoClient, "PacketMonitor").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				PacketStat stat = new PacketStat();
				stat.setRoute(t.getString("_id"));
				stat.setCount(t.getInteger("totalCount", 0));
				result.add(stat);
			}
		});
		logger.info("MongoDB统计个人封包消耗分布情况所需时间 time={} ms", (System.nanoTime() - startTime) / 100_0000f);
		return result;
	}
}