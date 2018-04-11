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
package cn.qeng.gm.module.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.query.service.RoleLevelUpService;

/**
 * 角色升级日志入口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Controller
@RequestMapping("/query/level")
public class LevelLogController {
	@Autowired
	private RoleLevelUpService roleLevelUpService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.QUERY_LEVELUP)
	@RecordLog(OperationType.QUERY_LEVELUP_MANAGE)
	public ModelAndView manage() {
		ModelAndView view = new ModelAndView("/query/level/list");
		return view;
	}

	/**
	 * 查看玩家升级记录
	 */
	@RequestMapping("/check")
	@Auth(AuthResource.QUERY_LEVELUP)
	@RecordLog(value = OperationType.QUERY_LEVELUP_CHECK, args = { "playerId" })
	public ModelAndView list(String playerId) {
		ModelAndView view = new ModelAndView("/query/level/list");
		view.addObject("page", roleLevelUpService.getRoleLevelUpResult(playerId));
		view.addObject("playerId", playerId);
		return view;
	}
}
