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
package cn.qeng.paycenter.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.monitor.MonitorConst;
import cn.qeng.common.gm.monitor.PayMonitor;
import cn.qeng.paycenter.OrderConst;
import cn.qeng.paycenter.api.PayAPI;
import cn.qeng.paycenter.api.rpc.RpcResponse;
import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.domain.OrderInfoRepository;
import cn.qeng.paycenter.util.NamedThreadFactory;
import cn.qeng.paycenter.util.Redis;

/**
 * 发货服务.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class ShippedService {
	private final static Logger logger = LogManager.getLogger(ShippedService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Value("${pay.statistics.redis.ip}")
	private String redisHost;
	@Value("${pay.statistics.redis.port}")
	private int redisPort;
	@Value("${pay.statistics.redis.password}")
	private String redisPassword;
	@Value("${pay.statistics.redis.db}")
	private int redisIndex;
	@Autowired
	private OrderInfoRepository orderInfoRepository;

	private ScheduledExecutorService pool;
	// 统计充值的Redis.
	private Redis redis;

	@PostConstruct
	public void initChat() {
		// 初始化线程池
		pool = Executors.newScheduledThreadPool(4, new NamedThreadFactory("shipped"));
		// 加载所以待发货的，准备发送...
		orderInfoRepository.findAllByState(OrderConst.STATE_UNSHIPPED).forEach(v -> scheduleExecute(v.getId()));

		if (StringUtils.isEmpty(redisPassword)) {
			redis = new Redis(redisHost, redisPort, redisIndex);
		} else {
			redis = new Redis(redisHost, redisPort, redisPassword, redisIndex);
		}
	}

	@PreDestroy
	public void destroy() {
		pool.shutdown();
	}

	public void execute(final String orderId) {
		pool.execute(() -> {
			try {
				trySettlementOrder(orderId);
			} catch (Exception e) {
				logger.info("trySettlementOrder error. orderId={}", orderId);
			}
		});
	}

	public void trySettlementOrder(final String orderId) {
		logger.info("尝试给游戏服推送充值发货请求 orderId={}", orderId);
		OrderInfo info = orderInfoRepository.findOne(orderId);
		if (info == null) {
			logger.warn("订单不见了... orderId={}", orderId);
			return;
		}

		if (info.getState() != OrderConst.STATE_UNSHIPPED) {
			logger.warn("订单状态不是待发货了... orderId={},state={}", orderId, info.getState());
			return;
		}

		info.setTryTime(new Date());
		info.setTryCount(info.getTryCount() + 1);
		info.setModifyTime(info.getTryTime());

		boolean flag = false;

		PayAPI pay = null;
		// 越南使用流水编号发货...
		if ("2002".equals(info.getChannel())) {
			int productId = Integer.parseInt(info.getNotifyTransId().substring(info.getNotifyTransId().lastIndexOf(".") + 1));
			pay = new PayAPI(orderId, info.getRoleId(), productId);
		} else {
			pay = new PayAPI(orderId);
		}
		RpcResponse response = pay.request(info.getServerId());

		// 发货成功
		if ("success".equals(response.getResult())) {
			flag = true;
			info.setState(OrderConst.STATE_COMPLETE);
			logger.info("发货成功 orderId={}", orderId);
		}
		orderInfoRepository.save(info);

		// 发货失败，重新放进队列
		if (!flag) {
			logger.info("发货失败 orderId={}", orderId);
			scheduleExecute(orderId);
		}
	}

	public void scheduleExecute(final String orderId) {
		logger.info("延迟5分钟后重新尝试发货 orderId={}", orderId);
		pool.schedule(() -> this.execute(orderId), 5, TimeUnit.MINUTES);
	}

	public void publish(int money) {
		String today = LocalDate.now().format(formatter);
		pool.execute(() -> {
			try {
				// 统计
				long totalMoney = redis.hincrBy(MonitorConst.REDIS_KEY_PAY_INFO, today, money);
				logger.info("充值统计 today={},money={}", today, totalMoney);

				// 发布
				PayMonitor monitor = new PayMonitor();
				monitor.setToday(today);
				monitor.setMoney(totalMoney);
				redis.publish(MonitorConst.REDIS_PUBLISH_PAY_MONITOR, JSON.toJSONString(monitor));
			} catch (Exception e) {
				logger.info("发布今天充值统计异常", e);
			}
		});
	}
}