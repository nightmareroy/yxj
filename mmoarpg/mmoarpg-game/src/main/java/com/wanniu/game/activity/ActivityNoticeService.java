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
package com.wanniu.game.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Utils;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.activity.ActivityNoticeHandler.ContextInfo;
import com.wanniu.redis.GlobalDao;

import cn.qeng.common.gm.RedisKeyConst;
import cn.qeng.common.gm.po.GameNoticePO;

/**
 * 游戏内公告业务类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class ActivityNoticeService {
	private static final ActivityNoticeService instance = new ActivityNoticeService();
	// 同步间隔
	private static final int SYNC_INTERVAL = 60 * 1000;
	// 下次同步时间
	private long nextSyncTime = System.currentTimeMillis();
	// 缓存的公告
	private List<GameNoticePO> cacheNotices = new ArrayList<>();
	private List<String> cacheNoticeIds = new ArrayList<>();

	public static ActivityNoticeService getInstance() {
		return instance;
	}

	public String getNotice(WNPlayer player) {
		long now = System.currentTimeMillis();
		if (now >= nextSyncTime) {
			sync(now);
		} else {
			Out.debug("命中缓存游戏内公告...");
		}
		ArrayList<ContextInfo> list = new ArrayList<>(cacheNotices.size());
		for (GameNoticePO prop : cacheNotices) {
			ContextInfo info = new ContextInfo();
			info.ID = prop.getId();
			info.NoticeTitle = prop.getName();
			info.ReleaseTime = prop.getCreateDate();
			info.ReleasePerson = "";
			info.Content = prop.getContent();
			info.isRead = player.activityManager.isReward(prop.getId()) ? 1 : 0;
			list.add(info);
		}
		return JSON.toJSONString(list);
	}

	private void sync(long now) {
		Out.debug("开始同步游戏内公告...");
		Map<String, String> map = GlobalDao.hgetAll(RedisKeyConst.REDIS_KEY_GAME_NOTICE);
		List<GameNoticePO> listPo = new ArrayList<>(map.size());
		List<String> noticeIds = new ArrayList<>();
		map.forEach((k, v) -> {
			listPo.add(Utils.deserialize(v, GameNoticePO.class));
			noticeIds.add(k);
		});
		// 排序
		Collections.sort(listPo, (o1, o2) -> o2.getId() - o1.getId());

		this.cacheNotices = listPo;
		this.cacheNoticeIds = noticeIds;
		this.nextSyncTime = now + SYNC_INTERVAL;
	}

	public List<String> getNoticeKey() {
		return cacheNoticeIds;
	}
}