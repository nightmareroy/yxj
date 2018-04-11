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

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import cn.qeng.common.gm.monitor.MonitorConst;
import cn.qeng.common.gm.monitor.PayMonitor;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.util.DateUtils;

/**
 * 充值模块逻辑
 *
 * @author 小流氓(176543888@qq.com)
 */
@Service
public class ChargeService {
	private final static Logger logger = LogManager.getLogger(ChargeService.class);

	@Autowired
	private RedisManager redisManager;

	private static final BlockingQueue<DeferredResult<String>> cachequeue = new LinkedBlockingQueue<>(512);

	private volatile long totalRmb = 0;

	@PostConstruct
	public void init() {
		String today = DateUtils.formatyyyy_MM_dd(LocalDate.now());
		String value = redisManager.getMsgRedis().hget(MonitorConst.REDIS_KEY_PAY_INFO, today);
		if (!StringUtils.isEmpty(value)) {
			this.totalRmb = Long.parseLong(value);
		}
		logger.info("初始化总充值 today={}, totalRmb={}", today, totalRmb);
	}

	@Scheduled(initialDelay = 30_000, fixedDelay = 30_000)
	public void refresh() {
		if (cachequeue.isEmpty()) {
			return;
		}

		this.sendData(cachequeue, formatTotalRmb());
		logger.info("定时释放拉取请求 totalRmb={}", totalRmb);
	}

	/**
	 * 请求放队列中去...
	 */
	public DeferredResult<String> asyncRealtimeRechargeCall(boolean cache) {
		DeferredResult<String> deferredTodayResult = new DeferredResult<String>();
		// 需要进缓存...
		if (cache) {
			cachequeue.add(deferredTodayResult);
		}
		// 不走缓存，直接发送掉...
		else {
			deferredTodayResult.setResult(formatTotalRmb());
		}
		return deferredTodayResult;
	}

	private void sendData(BlockingQueue<DeferredResult<String>> queue, String rmb) {
		// 把结果发给客户端...
		DeferredResult<String> req = queue.poll();
		while (req != null) {
			try {
				req.setResult(rmb);
			} catch (Exception e) {
				logger.error("实时充值时下发结果出现异常情况.", e);
			} finally {
				req = queue.poll();
			}
		}
	}

	public void updatePayInfo(PayMonitor pay) {
		String today = DateUtils.formatyyyy_MM_dd(LocalDate.now());
		if (!today.equals(pay.getToday())) {
			logger.error("更新充值信息时，发现不是今天的充值 today={},rmb={}", pay.getToday(), pay.getMoney());
			return;
		}

		this.totalRmb = pay.getMoney();
		logger.info("更新充值信息 today={},rmb={}", pay.getToday(), pay.getMoney());

		if (cachequeue.isEmpty()) {
			return;
		}

		this.sendData(cachequeue, formatTotalRmb());
	}

	public String formatTotalRmb() {
		DecimalFormat a = new DecimalFormat();
		a.applyPattern(",###.##");
		return a.format(totalRmb / 100D);
	}
}