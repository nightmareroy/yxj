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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qeng.paycenter.OrderConst;
import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.domain.OrderInfoRepository;

/**
 * 订单业务处理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class OrderService {
	private final static Logger logger = LogManager.getLogger(OrderService.class);

	@Autowired
	private OrderInfoRepository orderInfoRepository;

	private AtomicInteger autoNum = new AtomicInteger(0);

	/**
	 * 创建订单信息.
	 */
	public OrderInfo createOrderInfo(int appId, String channel, String subchannel, int serverId, String username, String roleId, int money) {
		OrderInfo order = new OrderInfo();
		order.setId(this.genOrdierId(channel));
		logger.info("生成订单号 id={}", order.getId());
		order.setAppId(appId);
		order.setChannel(channel);
		order.setSubchannel(subchannel);// 子渠道
		order.setServerId(serverId);
		order.setUsername(username);
		order.setRoleId(roleId);
		order.setMoney(money);
		order.setCreateTime(new Date());
		order.setModifyTime(order.getCreateTime());
		order.setState(OrderConst.STATE_UNPAID);// 订单状态：0=待支付
		orderInfoRepository.save(order);
		logger.info("创建订单 id={},channel={},subchannel={},serverId={},username={},roleId={}, money={}", order.getId(), channel, subchannel, serverId, username, roleId, money);
		return order;
	}

	// 服务器时间戳+平台+Code
	public synchronized String genOrdierId(String channel) {
		int code = autoNum.incrementAndGet();
		if (code > 99_9999) {
			autoNum.set(0);
			code = autoNum.incrementAndGet();
		}
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		int random = ThreadLocalRandom.current().nextInt(1000, 9999);
		return new StringBuffer(14 + 6).append(now).append(channel).append(addLeftZero(code)).append(code).append(random).toString();
	}

	private String addLeftZero(int code) {
		int x = 6 - String.valueOf(code).length();
		switch (x) {
		case 1:
			return "0";
		case 2:
			return "00";
		case 3:
			return "000";
		case 4:
			return "0000";
		case 5:
			return "00000";
		default:
			return "";
		}
	}

	public static void main(String[] args) {
		OrderService x = new OrderService();
		for (int i = 0; i < 1000000; i++) {
			System.out.println(x.genOrdierId("1002"));
		}
	}
}