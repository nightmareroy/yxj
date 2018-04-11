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

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.service.GameNoticeService;

/**
 * 游戏内公告
 * 
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/notice")
public class GameNoticeController {
	@Autowired
	private GameNoticeService gameNoticeService;

	/**
	 * 左边导航-这个有日志
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(OperationType.MANAGER_GAME_NOTICE_LIST)
	public ModelAndView manage() {
		return this.list();
	}

	/**
	 * 公告列表，没有日志.内容由Ajax来加载.
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	public ModelAndView list() {
		return new ModelAndView("game/notice/list");
	}

	/**
	 * 公告列表 Ajax请求...
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RequestMapping(value = "/query", produces = "application/json; charset=utf-8")
	public String query() {
		return JSON.toJSONString(gameNoticeService.getNotices());
	}

	/**
	 * 添加公告界面
	 */
	@RequestMapping("/addUI")
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(OperationType.MANAGER_GAME_NOTICE_ADDUI)
	public ModelAndView addUI() {
		return new ModelAndView("game/notice/index");
	}

	/**
	 * 添加公告.
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(value = OperationType.MANAGER_GAME_NOTICE_ADD, args = { "title" })
	@RequestMapping("/add/")
	public String add(HttpServletRequest request, String title, String content) throws ParseException {
		SessionUser user = SessionManager.getSessionUser(request);
		return String.valueOf(gameNoticeService.addNotice(user, title, content));
	}

	/**
	 * 编辑公告界面
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(value = OperationType.MANAGER_GAME_NOTICE_EDITUI, args = { "id" })
	@RequestMapping("/editUI/{id}/")
	public ModelAndView editUI(@PathVariable("id") int id) {
		ModelAndView view = this.addUI();
		view.addObject("notice", gameNoticeService.getNotice(id));
		return view;
	}

	/**
	 * 编辑公告.
	 */
	@ResponseBody
	@RequestMapping("/edit/")
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(value = OperationType.MANAGER_GAME_NOTICE_EDIT, args = { "title" })
	public String edit(HttpServletRequest request, int id, String title, String content) throws ParseException {
		SessionUser user = SessionManager.getSessionUser(request);
		return String.valueOf(gameNoticeService.editNotice(user, id, title, content));
	}

	/**
	 * 删除公告
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGER_GAME_NOTICE)
	@RecordLog(value = OperationType.MANAGER_GAME_NOTICE_DELETE, args = { "id" })
	@RequestMapping("/delete/{id}/")
	public ModelAndView delete(@PathVariable("id") int id) {
		gameNoticeService.deleteNotice(id);
		return this.list();
	}
}