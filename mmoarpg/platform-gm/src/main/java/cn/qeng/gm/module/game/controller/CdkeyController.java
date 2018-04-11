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
package cn.qeng.gm.module.game.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.common.gm.po.CdkCode;
import cn.qeng.common.gm.po.CdkPO;
import cn.qeng.gm.core.ItemTemplateManager;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.game.service.CdkeyService;

/**
 * CDKEY入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/cdkey")
public class CdkeyController {

	@Autowired
	private CdkeyService cdkeyService;
	@Autowired
	private ItemTemplateManager itemTemplateManager;

	/**
	 * 左边导航CDKEY列表.
	 */
	@RequestMapping("/manage/")
	@Auth(AuthResource.MANAGE_CDKEY_QUERY)
	@RecordLog(OperationType.MANAGER_CDKEY_LIST)
	public ModelAndView manage() {
		return this.list(0);
	}

	/**
	 * CDKEY列表分页.
	 */
	@RequestMapping("/list/")
	@Auth(AuthResource.MANAGE_CDKEY_QUERY)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("game/cdkey/list");
		view.addObject("page", cdkeyService.getCdkeyPage(page));
		return view;
	}

	/**
	 * 添加礼包界面.
	 */
	@RequestMapping("/add/ui/")
	@Auth(AuthResource.MANAGE_CDKEY_CONFIG)
	@RecordLog(OperationType.MANAGER_CDKEY_ADDUI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("game/cdkey/edit");
		view.addObject("items", itemTemplateManager.getItems());
		return view;
	}

	/**
	 * 添加礼包.
	 */
	@ResponseBody
	@RequestMapping("/add/")
	@Auth(AuthResource.MANAGE_CDKEY_CONFIG)
	@RecordLog(value = OperationType.MANAGER_CDKEY_ADD, args = "name")
	public String add(String name, String datepicker, int type, int minLevel, int useMax, int codeNum, //
			@RequestParam(required = false, name = "itemIdList[]") String[] itemIdList, @RequestParam(required = false, name = "itemNameList[]") String[] itemNameList, @RequestParam(required = false, name = "itemNumList[]") int[] itemNumList//
	) throws ParseException {

		// 最基本的认证一下.
		if (codeNum > 10_0000 || itemIdList == null || itemIdList.length == 0) {
			return "error";
		}

		cdkeyService.add(name, datepicker, type, minLevel, useMax, codeNum, itemIdList, itemNameList, itemNumList);
		return "OK";
	}

	/**
	 * 查询兑换码明细，可导出.
	 */
	@RequestMapping("/export/")
	@Auth(AuthResource.MANAGE_CDKEY_EXPORT)
	@RecordLog(value = OperationType.MANAGER_CDKEY_EXPORT, args = { "id" })
	public ModelAndView export(int id) {
		ModelAndView view = new ModelAndView("game/cdkey/export");
		view.addObject("data", cdkeyService.getExportCdk(id));
		return view;
	}

	/**
	 * 查询兑换码使用情况.
	 */
	@RequestMapping("/query/ui/")
	@Auth(AuthResource.MANAGE_CDKEY_QUERY)
	@RecordLog(value = OperationType.MANAGER_CDKEY_QUERY_UI)
	public ModelAndView queryUI() {
		return new ModelAndView("game/cdkey/query");
	}

	/**
	 * 查询兑换码使用情况.
	 */
	@RequestMapping("/query/")
	@Auth(AuthResource.MANAGE_CDKEY_QUERY)
	@RecordLog(value = OperationType.MANAGER_CDKEY_QUERY, args = "code")
	public ModelAndView query(String code) {
		ModelAndView view = this.queryUI();
		view.addObject("code", code);

		CdkPO cdkey = cdkeyService.getExportCdk(Integer.parseInt(code.substring(0, code.indexOf("X"))));
		if (cdkey != null) {
			view.addObject("cdkey", cdkey);

			for (CdkCode x : cdkey.getCdkCodes()) {
				if (x.getCode().equals(code)) {
					view.addObject("cdkCode", x);
					break;
				}
			}
		}
		return view;
	}
}