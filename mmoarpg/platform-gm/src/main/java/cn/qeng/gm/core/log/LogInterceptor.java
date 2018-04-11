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
package cn.qeng.gm.core.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.service.LogService;
import cn.qeng.gm.util.IpUtils;

/**
 * 日志拦截器.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class LogInterceptor implements HandlerInterceptor {
	@Autowired
	private LogService logService;

	private NamedThreadLocal<Long> timeThreadLocal = new NamedThreadLocal<>("WatchExecuteTime");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		timeThreadLocal.set(System.currentTimeMillis());// 一次操作创建一个新的...
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (handler instanceof HandlerMethod) {
			RecordLog log = ((HandlerMethod) handler).getMethod().getAnnotation(RecordLog.class);
			if (log != null) {
				SessionUser user = SessionManager.getSessionUser(request.getSession());
				if (user != null) {
					long cost = System.currentTimeMillis() - timeThreadLocal.get();

					StringBuilder sb = new StringBuilder();
					for (String key : log.args()) {
						if (StringUtils.isEmpty(key)) {
							continue;
						}
						Object param = request.getParameter(key);
						if (StringUtils.isEmpty(param)) {
							param = modelAndView.getModel().get(key);
						}
						sb.append(param).append(",");
					}

					if (sb.length() > 0) {
						sb.deleteCharAt(sb.length() - 1);
					}

					logService.log(user.getUsername(), user.getName(), log.value(), sb.toString(), IpUtils.getRemoteHost(request), cost);
				}
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}