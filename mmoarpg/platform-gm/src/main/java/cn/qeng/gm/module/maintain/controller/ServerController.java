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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.AuthServer;
import com.wanniu.GServer;
import com.wanniu.gm.message.GameInfoMessage;

import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.service.ServerService;
import io.netty.channel.Channel;

/**
 * 区服管理入口
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/maintain/server")
public class ServerController {
	@Autowired
	private ServerService serverService;

	/**
	 * 查看区服管理（左边导航）
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.SERVER_MANAGE)
	@RecordLog(OperationType.MAINTAIN_SERVER_MANAGE)
	public ModelAndView manage() {
		return this.list(0);
	}

	/**
	 * 查看区服列表.
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.SERVER_MANAGE)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = new ModelAndView("maintain/server/list");
		view.addObject("page", serverService.getAllServers(page));
		return view;
	}

	@RequestMapping("/addUI/")
	@Auth(AuthResource.SERVER_MANAGE)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("maintain/server/edit");
		return view;
	}

	@ResponseBody
	@RequestMapping("/add/")
	@Auth(AuthResource.SERVER_MANAGE)
	public String add(int serverId, String serverName, @RequestParam(required = false, defaultValue = "") String describe) {
		return serverService.addServer(serverId, serverName, describe) ? "OK" : "XX";
	}

	/**
	 * 编辑界面
	 */
	@Token
	@RequestMapping("/editUI/{id}/")
	@Auth(AuthResource.SERVER_MANAGE)
	public ModelAndView editUI(@PathVariable("id") int id) {
		Server server = ServerService.getServer(id);
		ModelAndView view = this.addUI();
		view.addObject("server", server);

		// 处理开服日期
		if (server != null && server.getOpenDate() != null) {
			Instant instant = server.getOpenDate().toInstant();
			view.addObject("opendate", LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
		} else {
			view.addObject("opendate", LocalDate.now());
		}
		// 对外时间
		if (server != null && server.getExternalTime() != null) {
			Instant instant = server.getExternalTime().toInstant();
			view.addObject("externalTime", LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
		} else {
			view.addObject("externalTime", LocalDateTime.now());
		}
		return view;
	}

	/**
	 * 编辑一个服务器
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.SERVER_MANAGE)
	public String edit(int serverId, String serverName, @RequestParam(required = false, defaultValue = "") String describe, @RequestParam(required = false, defaultValue = "0") int areaId, //
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date opendate, @RequestParam(required = false, defaultValue = "0") int olLimit, //
			@RequestParam(required = false, defaultValue = "0") int showState, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date externalTime) {
		Server server = ServerService.getServer(serverId);
		server.setServerName(serverName);
		server.setAreaId(areaId);
		server.setDescribe(describe);
		server.setOpenDate(opendate);
		server.setOlLimit(olLimit);
		server.setShowState(showState);
		server.setExternalTime(externalTime);
		ServerService.saveServer(server);

		// 同步从服状态...
		for (Integer sid : ServerService.getSidList(serverId)) {
			if (sid == serverId) {
				continue;
			}

			Server s = ServerService.getServer(sid);
			s.setIp(server.getIp());
			s.setPort(server.getPort());
			s.setAreaId(areaId);
			s.setOpenDate(opendate);
			s.setOlLimit(olLimit);
			s.setShowState(showState);
			s.setExternalTime(externalTime);
			ServerService.saveServer(s);
		}

		if (server.getAreaId() == 0) {
			// 修改区
			JSONObject json = new JSONObject();
			json.put("type", 0x105);
			json.put("appId", server.getAppId());
			json.put("areaId", server.getId());
			json.put("areaName", server.getServerName());
			AuthServer.publish(json);
		} else {
			ServerService.resetCalServerByGroupCaches();
			// 同步信息到游戏服...
			Channel channel = GServer.getInstance().getChannel(serverId);
			if (channel != null) {
				channel.writeAndFlush(new GameInfoMessage(server));

				// 走Redis订阅同步登录服.
				JSONObject json = new JSONObject();
				json.put("type", 0x109);
				json.put("appId", server.getAppId());
				json.put("logicServerId", server.getId());
				json.put("show", server.getShowState());
				AuthServer.publish(json);
			}
		}
		return "OK";
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.SERVER_MANAGE)
	public ModelAndView delete(@PathVariable("id") int id) {
		ServerService.deleteServer(id);
		return this.list(0);
	}

	/**
	 * 编辑一个服务器
	 */
	@ResponseBody
	@RequestMapping("/update/")
	@Auth(AuthResource.SERVER_MANAGE)
	public String update(int sid, int type, boolean checked) {
		Server server = ServerService.getServer(sid);
		switch (type) {
		case 1:
			server.setNew(checked);
			break;
		case 2:
			server.setHot(checked);
			break;
		case 3:
			server.setRecommend(checked);
			break;
		default:
			break;
		}
		ServerService.saveServer(server);

		// 同步信息到游戏服...
		Channel channel = GServer.getInstance().getChannel(server.getId());
		if (channel != null) {
			channel.writeAndFlush(new GameInfoMessage(server));
		}
		return "OK";
	}
}