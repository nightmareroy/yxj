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
package cn.qeng.gm.module.game.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wanniu.GmScheduledExecutor;

import cn.qeng.gm.api.RollNoticeAPI;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.RollNotice;
import cn.qeng.gm.module.game.domain.RollNoticeRepository;
import cn.qeng.gm.module.game.domain.RollNoticeResult;
import cn.qeng.gm.module.maintain.service.ServerService;

/**
 * 系统跑马灯业务实现类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class RollNoticeService {
	private final static Logger logger = LogManager.getLogger(RollNoticeService.class);

	/**
	 * 正在执行的Scheduled，用来控制任务的取消
	 */
	public Map<Integer, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

	@Autowired
	private ServerService serverService;
	@Autowired
	private RollNoticeRepository rollNoticeRepository;

	@PostConstruct
	public void init() {
		rollNoticeRepository.findAll().forEach(notice -> this.resetRollNoticeTask(notice));
	}

	@PreDestroy
	public void destory() {
		futures.forEach((k, v) -> v.cancel(true));
	}

	/**
	 * 所有的跑马灯
	 */
	public RollNoticeResult getNotices() {
		RollNoticeResult result = new RollNoticeResult();
		List<RollNotice> r = rollNoticeRepository.findAll();
		if (r != null && r.size() > 0) {
			result.getData().addAll(r);
		}
		return result;
	}

	/**
	 * 删除跑马灯
	 */
	public void deleteNotice(int id) {
		RollNotice notice = this.getNotice(id);
		if (notice != null) {
			rollNoticeRepository.delete(notice);
			// 停掉定时任务
			this.cancelScheduledFuture(notice.getId());
		}
	}

	private void cancelScheduledFuture(int id) {
		ScheduledFuture<?> future = futures.remove(id);
		if (future != null) {
			future.cancel(true);
		}
	}

	/**
	 * 删除账号
	 */
	public void delete(int id) {
		rollNoticeRepository.delete(id);
	}

	public void addRollNotice(SessionUser user, Integer[] sids, int interval, String content, Date startTime, Date endTime) {
		RollNotice notice = new RollNotice();

		notice.setSids(JSON.toJSONString(sids));
		notice.setSidsLength(sids.length);

		notice.setInterval(interval);
		notice.setStartTime(startTime);
		notice.setEndTime(endTime);
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		notice.setContent(content);
		notice.setCreateTime(new Date());
		notice.setModifyTime(notice.getCreateTime());
		rollNoticeRepository.save(notice);

		this.resetRollNoticeTask(notice);
	}

	public RollNotice getNotice(int id) {
		return rollNoticeRepository.findOne(id);
	}

	public void editRollNotice(SessionUser user, int id, Integer[] sids, int interval, String content, Date startTime, Date endTime) {
		RollNotice notice = this.getNotice(id);

		notice.setSids(JSON.toJSONString(sids));
		notice.setSidsLength(sids.length);

		notice.setInterval(interval);
		notice.setStartTime(startTime);
		notice.setEndTime(endTime);
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		notice.setContent(content);
		notice.setModifyTime(new Date());
		rollNoticeRepository.save(notice);

		this.resetRollNoticeTask(notice);
	}

	/**
	 * 重新计算此滚动公告的定时任务.
	 */
	private void resetRollNoticeTask(RollNotice notice) {
		// 移除老的
		this.cancelScheduledFuture(notice.getId());

		// 没过期
		if (System.currentTimeMillis() < notice.getEndTime().getTime()) {
			long delay = notice.getStartTime().getTime() - System.currentTimeMillis();
			delay = Math.max(delay, 3000);// 至少延迟3秒执行

			ScheduledFuture<?> f = GmScheduledExecutor.executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					List<Integer> servers = serverService.getSidList(notice.getSids());
					// 发送...
					for (Integer serverId : servers) {
						try {
							new RollNoticeAPI(notice.getContent()).request(serverId);
						} catch (Exception e) {
							logger.error("推送滚动公告异常。sid={}", serverId, e);
						}
					}

					if (System.currentTimeMillis() > notice.getEndTime().getTime()) {
						futures.remove(notice.getId()).cancel(true);
					}
				}
			}, delay, notice.getInterval() * 60 * 1000, TimeUnit.MILLISECONDS);

			// 任务缓存上.
			futures.put(notice.getId(), f);
		}
	}
}