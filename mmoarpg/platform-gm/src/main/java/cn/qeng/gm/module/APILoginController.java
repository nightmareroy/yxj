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
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.domain.LoginPlatform;
import cn.qeng.gm.module.backstage.service.LogService;
import cn.qeng.gm.module.backstage.service.LoginPlatformService;
import cn.qeng.gm.module.backstage.service.UserService;
import cn.qeng.gm.module.backstage.service.result.UserLoginResult;
import cn.qeng.gm.util.IpUtils;
import cn.qeng.gm.util.Md5Utils;

/**
 * 第三方平台登录入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
public class APILoginController {
	/**
	 * 非法请求.
	 */
	private static final String LOGIN_ERROR_CODE_ILLEGAL = "{code=-1, msg=\"非法请求\"}";
	/**
	 * 签名错误
	 */
	private static final String LOGIN_ERROR_CODE_SIGN_ERROR = "{code=-2, msg=\"签名错误\"}";
	/**
	 * 登录链接已超时
	 */
	private static final String LOGIN_ERROR_CODE_TIMEOUT = "{code=-3, msg=\"登录链接已超时\"}";
	/**
	 * 非法账号(账号不存在)
	 */
	private static final String LOGIN_ERROR_CODE_ACCOUNT_DOES_NOT_EXIST = "{code=-4, msg=\"非法账号(账号不存在)\"}";
	/**
	 * 登录失败(账号已锁定)
	 */
	private static final String LOGIN_ERROR_CODE_PASSWORD_ERROR = "{code=-5, msg=\"登录失败(账号已锁定)\"}";

	@Autowired
	private LogService logService;
	@Autowired
	private UserService userService;
	@Autowired
	private LoginPlatformService loginPlatformService;

	@RequestMapping("/api/v1/login")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String platform, String account, boolean sevendays, long time, String sign) {
		ModelAndView view = new ModelAndView("apiresult");
		LoginPlatform loginPlatform = loginPlatformService.getLoginPlatform(platform);
		if (loginPlatform == null) {
			view.addObject("msg", LOGIN_ERROR_CODE_ILLEGAL);
			return view;
		}

		long startTime = System.currentTimeMillis();
		if (time + 5 * 60 * 1000 < startTime / 1000) {
			view.addObject("msg", LOGIN_ERROR_CODE_TIMEOUT);
			return view;
		}

		// 签名验证，参数相连接。md5后的结果小写 md5($platform-$account-$sevendays-$time-$key)
		StringBuilder sb = new StringBuilder(512);
		sb.append(platform).append('-').append(account).append('-').append(sevendays).append('-').append(time).append('-').append(loginPlatform.getSecretkey());
		if (Md5Utils.encrypt(sb.toString()).toLowerCase().equals(sign)) {
			view.addObject("msg", LOGIN_ERROR_CODE_SIGN_ERROR);
			return view;
		}

		UserLoginResult result = userService.checkPlatformLogin(request, platform, account);
		if (result.getErrorCode() == ErrorCode.ACCOUNT_DOES_NOT_EXIST) {
			view.addObject("msg", LOGIN_ERROR_CODE_ACCOUNT_DOES_NOT_EXIST);
			return view;
		}

		if (result.getErrorCode() == ErrorCode.ACCOUNT_STATUS_LOCK) {
			view.addObject("msg", LOGIN_ERROR_CODE_PASSWORD_ERROR);
			return view;
		}

		if (result.getErrorCode() == ErrorCode.OK) {
			SessionUser user = result.getUser();
			SessionManager.login(request, user);

			// 7天免登录，写入
			if (sevendays) {
				// SessionManager.addUsername2Cookie(response, username);
				// SessionManager.addPassword2Cookie(response, password);
			}
			// 清理
			else {
				SessionManager.delLoginCookie(request, response);
			}

			// 登录日志
			long cost = System.currentTimeMillis() - startTime;
			logService.log(account, user.getName(), OperationType.LOGIN_PLATFORM, IpUtils.getRemoteHost(request), cost);

			view.setViewName("redirect:/welcome");
			return view;
		}

		// 不正常的都算失败...
		view.addObject("msg", LOGIN_ERROR_CODE_ILLEGAL);
		return view;
	}
}