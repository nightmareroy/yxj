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

import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.util.Md5;

/**
 * 当乐充值成功回调发货接口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@RestController
@RequestMapping("/paycenter/to")
public class DownjoyNotifyController extends AbstractNotifyController {
	private final static Logger logger = LogManager.getLogger(DownjoyNotifyController.class);

	@Value("${android.downjoy.payment.key}")
	private String paymentKey;

	/**
	 * 当乐充值成功回调发货接口.
	 */
	@ResponseBody
	@RequestMapping("/downjoyNotify.jsp")
	public String notify(HttpServletRequest request) throws Exception {
		logger.info("当乐充值成功回调 params={}", request.getParameterMap());

		final String notifyIp = request.getRemoteHost();
		String status = request.getParameter("result");// 支付结果，固定值。“1”代表成功，“0” 代表失败
		double price = Double.parseDouble(request.getParameter("money"));// 支付金额，单位：元，两位小数
		String orderid = request.getParameter("order");// 本次支付 SDK 生成的订单号
		String orgOrderId = request.getParameter("cpOrder");// 客户端购买商品时候传入的TransNo字段，小于 100 字符。（厂家用于金额验证,4.3.5 以上版本新增参数）
		String sign = request.getParameter("signature");// MD5 验证串，用于与接口生成的验证串做比较，保证计费通知的合法性。

		return super.notify(request, orgOrderId, sign, status, price, notifyIp, orderid);
	}

	@Override
	protected String buildParams(HttpServletRequest request) {
		return sortParams(request.getParameterMap());
	}

	public String sortParams(Map<String, String[]> params) {
		TreeSet<String> set = new TreeSet<String>(params.keySet());
		StringBuilder sb = new StringBuilder();
		for (String key : set) {
			if (key.equals("sign")) {
				continue;
			}
			sb.append(key + "=" + params.get(key)[0] + "&");
		}
		sb.deleteCharAt(sb.length() - 1);// 去除最后一个“&”符号
		return sb.toString();
	}

	@Override
	protected boolean checkSign(HttpServletRequest request, String params, String sign) throws Exception {
		String status = request.getParameter("result");// 支付结果，固定值。“1”代表成功，“0” 代表失败
		double price = Double.parseDouble(request.getParameter("money"));// 支付金额，单位：元，两位小数
		String orderid = request.getParameter("order");// 本次支付 SDK 生成的订单号
		String mid = request.getParameter("mid");// 本次支付用户的乐号，既登录后返回的 umid参数。 最长长度 64 字符
		String time = request.getParameter("time");// 时间戳，格式：yyyymmddHH24mmss 月日小时分秒小于 10 前面补充 0
		String ext = request.getParameter("ext") == null ? "" : request.getParameter("ext");// 客户端购买商品时传入的 ext 透传保留字段、没有则为空字符串，小于 200 个字符
		Long orgOrderId = Long.parseLong(request.getParameter("cpOrder"));// 客户端购买商品时候传入的TransNo字段，小于 100 字符。（厂家用于金额验证,4.3.5 以上版本新增参数）
		// order=xxxx&money=xxxx&mid=xxxx&time=xxxx&result=x&cpOrder=xxx&ext=xxx&key=xxxx
		String toCheck = new StringBuilder(128).append("order=").append(orderid)//
				.append("&").append("money=").append(price)//
				.append("&").append("mid=").append(mid)//
				.append("&").append("time=").append(time)//
				.append("&").append("result=").append(status)//
				.append("&").append("cpOrder=").append(orgOrderId)//
				.append("&").append("ext=").append(ext)//
				.append("&").append("key=").append(paymentKey).toString();
		return Md5.getMD5(toCheck.toString()).equals(sign.toUpperCase());
	}

	@Override
	protected String resultSuccess() {
		return "SUCCESS";
	}

	@Override
	protected boolean checkStatus(String status, OrderInfo info) {
		return "1".equals(status);
	}
}