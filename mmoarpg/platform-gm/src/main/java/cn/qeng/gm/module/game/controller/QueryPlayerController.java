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

import cn.qeng.common.gm.vo.GmItemVO;
import cn.qeng.common.gm.vo.GmMountVO;
import cn.qeng.common.gm.vo.GmPetVO;
import cn.qeng.common.gm.vo.GmPlayerInfoVO;
import cn.qeng.common.gm.vo.GmPlayerRankVO;
import cn.qeng.common.gm.vo.GmPlayerSkillVO;
import cn.qeng.common.gm.vo.GmResult;
import cn.qeng.gm.api.QueryBagItemInfoAPI;
import cn.qeng.gm.api.QueryPlayerInfoAPI;
import cn.qeng.gm.api.QueryPlayerMountAPI;
import cn.qeng.gm.api.QueryPlayerPetAPI;
import cn.qeng.gm.api.QueryPlayerRankAPI;
import cn.qeng.gm.api.QueryPlayerSkillAPI;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 查询角色入口.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/game/query/player")
public class QueryPlayerController {
	@Autowired
	private ServerService serverService;

	/**
	 * （左边导航）
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.QUERY_PLAYER)
	@RecordLog(OperationType.QUERY_PLAYER_MANAGE)
	public ModelAndView manage() {
		ModelAndView view = new ModelAndView("/game/query/player/list");
		serverService.buildServerList(view);
		return view;
	}

	/**
	 * 查询角色详细信息.
	 */
	@RequestMapping("/list/")
	@Auth(AuthResource.QUERY_PLAYER)
	@RecordLog(value = OperationType.QUERY_PLAYER_REQEST, args = { "playername" })
	public ModelAndView list(int serverId, String playername, boolean vague) {
		ModelAndView view = this.manage();

		view.addObject("playername", playername);
		view.addObject("vague", vague);
		// 已选择的区服
		Map<Integer, Integer> selectedServerSet = new HashMap<>();
		selectedServerSet.put(serverId, serverId);
		view.addObject("selectedServerMap", selectedServerSet);
		view.addObject("selectedServerId", serverId);

		// 查询...
		String json = new QueryPlayerInfoAPI(playername, vague ? 1 : 0).request(serverId).getResult();
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
			if (vague) {
				view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmPlayerInfoVO>>() {}));
			} else {
				view.addObject("result", JSON.parseObject(json, GmPlayerInfoVO.class));
			}
		}
		return view;
	}

	/**
	 * 查询角色详细信息.
	 */
	@RequestMapping("/bag/item/")
	@Auth(AuthResource.QUERY_PLAYER)
	public ModelAndView queryBagItem(int serverId, String playerId, int type) {
		ModelAndView view = new ModelAndView("/game/query/player/bagitem_" + type);
		view.addObject("playerId", playerId);
		// 查询...
		String json = new QueryBagItemInfoAPI(playerId, type).request(serverId).getResult();
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
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmItemVO>>() {}));
		}
		return view;
	}

	/**
	 * 查询个人排行.
	 */
	@RequestMapping("/query/rank/")
	@Auth(AuthResource.QUERY_PLAYER)
	public ModelAndView queryPlayerRank(int serverId, String playerId) {
		ModelAndView view = new ModelAndView("/game/query/player/rank");
		view.addObject("playerId", playerId);
		// 查询...
		String json = new QueryPlayerRankAPI(playerId).request(serverId).getResult();
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
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmPlayerRankVO>>() {}));
		}
		return view;
	}

	/**
	 * 查询个人技能.
	 */
	@RequestMapping("/query/skill/")
	@Auth(AuthResource.QUERY_PLAYER)
	public ModelAndView queryPlayerSkill(int serverId, String playerId) {
		ModelAndView view = new ModelAndView("/game/query/player/skill");
		view.addObject("playerId", playerId);
		// 查询...
		String json = new QueryPlayerSkillAPI(playerId).request(serverId).getResult();
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
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmPlayerSkillVO>>() {}));
		}
		return view;
	}

	/**
	 * 查询个人宠物.
	 */
	@RequestMapping("/query/pet/")
	@Auth(AuthResource.QUERY_PLAYER)
	public ModelAndView queryPlayerPet(int serverId, String playerId) {
		ModelAndView view = new ModelAndView("/game/query/player/pet");
		view.addObject("playerId", playerId);
		// 查询...
		String json = new QueryPlayerPetAPI(playerId).request(serverId).getResult();
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
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmPetVO>>() {}));
		}
		return view;
	}

	/**
	 * 查询个人坐骑.
	 */
	@RequestMapping("/query/mount/")
	@Auth(AuthResource.QUERY_PLAYER)
	public ModelAndView queryPlayerMount(int serverId, String playerId) {
		ModelAndView view = new ModelAndView("/game/query/player/mount");
		view.addObject("playerId", playerId);
		// 查询...
		String json = new QueryPlayerMountAPI(playerId).request(serverId).getResult();
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
			view.addObject("result", JSON.parseObject(json, new TypeReference<GmResult<GmMountVO>>() {}));
		}
		return view;
	}
}