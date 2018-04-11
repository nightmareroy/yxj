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
package cn.qeng.gm.module.data.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.data.service.DateLevelService;
import cn.qeng.gm.module.maintain.service.ServerService;
import cn.qeng.gm.util.DateUtils;

/**
 * 玩家等级分布.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Controller
@RequestMapping("/data/level")
public class DataLevelController {
	@Autowired
	private ServerService serverService;
	@Autowired
	private DateLevelService dateLevelService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.DATA_LEVEL)
	@RecordLog(OperationType.DATA_LEVEL)
	public ModelAndView manager() {
		ModelAndView view = new ModelAndView("/data/level/list");
		serverService.buildServerList(view);
		view.addObject("maxtime", LocalDate.now());
		return view;
	}

	@RequestMapping("/list/")
	@Auth(AuthResource.DATA_LEVEL)
	public ModelAndView list(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date reservationtime, @RequestParam(required = false, name = "serverIds") String serverIds) {
		ModelAndView view = this.manager();

		serverService.buildServerList(view);
		List<Integer> sidList = new ArrayList<>();
		if (!StringUtils.isEmpty(serverIds)) {// 已选择的区服
			Map<Integer, Integer> selectedServerMap = new HashMap<>();
			for (String id : serverIds.split(",")) {
				Integer sid = Integer.parseInt(id);
				sidList.add(sid);
				selectedServerMap.put(sid, sid);
			}
			view.addObject("selectedServerMap", selectedServerMap);
		}

		if (reservationtime != null) {
			view.addObject("today", DateUtils.formatyyyy_MM_dd(reservationtime));
		}

		view.addObject("result", dateLevelService.getLevelResult(reservationtime, sidList));
		return view;
	}
}