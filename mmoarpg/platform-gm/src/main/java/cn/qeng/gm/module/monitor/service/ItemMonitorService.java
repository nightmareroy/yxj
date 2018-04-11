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
package cn.qeng.gm.module.monitor.service;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.monitor.MonitorConstant;
import cn.qeng.gm.module.monitor.domain.ItemMonitor;
import cn.qeng.gm.module.monitor.domain.ItemMonitorRepository;

/**
 * 道具监控处理
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class ItemMonitorService {
	private final static Logger logger = LogManager.getLogger(MoneyMonitorService.class);
	@Autowired
	private ItemMonitorRepository itemMonitorRepository;

	/**
	 * 游戏道具监控查询处理
	 */
	public Page<ItemMonitor> getItemMonitorResult(int page) {
		logger.info("查询游戏道具监控记录");
		long stateTime = System.nanoTime();
		long endTime = System.nanoTime();
		logger.info("MogoDB查询游戏道具监控用时Time={}", (endTime - stateTime) / 100_0000f);
		Collections.sort(itemMonitorRepository.findAll(), (o1, o2) -> o1.getEventTime().compareTo(o2.getEventTime()));
		return new PageImpl<>(itemMonitorRepository.findAll(), new PageRequest(page, PageConstant.MAX_SIZE), itemMonitorRepository.findAll().size());
	}

	/**
	 * 处理游戏道具异常结果
	 */
	public void checkItemMonitorResult(int id) {
		ItemMonitor monitor = itemMonitorRepository.findOne(id);
		monitor.setState(MonitorConstant.MONEYMONITOR_APPROVE);
		itemMonitorRepository.save(monitor);
	}
}
