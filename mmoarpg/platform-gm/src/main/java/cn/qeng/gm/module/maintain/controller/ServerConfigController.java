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
package cn.qeng.gm.module.maintain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.ServerConfigService;

/**
 * 配置中心入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/maintain/serverconfig")
public class ServerConfigController {
	@Autowired
	private ServerConfigService serverConfigService;

	@RequestMapping("/manage")
	@Auth(AuthResource.GAME_CONFIG)
	@RecordLog(OperationType.GAME_CONFIG_MANAGE)
	public ModelAndView manage() {
		return this.list();
	}

	@RequestMapping("/list")
	@Auth(AuthResource.GAME_CONFIG)
	public ModelAndView list() {
		ModelAndView view = new ModelAndView("maintain/serverconfig/list");
		view.addObject("page", serverConfigService.getAllServerConfig());
		return view;
	}

	@RequestMapping("/addUI")
	@Auth(AuthResource.GAME_CONFIG)
	@RecordLog(OperationType.GAME_CONFIG_ADDUI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("maintain/serverconfig/edit");
		view.addObject("page", serverConfigService.getAllServerConfig());
		return view;
	}

	@ResponseBody
	@RequestMapping("/add")
	@Auth(AuthResource.GAME_CONFIG)
	@RecordLog(value = OperationType.GAME_CONFIG_ADD, args = "id")
	public String add(int id, String name, int appId, int areaId, String pubhost, int port, String gameHost, String template, // 基本配置
			String redisHost, int redisPort, @RequestParam(required = false) String redisPassword, int redisIndex, // Redis配置
			String battleHost, int battleFastPort, int battleIcePort) {// 战斗服
		serverConfigService.edit(id, name, template, appId, areaId, pubhost, port, gameHost, redisHost, redisPort, redisPassword, redisIndex, battleHost, battleFastPort, battleIcePort);
		return "OK";
	}

	@RequestMapping("/editUI")
	@Auth(AuthResource.GAME_CONFIG)
	@RecordLog(value = OperationType.GAME_CONFIG_EDITUI, args = "id")
	public ModelAndView editUI(int id) {
		ModelAndView view = new ModelAndView("maintain/serverconfig/edit");
		view.addObject("config", serverConfigService.getServerConfig(id));
		return view;
	}

	@ResponseBody
	@RequestMapping("/edit")
	@Auth(AuthResource.GAME_CONFIG)
	@RecordLog(value = OperationType.GAME_CONFIG_EDIT, args = "id")
	public String edit(int id, String name, int appId, int areaId, String pubhost, int port, String gameHost, String template, // 基本配置
			String redisHost, int redisPort, @RequestParam(required = false) String redisPassword, int redisIndex, // Redis配置
			String battleHost, int battleFastPort, int battleIcePort) {// 战斗服
		serverConfigService.edit(id, name, template, appId, areaId, pubhost, port, gameHost, redisHost, redisPort, redisPassword, redisIndex, battleHost, battleFastPort, battleIcePort);
		return "OK";
	}
}