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
import java.util.Date;
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
import org.springframework.util.StringUtils;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.query.domain.MoneyFlowResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class MoneyFlowService {
	private final static Logger logger = LogManager.getLogger(MoneyFlowService.class);
	@Autowired
	private MongoClient mongoClient;

	/**
	 * 货币流水数据处理
	 */
	public Page<MoneyFlowResult> getMoneyFlowResult(String playerId, String date, int type, String moneyId) {
		logger.info("查询游戏货币流水业务记录.playerId={}", playerId);
		List<MoneyFlowResult> result = new ArrayList<>();
		Bson match = Filters.eq("id", playerId);
		if (type > 0) {
			match = Filters.and(match, Filters.eq("operate", type));
		}
		if (!StringUtils.isEmpty(moneyId)) {
			match = Filters.and(match, Filters.eq("type", moneyId));
		}
		long startTime = System.nanoTime();
		MongoUtils.getCollection(mongoClient, "MoneyFlow" + date).find(match).forEach(new Block<Document>() {
			@Override
			public void apply(Document document) {
				MoneyFlowResult money = new MoneyFlowResult();
				money.setDate(new Date(document.getLong("_timestamp")));
				money.setId(document.getString("id"));
				money.setName(document.getString("name"));
				money.setType(document.getString("type"));
				money.setOperate(document.getInteger("operate"));
				money.setBefore(document.getInteger("before"));
				money.setMoney(document.getInteger("value"));
				money.setAfter(document.getInteger("after"));
				money.setOrigin(document.getInteger("origin"));
				result.add(money);
			}
		});
		long endTime = System.nanoTime();
		logger.info("MongoDB查询货币流水记录的时间 time={}", (endTime - startTime) / 100_0000f);
		return new PageImpl<>(result, new PageRequest(0, PageConstant.MAX_SIZE), result.size());
	}
}