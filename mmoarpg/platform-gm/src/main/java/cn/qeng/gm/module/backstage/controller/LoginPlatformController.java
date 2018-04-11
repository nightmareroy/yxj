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
import cn.qeng.gm.module.backstage.domain.LoginPlatform;
import cn.qeng.gm.module.backstage.service.LoginPlatformService;

/**
 * 登录平台控制类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/backstage/loginplatform")
public class LoginPlatformController {
	@Autowired
	private LoginPlatformService loginPlatformService;

	/**
	 * 登录平台管理，左边导航.
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.LOGINPLATFORM_LIST)
	@RecordLog(OperationType.BACKSTAGE_LOGINPALTFORM_MANAGE)
	public ModelAndView manage() {
		return this.list(0, PageConstant.MAX_SIZE);
	}

	/**
	 * 查看登录平台.
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.LOGINPLATFORM_LIST)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "15") int size) {
		ModelAndView view = new ModelAndView("backstage/loginplatform/list");
		view.addObject("page", loginPlatformService.getPlatforms(page, size));
		return view;
	}

	/**
	 * 添加登录平台界面
	 */
	@Token
	@RequestMapping("/addUI")
	@Auth(AuthResource.LOGINPLATFORM_ADD)
	@RecordLog(OperationType.BACKSTAGE_LOGINPALTFORM_ADD_UI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("backstage/loginplatform/edit");
		return view;
	}

	/**
	 * 添加一个登录平台
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/add/")
	@Auth(AuthResource.LOGINPLATFORM_ADD)
	@RecordLog(value = OperationType.BACKSTAGE_LOGINPALTFORM_ADD, args = { "loginPlatformName" })
	public String add(@RequestParam(value = "secretkey", required = false) String secretkey, @RequestParam(value = "loginPlatformName", required = false) String loginPlatformName, @RequestParam(value = "loginPlatformId", required = false) String loginPlatformId, @RequestParam(value = "platforms[]", required = false) String[] platforms) {
		loginPlatformService.add(secretkey, loginPlatformName, platforms, loginPlatformId);
		return "SUCCESS";
	}

	/**
	 * 编辑登录平台界面
	 */
	@Token
	@RequestMapping("/editUI/{id}/")
	@Auth(AuthResource.LOGINPLATFORM_EDIT)
	@RecordLog(OperationType.BACKSTAGE_LOGINPALTFORM_EDIT_UI)
	public ModelAndView editUI(@PathVariable("id") String id) {
		LoginPlatform loginPlatform = loginPlatformService.getLoginPlatform(id);
		ModelAndView view = this.addUI();
		view.addObject("loginPlatform", loginPlatform);
		return view;
	}

	/**
	 * 编辑一个登录平台
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.LOGINPLATFORM_EDIT)
	@RecordLog(value = OperationType.BACKSTAGE_LOGINPALTFORM_EDIT, args = { "loginPlatformName" })
	public String edit(@RequestParam(value = "secretkey", required = false) String secretkey, @RequestParam(value = "loginPlatformName", required = false) String loginPlatformName, @RequestParam(value = "loginPlatformId", required = false) String loginPlatformId, @RequestParam(value = "platforms[]", required = false) String[] platforms) {
		if (loginPlatformId != null) {
			loginPlatformService.edit(secretkey, loginPlatformName, loginPlatformId, platforms);
		} else {
			loginPlatformService.add(secretkey, loginPlatformName, platforms, loginPlatformId);
		}
		return "SUCCESS";
	}

	/**
	 * 删除登录平台
	 */
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.LOGINPLATFORM_DELETE)
	@RecordLog(value = OperationType.BACKSTAGE_LOGINPALTFORM_DELETE)
	public ModelAndView delete(HttpServletRequest request, @PathVariable("id") String id) {
		loginPlatformService.delete(id);
		ModelAndView view = this.list(0, PageConstant.MAX_SIZE);
		return view;
	}
}