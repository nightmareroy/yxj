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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
import cn.qeng.gm.module.game.service.FictitiousRechargeService;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 虚拟充值入口
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("game/fictitious")
public class FictitiousRechargeController {
	@Autowired
	private ServerService serverService;
	@Autowired
	private FictitiousRechargeService fictitiousRechargeService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.MANAGE_FICTITIOUS)
	@RecordLog(OperationType.MANAGER_FICTITIOUS_LIST)
	public ModelAndView manage() {
		return this.list(0);
	}

	/**
	 * 充值补单列表
	 */
	@RequestMapping("/list/")
	@Auth(AuthResource.MANAGE_FICTITIOUS)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("game/fictitious/list");
		view.addObject("page", fictitiousRechargeService.listFictitiousRecharge(page));
		return view;
	}

	@RequestMapping("/index")
	@Auth(AuthResource.MANAGE_FICTITIOUS)
	@RecordLog(OperationType.MANAGER_FICTITIOUS_INDEX)
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("game/fictitious/index");
		serverService.buildServerList(view);
		return view;
	}

	@ResponseBody
	@RequestMapping("/add")
	@Auth(AuthResource.MANAGE_FICTITIOUS)
	@RecordLog(value = OperationType.MANAGER_FICTITIOUS_ADD, args = { "currencyAmt", "player" })
	public String add(HttpServletRequest request, int serverId, int type, String player, int currencyAmt, String reson) {
		SessionUser user = SessionManager.getSessionUser(request);
		return String.valueOf(fictitiousRechargeService.sendRecharge(user, serverId, type, player, currencyAmt, reson));
	}

	/**
	 * 同意审批
	 */
	@ResponseBody
	@RequestMapping("/agree/{id}/")
	@Auth(AuthResource.MANAGE_FICTITIOUS_APPROVAL)
	@RecordLog(OperationType.MANAGER_FICTITIOUS_AGREE)
	public ModelAndView agree(HttpServletRequest request, @PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		fictitiousRechargeService.agree(SessionManager.getSessionUser(request), id);
		return this.list(page);
	}

	/**
	 * 拒绝审批
	 */
	@ResponseBody
	@RequestMapping("/refuse/{id}/")
	@Auth(AuthResource.MANAGE_FICTITIOUS_APPROVAL)
	@RecordLog(OperationType.MANAGER_FICTITIOUS_REFUSE)
	public ModelAndView refuse(HttpServletRequest request, @PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		fictitiousRechargeService.refuse(SessionManager.getSessionUser(request), id);
		return this.list(page);
	}
}