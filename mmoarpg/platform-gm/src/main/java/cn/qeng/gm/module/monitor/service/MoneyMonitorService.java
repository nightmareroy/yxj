/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.monitor.MonitorConstant;
import cn.qeng.gm.module.monitor.domain.MoneyMonitor;
import cn.qeng.gm.util.MongoUtils;

/**
 * 货币监控业务处理
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class MoneyMonitorService {
	private final static Logger logger = LogManager.getLogger(MoneyMonitorService.class);
	@Autowired
	private MongoClient mongoClient;

	/**
	 * 货币监控处理
	 */
	public Page<MoneyMonitor> getMoneyMonitorResult(int page) {
		logger.info("查询玩家货币用于监控记录");
		long stateTime = System.nanoTime();
		MongoCollection<Document> collection = MongoUtils.getCollection(mongoClient, "MoneyMonitor");
		long count = collection.count();
		List<MoneyMonitor> result = new ArrayList<>((int) count);
		collection.find().sort(new BasicDBObject("EventTime", -1)).skip(page * PageConstant.MAX_SIZE).limit(PageConstant.MAX_SIZE).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				MoneyMonitor monitor = new MoneyMonitor();
				monitor.setState(MonitorConstant.MONEYMONITOR_APPROVAL);
				monitor.setDate(new Date(t.getLong("_timestamp")));
				monitor.setId(t.getString("id"));
				monitor.setName(t.getString("name"));
				monitor.setLevel(t.getInteger("level"));
				monitor.setType(t.getString("type"));
				monitor.setMoney(t.getInteger("today"));
				monitor.setThreshold(t.getInteger("threshold"));
				result.add(monitor);
			}
		});
		long endTime = System.nanoTime();
		logger.info("MongoDB查询MoneyMonitor时间 time={}", (endTime - stateTime) / 100_0000f);
		return new PageImpl<>(result, new PageRequest(page, PageConstant.MAX_SIZE), count);
	}

	public void delete(String id) {
		MongoUtils.getCollection(mongoClient, "MoneyMonitor").deleteMany(Filters.eq("id", id));
	}
}
