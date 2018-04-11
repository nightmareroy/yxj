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
package cn.qeng.gm.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.service.LogService;

/**
 * Session监听器.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		event.getSession().setMaxInactiveInterval(5 * 60);// 5分钟
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		long startTime = System.currentTimeMillis();
		// 如果Session失效，那判定一下有不有登录过，记录一下退出日志.
		SessionUser user = SessionManager.getSessionUser(event.getSession());
		if (user != null) {
			WebApplicationContext context = WebApplicationContextUtils.findWebApplicationContext(event.getSession().getServletContext());
			long cost = System.currentTimeMillis() - startTime;
			context.getBean(LogService.class).log(user.getUsername(), user.getName(), OperationType.LOGOUT_SESSION_TIMEOUT, user.getLastLoginIp(), cost);
		}
	}
}