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
package cn.qeng.paycenter.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.qeng.paycenter.OrderConst;
import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.domain.OrderInfoRepository;
import cn.qeng.paycenter.service.ShippedService;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public abstract class AbstractNotifyController {
	private final static Logger logger = LogManager.getLogger(QengNotifyController.class);

	@Autowired
	private ShippedService shippedService;
	@Autowired
	private OrderInfoRepository orderInfoRepository;

	protected abstract String buildParams(HttpServletRequest request);

	protected abstract boolean checkSign(HttpServletRequest request, String params, String sign) throws Exception;

	protected abstract String resultSuccess();

	/**
	 * 判定当前状态是否交易成功.
	 */
	protected abstract boolean checkStatus(String status, OrderInfo info) throws Exception;

	public String notify(HttpServletRequest request, String orderId, String sign, String status, double price, String notifyIp, String transid) throws Exception {
		String params = buildParams(request);

		if (!checkSign(request, params, sign)) {
			logger.warn("发货通知签名验证失败，orderId={}", orderId);
			return "Sign Error";
		}

		// 取出本地的订单信息
		OrderInfo info = orderInfoRepository.findOne(orderId);
		if (info == null) {
			logger.warn("非法充值回调,params={}", params);
			// FIXME 记录非法充值回调，但也要把钱扣下，直接返回SUCCESS
			return resultSuccess();
		}

		// 这个订单已非待支付状态，返回成功
		if (info.getState() != OrderConst.STATE_UNPAID) {
			logger.warn("订单已非待支付状态,orderId={},state={}", info.getId(), info.getState());
			return resultSuccess();
		}

		// 交易成功
		if (checkStatus(status, info)) {
			info.setState(OrderConst.STATE_UNSHIPPED);
		}
		// 其他统算交易失败
		else {
			info.setState(OrderConst.STATE_FAIL);
		}

		// 验证钱
		if ((info.getMoney() / 100.0D) != price) {
			logger.warn("充值金额验证不一致,orderId={},selfMoney={} 分,targetPrice={} 元", info.getId(), info.getMoney(), price);
			info.setState(OrderConst.STATE_EXCEPTION);
		}

		info.setNotifyTime(new Date());
		info.setNotifyParams(params);
		info.setNotifyIp(notifyIp);
		info.setNotifyTransId(transid);
		info.setModifyTime(info.getNotifyTime());
		orderInfoRepository.save(info);
		logger.info("订单状态改变，orderId={},state={}", orderId, info.getState());

		// 订单状态：1=待发货-->抛给发货线程去发货
		if (info.getState() == OrderConst.STATE_UNSHIPPED) {
			shippedService.publish(info.getMoney());
			shippedService.execute(info.getId());
		}

		return resultSuccess();
	}
}