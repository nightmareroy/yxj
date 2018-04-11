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

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.query.domain.PetUpgradeResult;
import cn.qeng.gm.util.MongoUtils;

/**
 * 宠物升级业务类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class PetUpgradeService {
	private final static Logger logger = LogManager.getLogger(PetUpgradeService.class);
	@Autowired
	private MongoClient mongoClient;

	/**
	 * 玩家升级记录数据
	 */
	public Page<PetUpgradeResult> getPetUpgradeResult(String playerId) {
		logger.info("查询宠物升级记录.playerId={}", playerId);
		List<PetUpgradeResult> result = new ArrayList<>();
		Bson match = Filters.eq("id", playerId);
		long startTime = System.nanoTime();

		MongoUtils.getCollection(mongoClient, "PetUpgrade").find(match).forEach(new Block<Document>() {
			@Override
			public void apply(Document document) {
				PetUpgradeResult data = new PetUpgradeResult();
				data.setDate(new Date(document.getLong("_timestamp")));
				data.setId(document.getString("id"));
				data.setName(document.getString("name"));
				data.setPetId(document.getInteger("petId"));
				data.setPetName(document.getString("petName"));
				data.setUpLevel(document.getInteger("upLevel"));
				data.setLevel(document.getInteger("level"));
				data.setExp(MongoUtils.getLong(document, "exp"));
				result.add(data);
			}
		});

		long endTime = System.nanoTime();
		logger.info("MongoDB查询宠物升级记录的时间 time={}", (endTime - startTime) / 100_0000f);
		return new PageImpl<>(result, new PageRequest(0, PageConstant.MAX_SIZE), result.size());
	}
}