package com.wanniu.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.ConstsTR;
import com.wanniu.core.redis.GCache;

import cn.qeng.common.gm.po.AnnouncementPO;

/**
 * 登录公告处理器
 * 
 * @author lxm
 * @author 小流氓(176543888@qq.com)
 */
public class AnnounceServer {
	private static final AnnounceServer instance = new AnnounceServer();

	public static AnnounceServer getInstance() {
		return instance;
	}

	/**
	 * 正在使用的登录公告
	 */
	public static AnnouncementPO announce;

	/**
	 * 初始化登录公告
	 */
	public void init() {
		announce = null;

		Map<String, String> map = GCache.hgetAll(ConstsTR.announcement.value);
		List<AnnouncementPO> list = new ArrayList<>();
		map.forEach((k, v) -> {
			list.add(JSON.parseObject(v, AnnouncementPO.class));
		});
		for (AnnouncementPO p : list) {
			if (p.getIsUse()) {
				announce = p;
				Out.info("读取登录公告：", announce.getContent());
			}
		}
	}
}