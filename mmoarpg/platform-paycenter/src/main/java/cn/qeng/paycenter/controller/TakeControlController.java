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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.service.OrderService;

/**
 * 生成订单入口.
 * <p>
 * 类名来源于老系统，默认兼容形式
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@RestController
@RequestMapping("/paycenter/take")
public class TakeControlController {

	@Autowired
	private OrderService orderService;

	/**
	 * 游戏区向充值中心请求生成订单号.
	 */
	@ResponseBody
	@RequestMapping("/control.jsp")
	public String control(int appid, String channel, @RequestParam(required = false) String subchannel, int serverid, String username, String roleid, Float money) {
		OrderInfo orderInfo = orderService.createOrderInfo(appid, channel, subchannel, serverid, username, roleid, money.intValue());
		return orderInfo.getId();// 返回订单号...
	}
}