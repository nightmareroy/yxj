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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.RollNotice;
import cn.qeng.gm.module.game.service.RollNoticeService;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 滚动公告（跑马灯）入口
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/roll/notice")
public class RollNoticeController {
	@Autowired
	private ServerService serverService;
	@Autowired
	private RollNoticeService rollNoticeService;

	/**
	 * 跑马灯左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(OperationType.MANAGER_LOGIN_NOTICE_LIST)
	public ModelAndView manage() {
		return this.list();
	}

	/**
	 * 公告列表，没有日志.内容由Ajax来加载.
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	public ModelAndView list() {
		return new ModelAndView("game/rollnotice/list");
	}

	/**
	 * 跑马灯列表 Ajax请求...
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RequestMapping(value = "/query", produces = "application/json; charset=utf-8")
	public String query() {
		return JSON.toJSONString(rollNoticeService.getNotices());
	}

	/**
	 * 跑马灯添加界面
	 */
	@RequestMapping("/addUI/")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(OperationType.MANAGER_ROLL_NOTICE_ADDUI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("game/rollnotice/index");
		serverService.buildServerList(view);
		return view;
	}

	/**
	 * 添加跑马灯.
	 */
	@ResponseBody
	@RequestMapping("/add/")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(value = OperationType.MANAGER_ROLL_NOTICE_ADD, args = { "content" })
	public String add(HttpServletRequest request, @RequestParam(value = "sids[]") Integer[] sids, int interval, String content, String reservationtime) throws ParseException {
		SessionUser user = SessionManager.getSessionUser(request);
		Date startTime = null;
		Date endTime = null;
		if (!StringUtils.isEmpty(reservationtime)) {
			String[] time = reservationtime.split(" - ", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.parse(time[0]);
			endTime = sdf.parse(time[1]);
		}
		rollNoticeService.addRollNotice(user, sids, interval, content, startTime, endTime);
		return "SUCCESS";
	}

	/**
	 * 编辑界面
	 */
	@RequestMapping("/editUI/{id}/")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(value = OperationType.MANAGER_ROLL_NOTICE_EDITUI, args = { "id" })
	public ModelAndView editUI(@PathVariable("id") int id) {
		ModelAndView view = this.addUI();
		RollNotice notice = rollNoticeService.getNotice(id);

		// 已选择的区服
		Map<Integer, Integer> selectedServerMap = new HashMap<>();
		JSON.parseArray(notice.getSids(), Integer.class).forEach(sid -> selectedServerMap.put(sid, sid));
		view.addObject("selectedServerMap", selectedServerMap);

		view.addObject("notice", rollNoticeService.getNotice(id));
		return view;
	}

	/**
	 * 编辑跑马灯.
	 */
	@ResponseBody
	@RequestMapping("/edit/")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(value = OperationType.MANAGER_ROLL_NOTICE_EDIT, args = { "id" })
	public String edit(HttpServletRequest request, int id, @RequestParam(value = "sids[]") Integer[] sids, int interval, String content, String reservationtime) throws ParseException {
		SessionUser user = SessionManager.getSessionUser(request);
		Date startTime = null;
		Date endTime = null;
		if (!StringUtils.isEmpty(reservationtime)) {
			String[] time = reservationtime.split(" - ", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.parse(time[0]);
			endTime = sdf.parse(time[1]);
		}
		rollNoticeService.editRollNotice(user, id, sids, interval, content, startTime, endTime);
		return "SUCCESS";
	}

	/**
	 * 删除跑马灯
	 */
	@ResponseBody
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.MANAGER_ROLL_NOTICE)
	@RecordLog(value = OperationType.MANAGER_ROLL_NOTICE_DELETE, args = { "id" })
	public ModelAndView delete(@PathVariable("id") int id) {
		rollNoticeService.deleteNotice(id);
		return this.list();
	}
}