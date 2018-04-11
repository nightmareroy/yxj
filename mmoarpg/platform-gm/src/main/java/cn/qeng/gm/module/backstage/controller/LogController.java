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
package cn.qeng.gm.module.backstage.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.LogClassify;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.backstage.service.LogService;
import cn.qeng.gm.module.backstage.service.UserService;

/**
 * 操作日志访问接口类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/backstage/log")
public class LogController {

	@Autowired
	private LogService logService;
	@Autowired
	private UserService userService;

	/**
	 * 查看操作日志
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.LOGGER_LIST)
	@RecordLog(OperationType.BACKSTAGE_LOGGER_LIST)
	public ModelAndView manage() throws ParseException, java.text.ParseException {
		return this.list(0, null, null, null, null);
	}

	/**
	 * 查看操作日志
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.LOGGER_LIST)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(value = "reservationtime", required = false) String reservationtime, @RequestParam(value = "user", required = false) String user, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "classify", required = false) String classify) throws ParseException, java.text.ParseException {
		ModelAndView view = new ModelAndView("backstage/log/list");
		// 时间段
		Date startTime = null;
		Date endTime = null;
		if (!StringUtils.isEmpty(reservationtime)) {
			String[] time = reservationtime.split(" - ", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.parse(time[0]);
			endTime = sdf.parse(time[1]);
		}
		view.addObject("reservationtime", reservationtime);
		view.addObject("ip", ip);
		view.addObject("user", user);
		view.addObject("classify", classify);
		view.addObject("page", logService.getLogs(user, classify, ip, startTime, endTime, page));
		return view;
	}

	/**
	 * 查看操作日志页面
	 */
	@RequestMapping("/query/")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("/backstage/log/query");
		view.addObject("userlist", userService.getUser());// 所有用户
		view.addObject("classify", LogClassify.values());// 日志大类
		return view;
	}
}