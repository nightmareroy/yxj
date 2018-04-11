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
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.AuctionItemPO;

/**
 * 竟拍物品竟拍超时.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class AuctionTimeoutHandler implements Runnable {
	private final String id;

	public AuctionTimeoutHandler(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		AuctionItemPO item = AuctionDataManager.getInstance().getAuctionItem(id);
		if (item == null) {
			return;
		}
		synchronized (item) {
			// 状态切换.
			switch (item.state) {
			case AuctionConst.STATE_SHOW:
				updateAuctionState(item);
				break;
			case AuctionConst.STATE_AUCTION:
				updateShowState(item);
				break;
			default:
				Out.warn("竞拍物品出现非法状态了.", item.state);
				break;
			}
		}
	}

	private void updateShowState(AuctionItemPO item) {
		// 没有公会就是世界竞拍，那可以回收此物品了。
		if (StringUtils.isEmpty(item.guildId)) {
			AuctionService.getInstance().settlementAttribution(item);
		}
		// 有公会
		else {
			// 有公会，那就是没人要，转入世界
			String playerId = item.playerId;
			if (StringUtils.isEmpty(playerId)) {
				Out.debug("竟拍物品流入世界竟拍.id=", item.id);
				// 所有公会的先移除掉
				AuctionService.getInstance().syncRemoveAuctionItem(item);

				// 有仙盟，记录日志
				String guildId = item.guildId;
				if (StringUtils.isNotEmpty(guildId)) {
					int type = AuctionConst.LOG_TYPE_NOT_AUCTION;
					GWorld.getInstance().ansycExec(() -> AuctionService.getInstance().log(guildId, playerId, type, item.db.code, item.curPrice));
				}

				// 能进入世界...
				if (AuctionDataManager.getInstance().canEnterWorld(item.db.code)) {
					item.state = AuctionConst.STATE_SHOW;
					item.guildId = null;
					LocalDateTime now = LocalDateTime.now();
					item.stateOverTime = now.plusMinutes(GlobalConfig.Auction_WorldShowTime);

					// 挂载超时任务
					AuctionService.getInstance().addDelayJob(now, item);

					// 通知所有同步的人.
					AuctionService.getInstance().syncAddAuctionItemInfo(Arrays.asList(item), AuctionConst.TYPE_WORLD_AUCTION);

					// 处理红点
					AuctionService.getInstance().processWorldAuctionsPoint();
				} else {
					Out.info("世界竞拍此物品已达上限，直接回收掉...id=", item.id);
					item.state = AuctionConst.STATE_SHOW;
					item.guildId = null;
					AuctionService.getInstance().settlementAttribution(item);
				}
				// 100秒后尝试分红
				JobFactory.addDelayJob(() -> AuctionService.getInstance().trySendAuctionBonus(guildId), 100_000);
			}
			// 有人竞拍过，结算.
			else {
				AuctionService.getInstance().settlementAttribution(item);
			}
		}
	}

	private void updateAuctionState(AuctionItemPO item) {
		Out.debug("竟拍物品切换竟拍状态.id=", item.id);

		LocalDateTime now = LocalDateTime.now();
		item.state = AuctionConst.STATE_AUCTION;
		// 世界竟拍
		if (StringUtils.isEmpty(item.guildId)) {
			item.stateOverTime = now.plusMinutes(GlobalConfig.Auction_WorldBiddingTime);
		}
		// 仙盟竟拍
		else {
			item.stateOverTime = now.plusMinutes(GlobalConfig.Auction_GuildBiddingTime);
		}

		// 挂载超时任务
		AuctionService.getInstance().addDelayJob(now, item);

		// 通知所有同步的人.
		AuctionService.getInstance().syncAuctionItemInfo(item);
	}
}