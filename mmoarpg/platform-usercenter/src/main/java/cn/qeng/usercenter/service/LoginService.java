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
package cn.qeng.usercenter.service;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.usercenter.ChannelHander;
import cn.qeng.usercenter.ErrorCode;

/**
 * 渠道登录业务逻辑处理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class LoginService implements ApplicationContextAware {
	private final static Logger logger = LogManager.getLogger(LoginService.class);
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private ChannelHander getChannelHander(String handlerName) {
		return applicationContext.getBean(handlerName, ChannelHander.class);
	}

	public LoginResult login(Map<String, String> paramMap) {
		String handlerName = "channel:" + paramMap.getOrDefault(LoginConst.PARAM_NAME_CHANNEL, "");
		if (!applicationContext.containsBean(handlerName)) {
			logger.warn("没有实现的渠道登录方式.paramMap={}", paramMap);
			return new LoginResult(false, ErrorCode.NOT_FOUND_CHANNEL_HANDLER);
		}
		ChannelHander handler = this.getChannelHander(handlerName);
		try {
			return handler.channelLogin(paramMap);
		} catch (Exception e) {
			logger.error("没有实现的渠道登录方式.paramMap={}", paramMap, e);
			return new LoginResult(false, ErrorCode.CHANNEL_SERVER_ERROR);
		}
	}
}