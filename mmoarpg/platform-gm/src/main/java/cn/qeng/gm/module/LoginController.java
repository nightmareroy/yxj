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
package cn.qeng.gm.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.service.LogService;
import cn.qeng.gm.module.backstage.service.UserService;
import cn.qeng.gm.module.backstage.service.result.UserLoginResult;
import cn.qeng.gm.util.IpUtils;

/**
 * 登录控制类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
public class LoginController {
	@Autowired
	private LogService logService;
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response, String username, String password, @RequestParam(required = false, defaultValue = "false") boolean sevendays) {
		long startTime = System.currentTimeMillis();
		UserLoginResult result = userService.checkLogin(request, username, password);
		if (result.getErrorCode() == ErrorCode.OK) {
			SessionUser user = result.getUser();
			SessionManager.login(request, user);

			// 7天免登录，写入
			if (sevendays) {
				SessionManager.addUsername2Cookie(response, username);
				SessionManager.addPassword2Cookie(response, password);
			}
			// 清理
			else {
				SessionManager.delLoginCookie(request, response);
			}

			// 登录日志
			long cost = System.currentTimeMillis() - startTime;
			logService.log(username, user.getName(), OperationType.LOGIN, IpUtils.getRemoteHost(request), cost);
		}

		return String.valueOf(result.getErrorCode());
	}

	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		long startTime = System.currentTimeMillis();
		SessionUser user = SessionManager.getSessionUser(request);
		if (user != null) {
			long cost = System.currentTimeMillis() - startTime;
			logService.log(user.getUsername(), user.getName(), OperationType.LOGOUT, IpUtils.getRemoteHost(request), cost);
		}
		SessionManager.logout(request.getSession());
		SessionManager.delLoginCookie(request, response);
		return "redirect:/";
	}

	/**
	 * 一键锁定
	 */
	@RequestMapping("/lockscreen")
	public ModelAndView lockscreen(HttpServletRequest request, HttpServletResponse response, @RequestParam String username) {
		long startTime = System.currentTimeMillis();
		SessionUser user = SessionManager.getSessionUser(request);
		String name = "";
		if (user != null) {
			name = user.getName();
			username = user.getUsername();// 修正参数
			long cost = System.currentTimeMillis() - startTime;
			logService.log(user.getUsername(), user.getName(), OperationType.LOCKSCREEN, IpUtils.getRemoteHost(request), cost);
		}
		SessionManager.logout(request.getSession());
		SessionManager.delLoginCookie(request, response);

		ModelAndView view = new ModelAndView("lockscreen");
		view.addObject("username", username);
		view.addObject("name", name);
		return view;
	}
}