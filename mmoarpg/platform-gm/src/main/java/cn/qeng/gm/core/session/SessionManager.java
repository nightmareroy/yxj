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
package cn.qeng.gm.core.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.util.WebUtils;

/**
 * 登录Session管理器.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
public class SessionManager {
	private static final String session_user_key = "SESSION_USER";
	private static final String cookie_username_key = "COOKIE_USERNAME";
	private static final String cookie_password_key = "COOKIE_PASSWORD";

	public static void login(HttpServletRequest request, SessionUser user) {
		WebUtils.setSessionAttribute(request, session_user_key, user);
	}

	public static void logout(HttpSession session) {
		session.removeAttribute(session_user_key);
	}

	public static SessionUser getSessionUser(HttpSession session) {
		return (SessionUser) session.getAttribute(session_user_key);
	}

	public static SessionUser getSessionUser(HttpServletRequest request) {
		return (SessionUser) WebUtils.getSessionAttribute(request, session_user_key);
	}

	public static void addUsername2Cookie(HttpServletResponse response, String username) {
		addCookie(response, cookie_username_key, username);
	}

	public static void addPassword2Cookie(HttpServletResponse response, String password) {
		addCookie(response, cookie_password_key, password);
	}

	public static void addCookie(HttpServletResponse response, String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(7 * 24 * 60 * 60);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void delLoginCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);// 立即销毁cookie
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
	}

	public static CookieUser getCookieUser(HttpServletRequest request) {
		CookieUser user = null;

		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				// 账号
				if (cookie_username_key.equals(cookie.getName())) {
					if (user == null) {
						user = new CookieUser();
					}
					user.setUsername(cookie.getValue());
				}
				// 密码
				else if (cookie_password_key.equals(cookie.getName())) {
					if (user == null) {
						user = new CookieUser();
					}
					user.setPassword(cookie.getValue());
				}
			}
		}

		return user;
	}
}