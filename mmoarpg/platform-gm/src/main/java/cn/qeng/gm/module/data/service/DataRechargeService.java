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

import cn.qeng.gm.module.data.domain.RechargeSectionVO;
import cn.qeng.gm.module.data.domain.RechargeVO;
import cn.qeng.gm.util.MongoUtils;

/**
 * 充值相关统计业务逻辑类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class DataRechargeService {
	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private final static Logger logger = LogManager.getLogger(OnlineService.class);

	// 10个区间，后期有需求再改
	public static final int[] SECTION = new int[] { 1, 6, 30, 98, 198, 328, 648, 3240, 5000 };

	@Autowired
	private MongoClient mongoClient;

	public RechargeVO getChargeResult(LocalDate start, LocalDate end, List<Integer> sidList) {
		List<String> matchList = new ArrayList<>(31);
		for (LocalDate i = start; !i.equals(end); i = i.plusDays(1)) {
			matchList.add(i.format(formatter));
		}
		matchList.add(end.format(formatter));
		Bson match = Filters.in("today", matchList.toArray());
		if (!sidList.isEmpty()) {
			match = Filters.and(match, Filters.in("sid", sidList));
		}

		long startTime = System.nanoTime();

		List<String> days = new ArrayList<>();
		Map<String, Integer> rmbs = new HashMap<>();
		Map<String, Integer> nums = new HashMap<>();
		Map<String, Integer> counts = new HashMap<>();
		Bson group = Aggregates.group("$today", Accumulators.sum("totalRmb", "$rechargeRmb"), Accumulators.sum("totalNum", "$rechargeNum"), Accumulators.sum("totalCount", "$rechargeCount"));
		MongoUtils.getCollection(mongoClient, "DailyDataReport").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				String day = t.getString("_id");
				days.add(day);
				rmbs.put(day, t.getInteger("totalRmb", 0));
				nums.put(day, t.getInteger("totalNum", 0));
				counts.put(day, t.getInteger("totalCount", 0));
			}
		});
		days.sort((o1, o2) -> o1.compareTo(o2));

		RechargeVO result = new RechargeVO();
		for (String d : days) {
			result.getChargeDays().add(d);
			result.getChargeRmbs().add(rmbs.get(d));
			result.getChargeNums().add(nums.get(d));
			result.getChargeCounts().add(counts.get(d));
		}
		logger.info("MongoDB拉取基本数据所需时间 time={} ms", (System.nanoTime() - startTime) / 100_0000f);
		return result;
	}

	public List<RechargeSectionVO> getRechargeSectionResult(LocalDate start, LocalDate end, List<Integer> sidList) {
		List<String> matchList = new ArrayList<>(31);
		for (LocalDate i = start; !i.equals(end); i = i.plusDays(1)) {
			matchList.add(i.format(formatter));
		}
		matchList.add(end.format(formatter));
		Bson match = Filters.in("today", matchList.toArray());
		if (!sidList.isEmpty()) {
			match = Filters.and(match, Filters.in("sid", sidList));
		}
		List<RechargeSectionVO> result = new ArrayList<>();
		// 1, 6, 30, 98, 198, 328, 648, 3240, 5000
		Bson group = Aggregates.group("$today", Accumulators.sum("v1", "$1"), //
				Accumulators.sum("v6", "$6"), Accumulators.sum("v30", "$30"), //
				Accumulators.sum("v98", "$98"), Accumulators.sum("v198", "$198"), //
				Accumulators.sum("v328", "$328"), Accumulators.sum("v648", "$648"), //
				Accumulators.sum("v3240", "$3240"), Accumulators.sum("v5000", "$5000"));
		MongoUtils.getCollection(mongoClient, "StatisticsRechargeSection").aggregate(Arrays.asList(Aggregates.match(match), group)).forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				RechargeSectionVO vo = new RechargeSectionVO();
				vo.setDate(t.getString("_id"));
				vo.setV1(t.getInteger("v1", 0));
				vo.setV6(t.getInteger("v6", 0));
				vo.setV30(t.getInteger("v30", 0));
				vo.setV98(t.getInteger("v98", 0));
				vo.setV198(t.getInteger("v198", 0));
				vo.setV328(t.getInteger("v328", 0));
				vo.setV648(t.getInteger("v648", 0));
				vo.setV3240(t.getInteger("v3240", 0));
				vo.setV5000(t.getInteger("v5000", 0));
				result.add(vo);
			}
		});
		result.sort((o2, o1) -> o1.getDate().compareTo(o2.getDate()));
		return result;
	}
}