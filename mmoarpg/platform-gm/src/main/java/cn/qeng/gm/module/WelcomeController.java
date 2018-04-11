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
package cn.qeng.gm.module;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.module.maintain.service.PosterMessageService;

/**
 * 主要入口.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
@Controller
public class WelcomeController {
	@Autowired
	private PosterMessageService posterMessageService;

	/**
	 * 登录页面.
	 */
	@RequestMapping("/")
	public String index() {
		return "index";
	}

	/**
	 * 没有权限
	 */
	@RequestMapping("/noauth")
	public String noauth() {
		return "noauth";
	}

	@RequestMapping("/welcome")
	@Auth(AuthResource.WELCOME)
	public ModelAndView welcome(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("welcome");
		view.addObject("today", LocalDate.now());
		view.addObject("result", posterMessageService.getPoster());
		view.addObject("message", posterMessageService.getPosterMessage());
		return view;
	}

	/**
	 * 不支持的浏览器
	 */
	@RequestMapping("/unsupport-browser")
	public void unsupportBrowser() {}
}