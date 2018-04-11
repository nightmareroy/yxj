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
package cn.qeng.gm.module.maintain.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.MLogExportService;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Controller
@RequestMapping("/maintain/log")
public class MLogExportController {
	@Autowired
	private MLogExportService mlogExportService;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	/**
	 * 查看日志提取页面
	 */
	@Auth(AuthResource.MLOG_EXPORT)
	@RecordLog(OperationType.MAINTAIN_MLOG_LIST)
	@RequestMapping("/list")
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") String collections) {
		ModelAndView view = new ModelAndView("maintain/mlog/list");
		view.addObject("maxtime", LocalDate.now());
		return view;
	}

	/**
	 * 查看以往生成的所有日志文件
	 */
	@ResponseBody
	@RequestMapping(value = "/mlog/", produces = "application/json;charset=utf-8")
	public String getMlog() {
		return JSON.toJSONString(mlogExportService.getMlog());
	}

	/**
	 * 根据页面请求生成相应的文件
	 */
	@Auth(AuthResource.MLOG_EXPORT)
	@RecordLog(value = OperationType.MAINTAIN_MLOG_CREATE, args = { "struct" })
	@RequestMapping("/create/")
	public ModelAndView create(HttpServletRequest request, @RequestParam(required = false) String struct, @RequestParam(required = false) String entry, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate start, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate end, @RequestParam(required = false, defaultValue = "") String note) {
		ModelAndView view = new ModelAndView("maintain/mlog/list");
		view.addObject("maxtime", LocalDate.now());
		if (struct == null || struct.isEmpty() || entry == null || entry.isEmpty() || start == null || end == null) {
			return view;
		}
		Date createTime = new Date();
		// 文件名
		String fileName = struct + "_" + formatter.format(start) + "_" + formatter.format(end) + "_" + System.currentTimeMillis() / 1000 + ".csv";

		// 记录到数据库的下载路径,由于最后会压缩成zip，所以此处要先修改好
		String uri = "/mlog/" + formatter.format(LocalDate.now()) + "/" + fileName.replace("csv", "zip");
		// 获取生成文件的路径
		String url = request.getSession().getServletContext().getRealPath("/mlog/" + formatter.format(LocalDate.now()) + "/") + "/" + fileName;
		int id = mlogExportService.insertLog(struct, entry, start, end, createTime, uri, note, fileName, url);
		mlogExportService.createLog(struct, entry.split("::"), start, end, url, id);

		return view;
	}

	@ResponseBody
	@RequestMapping(value = "/download/", produces = "application/json;charset=utf-8")
	public String download() {
		return JSON.toJSONString(mlogExportService.getInfo());
	}

	@RequestMapping("/del/")
	@Auth(AuthResource.MLOG_EXPORT)
	@RecordLog(value = OperationType.MAINTAIN_MLOG_DELETE, args = { "name" })
	public ModelAndView delete(HttpServletRequest request, @RequestParam(required = false) int id, @RequestParam(required = false) String name) throws Exception {
		mlogExportService.delete(request, id);
		return list("");
	}

	@PostConstruct
	public void createMlog() {
		mlogExportService.initCreateMlog();
	}

	@ResponseBody
	@Auth(AuthResource.MLOG_EXPORT)
	@RecordLog(value = OperationType.MAINTAIN_MLOG_DOWNLOAD, args = { "name" })
	@RequestMapping(value = "/down/", produces = "application/json;charset=utf-8")
	public void down(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) int id, @RequestParam(required = false) String name) throws Exception {
		mlogExportService.downloadFile(request, response, id);
	}
}