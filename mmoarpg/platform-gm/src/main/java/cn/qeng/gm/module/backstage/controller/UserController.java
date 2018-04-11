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

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.backstage.domain.User;
import cn.qeng.gm.module.backstage.service.AuthService;
import cn.qeng.gm.module.backstage.service.LoginPlatformService;
import cn.qeng.gm.module.backstage.service.UserService;

/**
 * 账号管理入口
 * 
 * @author 小流氓(mingkai.zhou@qeng.net)
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/backstage/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private AuthService authService;
	@Autowired
	private LoginPlatformService LoginPlatformService;

	/**
	 * 查看后台账号
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.USER_LIST)
	@RecordLog(OperationType.BACKSTAGE_USER_MANAGE)
	public ModelAndView manage() {
		return this.list(0);
	}

	/**
	 * 查看后台账号
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.USER_LIST)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("backstage/user/list");
		view.addObject("page", userService.getUsers(page));
		view.addObject("author", authService.getAllAuthers());
		view.addObject("loginplatform", LoginPlatformService.getAllPlatformes());
		return view;
	}

	/**
	 * 添加界面
	 */
	@RequestMapping("/addUI")
	@Auth(AuthResource.USER_ADD)
	@RecordLog(OperationType.BACKSTAGE_USER_ADD_UI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("backstage/user/edit");
		view.addObject("auths", authService.getAllAutheres());
		view.addObject("loginplatforms", LoginPlatformService.getAllPlatforms());
		return view;
	}

	/**
	 * 添加一个账号
	 */
	@ResponseBody
	@RequestMapping("/add/")
	@Auth(AuthResource.USER_ADD)
	@RecordLog(value = OperationType.BACKSTAGE_USER_ADD, args = { "username", "name" })
	public String add(String username, String password, String name, int auth, String loginPlatformId) {
		return String.valueOf(userService.add(username, password, name, auth, loginPlatformId));
	}

	/**
	 * 编辑界面
	 */
	@Token
	@RequestMapping("/editUI/{id}/")
	@Auth((AuthResource.USER_EDIT))
	@RecordLog(OperationType.BACKSTAGE_USER_EDIT_UI)
	public ModelAndView editUI(@PathVariable("id") int id) {
		User user = userService.getUser(id);
		if (user != null) {
			user.setPassword(UserService.DEFAULT_SHOW_PASSWORD);
		}
		ModelAndView view = this.addUI();
		view.addObject("user", user);
		return view;
	}

	/**
	 * 编辑一个账号
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.USER_EDIT)
	@RecordLog(value = OperationType.BACKSTAGE_USER_EDIT, args = { "username", "name" })
	public String edit(@RequestParam(required = false, defaultValue = "0") int id, String username, String password, String name, int auth, String loginPlatformId) {
		if (id > 0) {
			userService.edit(id, username, password, name, auth, loginPlatformId);
		} else {
			userService.add(username, password, name, auth, loginPlatformId);
		}
		return "OK";
	}

	/**
	 * 删除账号
	 */
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.USER_DELETE)
	@RecordLog(value = OperationType.BACKSTAGE_USER_DELETE, args = { "username" })
	public ModelAndView delete(HttpServletRequest request, @PathVariable("id") int id) {
		String username = userService.findName(id).getUsername();
		userService.delete(id);
		ModelAndView view = this.list(0);
		view.addObject("username", username);
		return view;
	}

	/**
	 * 修改密码界面
	 */
	@Token
	@RequestMapping("/password/modifyUI/")
	@Auth(AuthResource.MODIFY_PASSWORD)
	@RecordLog(OperationType.BACKSTAGE_MODIFY_PASSWORD_UI)
	public ModelAndView modifyPasswordUI() {
		return new ModelAndView("backstage/user/password");
	}

	/**
	 * 修改密码
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/password/modify/")
	@Auth(AuthResource.MODIFY_PASSWORD)
	@RecordLog(OperationType.BACKSTAGE_MODIFY_PASSWORD)
	public String modifyPassword(HttpServletRequest request, String OldPassword, String NewPassword) {
		return String.valueOf(userService.modifyPassword(SessionManager.getSessionUser(request), OldPassword, NewPassword));
	}
}