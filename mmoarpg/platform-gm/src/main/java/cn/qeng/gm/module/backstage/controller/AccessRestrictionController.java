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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.backstage.domain.AccessRestriction;
import cn.qeng.gm.module.backstage.service.AccessRestrictionService;
import cn.qeng.gm.module.backstage.service.LoginPlatformService;

/**
 * 访问限制访问入口
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/backstage/access")
public class AccessRestrictionController {
	@Autowired
	private AccessRestrictionService accessRestrictionService;
	@Autowired
	private LoginPlatformService LoginPlatformService;

	/**
	 * 左边的导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.ACCESS_LIST)
	@RecordLog(OperationType.BACKSTAGE_ACCESS_MANAGE)
	public ModelAndView manage() {
		return this.list(0, PageConstant.MAX_SIZE);
	}

	/**
	 * 查看访问限制.
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.ACCESS_LIST)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "15") int size) {
		ModelAndView view = new ModelAndView("backstage/access/list");
		view.addObject("page", accessRestrictionService.getAllAccessibleRelations(page, size));
		return view;
	}

	/**
	 * 添加访问限制界面
	 */
	@Token
	@RequestMapping("/addUI")
	@Auth(AuthResource.ACCESS_ADD)
	@RecordLog(OperationType.BACKSTAGE_ACCESS_ADD_UI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("backstage/access/edit");
		view.addObject("loginplatforms", LoginPlatformService.getAllPlatforms());
		return view;
	}

	/**
	 * 添加访问限制
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/add/")
	@Auth(AuthResource.ACCESS_ADD)
	@RecordLog(value = OperationType.BACKSTAGE_ACCESS_ADD, args = { "name" })
	public String add(String name, @RequestParam(value = "IP", required = false) String IP, @RequestParam(value = "ipUserName", required = false) String ipUserName) {
		accessRestrictionService.add(name, IP, ipUserName);
		return "SUCCESS";
	}

	/**
	 * 编辑访问限制界面
	 */
	@Token
	@RequestMapping("/editUI/{id}/")
	@Auth((AuthResource.ACCESS_EDIT))
	@RecordLog(OperationType.BACKSTAGE_ACCESS_EDIT_UI)
	public ModelAndView editUI(@PathVariable("id") int id) {
		AccessRestriction access = accessRestrictionService.getAccessibleRelation(id);
		ModelAndView view = this.addUI();
		view.addObject("access", access);
		return view;
	}

	/**
	 * 编辑一个访问限制
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.ACCESS_EDIT)
	@RecordLog(value = OperationType.BACKSTAGE_ACCESS_EDIT, args = { "name" })
	public String edit(@RequestParam(required = false, defaultValue = "0") int id, String name, String IP, String ipUserName) {
		if (id > 0) {
			accessRestrictionService.edit(id, name, IP, ipUserName);
		} else {
			accessRestrictionService.add(name, IP, ipUserName);
		}
		return "OK";
	}

	/**
	 * 删除账号
	 */
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.ACCESS_DELETE)
	@RecordLog(OperationType.BACKSTAGE_ACCESS_DELETE)
	public ModelAndView delete(HttpServletRequest request, @PathVariable("id") int id) {
		accessRestrictionService.delete(id);
		ModelAndView view = this.list(0, PageConstant.MAX_SIZE);
		return view;
	}
}
