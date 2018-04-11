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

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.api.HotfixAPI;
import cn.qeng.gm.api.rpc.RpcResponse;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 热修复入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/maintain/hotfix")
public class HotfixController {
	@Autowired
	private ServerService serverService;

	@RequestMapping("/index")
	@Auth(AuthResource.JAVA_HOTFIX)
	@RecordLog(OperationType.MAINTAIN_HOTFIX_INDEX)
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("maintain/hotfix/index");
		serverService.buildServerListx(view);
		return view;
	}

	/**
	 * 上传文件.
	 */
	@ResponseBody
	@Auth(AuthResource.JAVA_HOTFIX)
	@RecordLog(OperationType.MAINTAIN_HOTFIX_EXEC)
	@RequestMapping(value = "/exec/", method = RequestMethod.POST)
	public String uploadfileUpload(HttpServletRequest request, @RequestParam(name = "serverIds[]") int[] serverIds, String className, @RequestParam("classFile") CommonsMultipartFile file) throws Exception {
		InputStream is = file.getInputStream();
		byte[] bytes = new byte[is.available()];
		is.read(bytes);

		StringBuilder sb = new StringBuilder();
		for (int serverId : serverIds) {
			RpcResponse response = new HotfixAPI(className, bytes).request(serverId);
			sb.append("SID:").append(serverId).append("==>").append(response.getResult()).append("<br>");
		}
		return sb.toString();
	}
}