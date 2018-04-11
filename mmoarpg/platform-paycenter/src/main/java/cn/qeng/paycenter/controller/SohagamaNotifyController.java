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
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.paycenter.OrderConst;
import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.domain.OrderInfoRepository;
import cn.qeng.paycenter.service.ShippedService;
import cn.qeng.paycenter.util.RsaUtil;

/**
 * 越南充值成功回调发货接口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@RestController
@RequestMapping("/paycenter/to")
public class SohagamaNotifyController {
	private final static Logger logger = LogManager.getLogger(SohagamaNotifyController.class);
	@Autowired
	private ShippedService shippedService;
	@Autowired
	private OrderInfoRepository orderInfoRepository;

	// 清源分配的公钥
	@Value("${android.qeng.publickey}")
	private String publicKey;

	/**
	 * 清源充值成功回调发货接口.
	 */
	@ResponseBody
	@RequestMapping("/sohagameNotify.jsp")
	public String notify(HttpServletRequest request) throws Exception {
		logger.info("越南充值成功回调 params={}", request.getParameterMap());
		final String sign = request.getParameter("sign");// 签名
		String params = buildParams(request);
		if (!checkSign(request, params, sign)) {
			logger.warn("发货通知签名验证失败，attach={}", request.getParameter("attach"));
			return "Sign Error";
		}

		final String attachStr = request.getParameter("attach");
		JSONObject attach = JSON.parseObject(attachStr);
		// {"app_id":"7c20921b54172fef8c55560ed759980e","user_id":"837078289","order_info":"yxj.sohagame.115","role_id":"22","area_id":"22","order_id":"057051521452039","price":"14","time":1521452057,"algorithm":"HMAC-SHA256"}
		String orderId = "sohagame" + attach.getString("order_id");
		// 取出本地的订单信息
		OrderInfo info = orderInfoRepository.findOne(orderId);
		if (info != null) {
			logger.warn("重复充值回调,params={}", params);
			return resultSuccess();
		}

		logger.info("越南订单号 id={}", orderId);
		info = new OrderInfo();
		info.setId(orderId);
		info.setAppId(0);// 没有什么用
		info.setChannel("2002");
		info.setSubchannel("sohagame");// 子渠道
		info.setServerId(attach.getIntValue("area_id"));
		info.setUsername(attach.getString("user_id"));
		info.setRoleId(attach.getString("role_id"));
		info.setMoney(attach.getIntValue("price"));
		info.setCreateTime(new Date());
		info.setModifyTime(info.getCreateTime());
		logger.info("创建订单 id={},channel={},subchannel={},serverId={},username={},roleId={}, money={}", info.getId(), info.getChannel(), info.getSubchannel(), info.getServerId(), info.getUsername(), info.getRoleId(), info.getMoney());

		final String notifyIp = request.getRemoteHost();
		info.setState(OrderConst.STATE_UNSHIPPED);
		info.setNotifyTime(new Date());
		info.setNotifyParams(attachStr);
		info.setNotifyIp(notifyIp);
		info.setNotifyTransId(attach.getString("order_info"));
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

	protected boolean checkSign(HttpServletRequest request, String params, String sign) throws Exception {
		return RsaUtil.doCheck(params, sign, publicKey);
	}

	protected String resultSuccess() {
		return "SUCCESS";
	}
}