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
package cn.qeng.gm.module.game.controller;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.service.UserService;
import cn.qeng.gm.module.game.service.PublishService;
import cn.qeng.gm.module.maintain.service.ServerService;
import cn.qeng.gm.util.DateUtils;

/**
 * 玩家处罚控制入口
 * 
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("game/publish")
public class PublishController {
	@Autowired
	public UserService userService;
	@Autowired
	public ServerService serverService;
	@Autowired
	public PublishService publishService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.MANAGE_PUBLISH)
	@RecordLog(OperationType.MANAGER_PUBLISHLOG_LIST)
	public ModelAndView manage() {
		return this.list(0, null, null, "", -1, "");
	}

	/**
	 * 查询惩处记录
	 */
	@RequestMapping("/list/")
	@Auth(AuthResource.MANAGE_PUBLISH)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end, @RequestParam(required = false, defaultValue = "") String username, @RequestParam(required = false, defaultValue = "-1") int type, @RequestParam(required = false, defaultValue = "") String playerName) {
		ModelAndView view = new ModelAndView("game/publish/list");
		view.addObject("page", publishService.getPublishByWhere(start, end, username, type, playerName, page));
		view.addObject("start", start == null ? "" : DateUtils.formatyyyyMMddHHmmss(start));
		view.addObject("end", end == null ? "" : DateUtils.formatyyyyMMddHHmmss(end));
		view.addObject("username", username);
		view.addObject("type", type);
		view.addObject("playerName", playerName);
		return view;
	}

	/**
	 * 打开高级查询界面
	 */
	@RequestMapping("/query/")
	@Auth(AuthResource.MANAGE_PUBLISH)
	@RecordLog(OperationType.MANAGER_PUBLISHLOG_QUERY)
	public ModelAndView query() {
		ModelAndView view = new ModelAndView("game/publish/query");
		view.addObject("userlist", userService.getUser());// 所有用户
		view.addObject("maxTime", LocalDate.now().plusDays(1));
		view.addObject("startTime", LocalDate.now().minusDays(30));
		return view;
	}

	@RequestMapping("/addUI")
	@Auth(AuthResource.MANAGE_PUBLISH)
	@RecordLog(OperationType.MANAGER_PUBLISHLOG_ADDUI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("game/publish/edit");
		serverService.buildServerList(view);
		return view;
	}

	/**
	 * 惩罚操作
	 */
	@ResponseBody
	@RequestMapping("/active/")
	@Auth(AuthResource.MANAGE_PUBLISH)
	@RecordLog(value = OperationType.MANAGER_PUBLISHLOG_ADD, args = { "playerName", "type" })
	public String pubilsh(HttpServletRequest request, int serverId, String playerName, @RequestParam(value = "reason", required = false) String reason, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date datepicker, int type) {
		SessionUser user = SessionManager.getSessionUser(request);
		return publishService.publish(user, serverId, playerName, reason, datepicker, type);
	}

	/**
	 * 由聊天监控返回的惩罚操作
	 */
	@ResponseBody
	@RequestMapping("/mactive/")
	@Auth(AuthResource.MANAGE_PUBLISH)
	public String monitorPublish(HttpServletRequest request, int serverId, String playerId, String playerName, String reason) {
		SessionUser user = SessionManager.getSessionUser(request);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 6);// 禁言6小时
		return publishService.publish(user, serverId, playerId, playerName, reason, calendar.getTime(), 2);
	}
}
