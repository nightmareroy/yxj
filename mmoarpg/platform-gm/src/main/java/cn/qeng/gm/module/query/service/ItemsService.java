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
import cn.qeng.gm.module.query.domain.ItemsResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 道具流水业务处理类
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class ItemsService {
	private final static Logger logger = LogManager.getLogger(ItemsService.class);
	@Autowired
	private MongoClient mongoClient;

	/**
	 * 道具流水记录
	 */
	public Page<ItemsResult> queryItemsResult(String date, String playerId, int operate, String itemcode) {
		logger.info("查询游戏物品流水业务记录.playerId={}", playerId);
		List<ItemsResult> result = new ArrayList<>();
		long startTime = System.nanoTime();
		Bson filters = Filters.eq("id", playerId);
		if (operate > 0) {
			filters = Filters.and(filters, Filters.eq("operate", operate));
		}
		if (!StringUtils.isEmpty(itemcode)) {
			filters = Filters.and(filters, Filters.eq("itemcode", itemcode));
		}
		MongoUtils.getCollection(mongoClient, "ItemFlow" + date).find(filters).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				ItemsResult item = new ItemsResult();
				item.setDate(new Date(t.getLong("_timestamp")));
				item.setId(t.getString("id"));
				item.setName(t.getString("name"));
				item.setOperate(t.getInteger("operate"));
				item.setItemcode(t.getString("itemcode"));
				item.setCount(t.getInteger("count"));
				item.setBind(t.getBoolean("bind"));
				item.setOrigin(t.getInteger("origin"));
				result.add(item);
			}
		});
		long endTime = System.nanoTime();
		logger.info("MongoDB查询物品记录的时间 time={}", (endTime - startTime) / 100_0000f);
		return new PageImpl<>(result, new PageRequest(0, PageConstant.MAX_SIZE), result.size());
	}
}