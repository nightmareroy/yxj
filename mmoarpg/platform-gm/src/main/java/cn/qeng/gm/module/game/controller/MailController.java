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
package cn.qeng.gm.module.game.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.ItemTemplateManager;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.session.SessionManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.ItemInfo;
import cn.qeng.gm.module.game.service.MailService;
import cn.qeng.gm.module.maintain.service.ServerService;
import cn.qeng.gm.util.ItemUtils;

/**
 * 邮件控制入口
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/mail")
public class MailController {
	@Autowired
	private MailService mailService;
	@Autowired
	public ServerService serverService;
	@Autowired
	private ItemTemplateManager itemTemplateManager;

	/**
	 * 左边导航.邮件申请界面
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.MANAGE_MAIL_APPLY)
	@RecordLog(OperationType.MANAGER_MAIL_MANAGE)
	public ModelAndView manage() {
		ModelAndView view = new ModelAndView("game/mail/apply");
		view.addObject("items", itemTemplateManager.getItems());
		view.addObject("date", new Date());
		serverService.buildServerList(view);
		return view;
	}

	/**
	 * 申请邮件
	 */
	@ResponseBody
	@RequestMapping("/apply")
	@Auth(AuthResource.MANAGE_MAIL_APPLY)
	@RecordLog(value = OperationType.MANAGER_MAIL_ADD, args = { "title" })
	public String apply(HttpServletRequest request, @RequestParam(required = false, name = "serverIds[]") int[] serverIds, //
			@RequestParam(required = false, defaultValue = "0") int mailType, String playerId, String title, String content, String reason, //
			@RequestParam(required = false, name = "itemIdList[]") String[] itemIdList, @RequestParam(required = false, name = "itemNameList[]") String[] itemNameList, @RequestParam(required = false, name = "itemNumList[]") int[] itemNumList, //
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createRoleTime, int minLevel) {
		if (createRoleTime == null) {
			createRoleTime = new Date();
		}
		SessionUser user = SessionManager.getSessionUser(request);
		List<ItemInfo> itemList = ItemUtils.build(itemIdList, itemNameList, itemNumList);
		return mailService.apply(user, serverIds, mailType, playerId, title, content, reason, itemList, createRoleTime, minLevel);
	}

	/**
	 * 批量申请界面
	 */
	@RequestMapping("/batch/apply/")
	@Auth(AuthResource.MANAGE_MAIL_BATCH)
	@RecordLog(OperationType.MANAGER_MAIL_BATCH)
	public ModelAndView batchApply(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("game/mail/batch");
		view.addObject("page", mailService.getBatchApply(page));
		return view;
	}

	/**
	 * 清空批量申请
	 */
	@Auth(AuthResource.MANAGE_MAIL_BATCH)
	@RequestMapping("/batch/clean/")
	@RecordLog(OperationType.MANAGER_MAIL_CLEAN)
	public ModelAndView cleanBatchApply() {
		mailService.cleanBatchApply();
		return this.batchApply(0);
	}

	/**
	 * 上传文件.
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGE_MAIL_BATCH)
	@RecordLog(OperationType.MANAGER_MAIL_UPLOADFILE)
	@RequestMapping(value = "/batch/uploadfile/", method = RequestMethod.POST)
	public String uploadfileUpload(HttpServletRequest request, HttpServletResponse resp, @RequestParam("file") CommonsMultipartFile file) throws Exception {
		SessionUser user = SessionManager.getSessionUser(request);
		return String.valueOf(mailService.handleUploadFile(user, file));
	}

	/**
	 * 发送
	 */
	@RequestMapping("/batch/send/")
	@Auth(AuthResource.MANAGE_MAIL_BATCH)
	@RecordLog(OperationType.MANAGER_MAIL_SEND)
	public ModelAndView sendBatchApply() {
		mailService.sendBatchApply();
		return this.batchApply(0);
	}

	/**
	 * 左边导航.
	 */
	@Auth(AuthResource.MANAGE_MAIL_APPROVAL)
	@RecordLog(OperationType.MANAGER_MAIL_APPROVAL)
	@RequestMapping("/approval/manage/")
	public ModelAndView approvalManage() {
		return this.approvalList(0);
	}

	/**
	 * 福利审批界面
	 */
	@RequestMapping("/approval/list/")
	@Auth(AuthResource.MANAGE_MAIL_APPROVAL)
	public ModelAndView approvalList(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("game/mail/list");
		view.addObject("page", mailService.getWelfare(page));
		return view;
	}

	/**
	 * 同意福利审批
	 */
	@ResponseBody
	@RequestMapping("/agree/{id}/")
	@Auth(AuthResource.MANAGE_MAIL_APPROVAL)
	@RecordLog(OperationType.MANAGER_MAIL_AGREE)
	public ModelAndView agree(HttpServletRequest request, @PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		mailService.agreeMail(SessionManager.getSessionUser(request), id);
		return this.approvalList(page);
	}

	/**
	 * 拒绝福利审批
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGE_MAIL_APPROVAL)
	@RecordLog(OperationType.MANAGER_MAIL_REFUSE)
	@RequestMapping("/refuse/{id}/")
	public ModelAndView refuse(HttpServletRequest request, @PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		mailService.refuseMail(SessionManager.getSessionUser(request), id);
		return this.approvalList(page);
	}

	/**
	 * 删除游戏线上所有发送的邮件
	 */
	@ResponseBody
	@Auth(AuthResource.MANAGE_MAIL_APPROVAL)
	@RecordLog(OperationType.MANAGER_MAIL_DELETE)
	@RequestMapping("/delete/{id}/")
	public ModelAndView delete(HttpServletRequest request, @PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		mailService.deleteMail(id);
		return this.approvalList(page);
	}
}