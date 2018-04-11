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
package cn.qeng.gm.core.token;

import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 令牌检测拦截器.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class TokenInterceptor implements HandlerInterceptor {
	private static final String session_token = "token";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			Token annotation = ((HandlerMethod) handler).getMethod().getAnnotation(Token.class);
			if (annotation != null) {
				// 检测令牌
				if (annotation.check()) {
					if (isRepeatSubmit(request)) {
						final PrintWriter writer = response.getWriter();
						writer.write("<script>alert('请勿重复提示表单...');</script>");
						writer.flush();
						return false;
					}
					request.getSession(false).removeAttribute(session_token);
				}
				// 放置令牌
				else {
					request.getSession(false).setAttribute(session_token, UUID.randomUUID().toString());
				}
			}
		}
		return true;
	}

	private boolean isRepeatSubmit(HttpServletRequest request) {
		String clinetToken = request.getParameter(session_token);
		if (clinetToken == null) {
			return true;// 没上报令牌
		}

		String serverToken = (String) request.getSession(false).getAttribute(session_token);
		if (serverToken == null) {
			return true;// 服务器没有存令牌
		}

		return !serverToken.equals(clinetToken);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}