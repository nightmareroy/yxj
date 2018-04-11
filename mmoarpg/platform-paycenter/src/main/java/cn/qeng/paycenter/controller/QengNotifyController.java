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
import cn.qeng.paycenter.util.RsaUtil;

/**
 * 清源充值成功回调发货接口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@RestController
@RequestMapping("/paycenter/to")
public class QengNotifyController extends AbstractNotifyController {
	private final static Logger logger = LogManager.getLogger(QengNotifyController.class);

	// 清源分配的公钥
	@Value("${android.qeng.publickey}")
	private String publicKey;

	/**
	 * 清源充值成功回调发货接口.
	 */
	@ResponseBody
	@RequestMapping("/qengNotify.jsp")
	public String notify(HttpServletRequest request) throws Exception {
		logger.info("清源充值成功回调 params={}", request.getParameterMap());

		final String notifyIp = request.getRemoteHost();
		final String orderid = request.getParameter("orderid");// 商户系统生成的订单号
		final String transid = request.getParameter("transid");// 支付平台的交易流水号
		final double price = Double.parseDouble(request.getParameter("price"));// 支付金额，单位为元
		final String status = request.getParameter("status");// 3系统异常，4交易失败，5交易成功
		final String sign = request.getParameter("sign");// 签名

		return super.notify(request, orderid, sign, status, price, notifyIp, transid);
	}

	@Override
	protected String buildParams(HttpServletRequest request) {
		return sortParams(request.getParameterMap());
	}

	// 有参数名按字母顺序进行排序，并去除sign
	public static String sortParams(Map<String, String[]> params) {
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
		return RsaUtil.doCheck(params, sign, publicKey);
	}

	@Override
	protected String resultSuccess() {
		return "SUCCESS";
	}

	@Override
	protected boolean checkStatus(String status, OrderInfo info) {
		// 5交易成功
		return "5".equals(status);
	}
}