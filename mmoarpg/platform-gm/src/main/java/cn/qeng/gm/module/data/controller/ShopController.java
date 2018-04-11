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
package cn.qeng.gm.module.data.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.qeng.gm.core.MoneyType;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.module.data.service.ShopService;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Controller
@RequestMapping("/data/shop")
public class ShopController {
	@Autowired
	private ServerService serverService;
	@Autowired
	private ShopService shopService;

	/**
	 * 左边导航
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.DATA_SHOP_DISTRIBUTION)
	@RecordLog(OperationType.DATA_SHOP_MANAGER)
	public ModelAndView manage() {
		ModelAndView view = new ModelAndView("/data/shop/list");
		serverService.buildServerList(view);
		view.addObject("moneyType", MoneyType.values());//
		view.addObject("shopType", MoneyType.DIAMOND.getShopType());
		return view;
	}

	/**
	 * 查看玩家升级记录
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.DATA_SHOP_DISTRIBUTION)
	@RecordLog(value = OperationType.DATA_SHOP_LIST)
	public ModelAndView list(@RequestParam(required = false, name = "serverIds") String serverIds, String reservationtime, int shopType) throws ParseException {
		ModelAndView view = this.manage();

		List<Integer> sidList = new ArrayList<>();
		if (!StringUtils.isEmpty(serverIds)) {// 已选择的区服
			Map<Integer, Integer> selectedServerMap = new HashMap<>();
			for (String id : serverIds.split(",")) {
				Integer sid = Integer.parseInt(id);
				sidList.add(sid);
				selectedServerMap.put(sid, sid);
			}
			view.addObject("selectedServerMap", selectedServerMap);
		}

		// 时间段
		Date startTime = null;
		Date endTime = null;
		if (!StringUtils.isEmpty(reservationtime)) {
			String[] time = reservationtime.split(" - ", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.parse(time[0]);
			endTime = sdf.parse(time[1]);
		}
		view.addObject("reservationtime", reservationtime);
		view.addObject("shopType", shopType);

		view.addObject("data", shopService.getShopDataResult(sidList, startTime, endTime, shopType));
		return view;
	}
}