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
package cn.qeng.gm.core.auth;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.session.CookieUser;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.service.LogService;
import cn.qeng.gm.module.backstage.service.UserService;
import cn.qeng.gm.module.backstage.service.result.UserLoginResult;
import cn.qeng.gm.util.IpUtils;

/**
 * 权限拦截器.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class AuthInterceptor implements HandlerInterceptor {
	@Autowired
	private UserService userService;
	@Autowired
	private LogService logService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			Auth auth = ((HandlerMethod) handler).getMethod().getAnnotation(Auth.class);
			if (auth != null) {// 有权限控制的就要检查
				SessionUser user = SessionManager.getSessionUser(request.getSession());
				// 未登录，判定是否7天免登录
				if (user == null) {
					long startTime = System.currentTimeMillis();
					CookieUser cookieUser = SessionManager.getCookieUser(request);
					if (cookieUser != null) {
						UserLoginResult result = userService.checkLogin(request, cookieUser.getUsername(), cookieUser.getPassword());

						if (result.getErrorCode() == ErrorCode.OK) {
							user = result.getUser();
							SessionManager.login(request, user);
							// 登录日志
							long cost = System.currentTimeMillis() - startTime;
							logService.log(cookieUser.getUsername(), user.getName(), OperationType.LOGIN_SEVEN_DAYS, IpUtils.getRemoteHost(request), cost);
						}
					}
				}
				// 未登录，跳转登录页面
				if (user == null) {
					// 常规页面，直接重定向
					if (StringUtils.isEmpty(request.getParameter("ajax"))) {
						response.sendRedirect(request.getContextPath());
					}
					// 如果是AJAX方式，使用JS跳转.
					else {
						final PrintWriter writer = response.getWriter();
						writer.write("<script>window.location.href='" + request.getContextPath() + "';</script>");
						writer.flush();
					}
					return false;
				}
				// 没有权限，去提示页面
				else if (!this.hasAuth(user, auth)) {
					response.sendRedirect(request.getContextPath() + "/noauth/");
					return false;
				}
			}
		}
		return true;
	}

	public boolean hasAuth(SessionUser user, Auth auth) {
		// 如果是首页，那还是要放行的.
		if (auth.value() == AuthResource.WELCOME) {
			return true;
		}

		// 没有权限？？？
		if (user.getAuth() == null || user.getAuth().isEmpty()) {
			return false;
		}

		// 有就有，没有就失败.
		return user.getAuth().isSuperman() || user.getAuth().contains(auth.value().name());
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}