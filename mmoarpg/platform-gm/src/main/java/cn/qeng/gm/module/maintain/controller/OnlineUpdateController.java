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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.ServerConfigService;
import cn.qeng.gm.module.maintain.service.ServerPackageService;

/**
 * 在线更新入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/maintain/onlineupdate")
public class OnlineUpdateController {
	@Autowired
	private ServerConfigService serverConfigService;
	@Autowired
	private ServerPackageService serverPackageService;

	@RequestMapping("/manage")
	@Auth(AuthResource.ONLINE_UPDATE)
	@RecordLog(OperationType.ONLINE_UPDATE_MANAGE)
	public ModelAndView manage() {
		return this.list(0);
	}

	@RequestMapping("/list")
	@Auth(AuthResource.ONLINE_UPDATE)
	public ModelAndView list(int page) {
		ModelAndView view = new ModelAndView("maintain/onlineupdate/list");
		view.addObject("page", serverPackageService.getAllServerPackage(page));
		return view;
	}

	@RequestMapping("/uploadUI")
	@Auth(AuthResource.ONLINE_UPDATE)
	@RecordLog(OperationType.ONLINE_UPDATE_UPLOADUI)
	public ModelAndView uploadUI() {
		return new ModelAndView("maintain/onlineupdate/upload");
	}

	/**
	 * 上传文件.
	 */
	@ResponseBody
	@Auth(AuthResource.ONLINE_UPDATE)
	@RecordLog(OperationType.ONLINE_UPDATE_UPLOAD)
	@RequestMapping(value = "/upload/", method = RequestMethod.POST)
	public String uploadfileUpload(HttpServletRequest request, @RequestParam("GFile") CommonsMultipartFile GFile, @RequestParam("BFile") CommonsMultipartFile BFile) throws Exception {
		serverPackageService.upload(request, GFile, BFile);
		return "OK";
	}

	/**
	 * 使用UI
	 */
	@RequestMapping("/useUI")
	@Auth(AuthResource.ONLINE_UPDATE)
	@RecordLog(OperationType.ONLINE_UPDATE_USEUI)
	public ModelAndView useUI(int id) {
		ModelAndView view = new ModelAndView("maintain/onlineupdate/index");
		view.addObject("servers", serverConfigService.calServerByGroupCaches());
		view.addObject("packet", serverPackageService.getServerPackage(id));
		return view;
	}

	/**
	 * 一键更新
	 */
	@ResponseBody
	@Auth(AuthResource.ONLINE_UPDATE)
	@RequestMapping(value = "/update/")
	@RecordLog(OperationType.ONLINE_UPDATE_UPDATE)
	public String update(@RequestParam(name = "serverIds[]") int[] serverIds, @RequestParam int packageId) {
		serverPackageService.update(serverIds, packageId);
		return "OK";
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@Auth(AuthResource.ONLINE_UPDATE)
	@RecordLog(OperationType.ONLINE_UPDATE_DELETE)
	public ModelAndView delete(int id) {
		serverPackageService.delete(id);
		return this.list(0);
	}
}