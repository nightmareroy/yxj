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
package cn.qeng.gm.module.maintain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.maintain.service.EmailService;

/**
 * 监控邮件管理.
 * 
 * @since 2.0
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("maintain/email")
public class EmailController {
	@Autowired
	private EmailService emailService;

	@RequestMapping("/manage/")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(OperationType.EAMIL_CONFIG_LIST)
	public ModelAndView manage() {
		return list(0);
	}

	@RequestMapping("/list/")
	@Auth(AuthResource.EMAIL_CONFIG)
	public ModelAndView list(@RequestParam(value = "page", required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("maintain/email/list");
		view.addObject("page", emailService.getAll(page));
		return view;
	}

	/**
	 * 添加邮件界面.
	 */
	@Token
	@RequestMapping("/addUI")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(OperationType.EAMIL_CONFIG_ADD_UI)
	public ModelAndView addUI() {
		return new ModelAndView("maintain/email/edit");
	}

	/**
	 * 添加一个邮件
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/add/")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(value = OperationType.EAMIL_CONFIG_ADD, args = "addr")
	public String add(@RequestParam String addr, @RequestParam String name, @RequestParam String remarks) {
		emailService.edit(addr, name, remarks);
		return "SUCCESS";
	}

	/**
	 * 编辑邮件界面
	 */
	@Token
	@RequestMapping("/editUI/")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(value = OperationType.EAMIL_CONFIG_EDIT_UI, args = "addr")
	public ModelAndView editUI(@RequestParam String addr) {
		ModelAndView view = this.addUI();
		view.addObject("email", emailService.getEmail(addr));
		return view;
	}

	/**
	 * 编辑一个邮件
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(value = OperationType.EAMIL_CONFIG_DELETE, args = "addr")
	public String edit(@RequestParam String addr, @RequestParam String name, @RequestParam String remarks) {
		emailService.edit(addr, name, remarks);
		return "SUCCESS";
	}

	/**
	 * 删除一个平台
	 */
	@RequestMapping("/delete/")
	@Auth(AuthResource.EMAIL_CONFIG)
	@RecordLog(value = OperationType.EAMIL_CONFIG_DELETE, args = "addr")
	public ModelAndView delete(@RequestParam String addr) {
		emailService.delete(addr);
		return this.list(0);
	}
}