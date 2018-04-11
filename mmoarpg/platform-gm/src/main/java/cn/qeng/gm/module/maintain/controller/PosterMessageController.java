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

import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.PosterMessageService;

/**
 * 紧急通知联系人添加控制入口
 * 
 * @since 2.0
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("maintain/poster")
public class PosterMessageController {
	@Autowired
	private PosterMessageService posterMessageService;

	/**
	 * welcome页面更新紧急联系人信息
	 */
	@SuppressWarnings("deprecation")
	@Auth(AuthResource.POSTER_EDIT)
	@RecordLog(OperationType.BACKSTAGE_POSTER_EDIT)
	@ResponseBody
	@RequestMapping("/update")
	public String update(@RequestParam(value = "head", required = false, defaultValue = "") String head, @RequestParam(value = "title", required = false, defaultValue = "") String title, @RequestParam(required = true, defaultValue = "") String name, @RequestParam(required = true, defaultValue = "") String status, @RequestParam(required = true, defaultValue = "") String poster, @RequestParam(required = true, defaultValue = "") String phone) {
		head = URLDecoder.decode(URLDecoder.decode(head));
		title = URLDecoder.decode(URLDecoder.decode(title));
		posterMessageService.updateInfo(head, title, name, status, poster, phone);
		return "0";
	}
}
