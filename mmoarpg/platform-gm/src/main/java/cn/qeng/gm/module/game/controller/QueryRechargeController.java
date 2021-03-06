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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.qeng.common.gm.vo.GmPayVO;
import cn.qeng.common.gm.vo.GmResult;
import cn.qeng.gm.api.QueryRechargeAPI;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 查询充值入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/query/recharge")
public class QueryRechargeController {
	@Autowired
	private ServerService serverService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.QUERY_RECHARGE)
	@RecordLog(OperationType.QUERY_RECHARGE_MANAGE)
	public ModelAndView manage() {
		ModelAndView view = new ModelAndView("/game/query/recharge/list");
		serverService.buildServerList(view);
		return view;
	}

	/**
	 * 查询充值记录.
	 */
	@RequestMapping("/list/")
	@Auth(AuthResource.QUERY_RECHARGE)
	@RecordLog(value = OperationType.QUERY_RECHARGE_REQEST, args = { "playername" })
	public ModelAndView list(int serverId, String playername) {
		ModelAndView view = this.manage();

		view.addObject("playername", playername);
		// 已选择的区服
		Map<Integer, Integer> selectedServerSet = new HashMap<>();
		selectedServerSet.put(serverId, serverId);
		view.addObject("selectedServerMap", selectedServerSet);

		// 查询...
		String json = new QueryRechargeAPI(playername).request(serverId).getResult();
		// 维护状态
		if (ErrorCode.SERVER_NOT_FOUND.equals(json)) {
			view.addObject(ErrorCode.SERVER_NOT_FOUND, true);
		}
		// 没找到指定名称的玩家信息
		else if (StringUtils.isEmpty(json)) {
			view.addObject(ErrorCode.TARGET_NOT_FOUND, true);
		}
		// 正常结果
		else {
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmPayVO>>() {}));
		}
		return view;
	}
}