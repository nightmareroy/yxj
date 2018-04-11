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
package com.wanniu.game.auction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.AuctionItemPO;
import com.wanniu.redis.GameDao;

/**
 * 竞拍数据管理器.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class AuctionDataManager {
	private static AuctionDataManager instance;

	public static AuctionDataManager getInstance() {
		if (instance == null) {
			synchronized (ConsignmentLineService.class) {
				if (instance == null) {
					instance = new AuctionDataManager();
				}
			}
		}
		return instance;
	}

	// 所有竟拍的物品缓存.
	public static final Map<String, AuctionItemPO> items = new ConcurrentHashMap<>();

	public AuctionDataManager() {
		long start = System.currentTimeMillis();
		Map<String, String> result = GCache.hgetAll(ConstsTR.auction_itemsTR.value);
		for (Map.Entry<String, String> e : result.entrySet()) {
			AuctionItemPO item = JSON.parseObject(e.getValue(), AuctionItemPO.class);
			// 服务器重启，重新补个时间
			boolean show = item.state == AuctionConst.STATE_SHOW;
			LocalDateTime now = LocalDateTime.now();
			// 世界
			if (StringUtils.isEmpty(item.guildId)) {
				item.stateOverTime = now.plusMinutes(show ? GlobalConfig.Auction_WorldShowTime : GlobalConfig.Auction_WorldBiddingTime);
			}
			// 公会
			else {
				item.stateOverTime = now.plusMinutes(show ? GlobalConfig.Auction_GuildShowTime : GlobalConfig.Auction_GuildBiddingTime);
			}
			// 挂载一个任务
			AuctionService.getInstance().addDelayJob(now, item);

			items.put(e.getKey(), item);
		}
		Out.info("加载竟拍数据耗时:", (System.currentTimeMillis() - start), " ms");
	}

	/**
	 * 停服逻辑.
	 */
	public void onCloseGame() {
		for (Map.Entry<String, AuctionItemPO> e : items.entrySet()) {
			// 保存到redis
			GameDao.update(ConstsTR.auction_itemsTR.value, e.getKey(), e.getValue());
			// 保存到数据库
			// GameDao.updateToDB(ConstsTR.auction_itemsTR, e.getKey(), ModifyDataType.MAP);
		}
	}

	/**
	 * 以公会ID来取物品.
	 */
	public List<AuctionItemPO> getItemByPredicate(Predicate<? super AuctionItemPO> predicate) {
		return items.values().stream().filter(predicate).collect(Collectors.toList());
	}

	public boolean hasGuildItem(String guildId) {
		return items.values().stream().filter(v -> guildId.equals(v.guildId)).findFirst().isPresent();
	}

	// 能进入世界竞拍
	public synchronized boolean canEnterWorld(String itemcode) {
		return items.values().stream().filter(v -> v.guildId == null && itemcode.equals(v.db.code)).count() < 3;
	}

	public boolean hasWorldItem() {
		return items.values().stream().filter(v -> v.guildId == null).findFirst().isPresent();
	}

	public AuctionItemPO getAuctionItem(String id) {
		return items.get(id);
	}

	public void addAuctionItem(AuctionItemPO aitem) {
		items.put(aitem.id, aitem);
	}

	public void removeAuctionItem(String id) {
		items.remove(id);
		GCache.hremove(ConstsTR.auction_itemsTR.value, id);
	}
}