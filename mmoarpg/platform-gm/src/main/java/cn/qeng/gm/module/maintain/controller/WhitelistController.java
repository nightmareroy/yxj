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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.maintain.service.WhitelistService;

/**
 * 登录白名单。
 * 
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("maintain/whitelist")
public class WhitelistController {
	@Autowired
	private WhitelistService whitelistService;

	@RequestMapping("/manage/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(OperationType.MAINTAIN_WHITELIST_MANAGE)
	public ModelAndView manage() {
		return list();
	}

	@RequestMapping("/list/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	public ModelAndView list() {
		ModelAndView view = new ModelAndView("maintain/whitelist/list");
		view.addObject("result", whitelistService.getAll());
		return view;
	}

	@RequestMapping("/addUI/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(OperationType.MAINTAIN_WHITELIST_ADDUI)
	public ModelAndView addUI() {
		return new ModelAndView("maintain/whitelist/edit");
	}

	@ResponseBody
	@RequestMapping("/add/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(value = OperationType.MAINTAIN_WHITELIST_ADD, args = "ip")
	public String add(int whitelisttype, int type, String ip, int hour, String describe) {
		whitelistService.edit(whitelisttype, type, ip, hour, describe);
		return "";
	}

	/**
	 * 编辑界面
	 */
	@Token
	@RequestMapping("/editUI/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(value = OperationType.MAINTAIN_WHITELIST_EDITUI, args = "ip")
	public ModelAndView editUI(int whitelisttype, int type, String ip) {
		ModelAndView view = this.addUI();
		view.addObject("whitelist", whitelistService.getWhitelist(whitelisttype, type, ip));
		return view;
	}

	/**
	 * 编辑
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(value = OperationType.MAINTAIN_WHITELIST_EDIT, args = "ip")
	public String edit(int whitelisttype, int type, String ip, int hour, String describe) {
		whitelistService.edit(whitelisttype, type, ip, hour, describe);
		return "OK";
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete/")
	@Auth(AuthResource.WHITELIST_MANAGE)
	@RecordLog(value = OperationType.MAINTAIN_WHITELIST_DELETE, args = "ip")
	public ModelAndView delete(int whitelisttype, int type, String ip) {
		whitelistService.delete(whitelisttype, type, ip);
		return this.list();
	}
}