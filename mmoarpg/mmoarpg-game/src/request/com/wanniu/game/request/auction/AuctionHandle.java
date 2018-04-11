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
package com.wanniu.game.request.auction;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.auction.AuctionConst;
import com.wanniu.game.auction.AuctionDataManager;
import com.wanniu.game.auction.AuctionService;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RewardListCO;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.money.CostResult;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AuctionItemPO;

import pomelo.auction.AuctionHandler.AuctionRequest;
import pomelo.auction.AuctionHandler.AuctionResponse;

/**
 * 竞拍物品.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@GClientEvent("auction.auctionHandler.auctionRequest")
public class AuctionHandle extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		AuctionRequest request = AuctionRequest.parseFrom(pak.getRemaingBytes());
		String id = request.getItemId();
		int price = request.getPrice();

		// 物品不存了，提示已别人买走了...
		AuctionItemPO item = AuctionDataManager.getInstance().getAuctionItem(id);
		if (item == null) {
			return new ErrorResponse(LangService.getValue("AUCTION_ITEM_NOT_EXSIT"));
		}

		synchronized (item) {
			// 非竞拍状态
			if (item.state != AuctionConst.STATE_AUCTION) {
				return new ErrorResponse(LangService.getValue("AUCTION_NOT_START"));
			}

			// 这个物品的竞价模板
			Optional<RewardListCO> template = GameData.RewardLists.values().stream().filter(v -> item.db.code.equals(v.code)).findFirst();
			if (!template.isPresent()) {
				Out.warn("竞拍物品找不到竞价模板，code=", item.db.code);
				return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
			}

			// 判定价格是否有变化...
			if (price < item.maxPrice && item.nextPrice != price) {
				return new ErrorResponse(LangService.getValue("AUCTION_PRICE_CHANGE"));
			}

			WNPlayer player = (WNPlayer) pak.getPlayer();

			// 公会判定
			if (StringUtils.isNotEmpty(item.guildId) && !item.guildId.equals(player.guildManager.getGuildId())) {
				return new ErrorResponse(LangService.getValue("AUCTION_NOT_GUILD"));
			}

			// 如果竞价超出最大值，修正
			price = Math.min(price, item.maxPrice);

			// 扣钱
			CostResult result = player.moneyManager.costTicketAndDiamond(price, GOODS_CHANGE_TYPE.AUCTION);
			if (!result.isSuccess()) {
				return new ErrorResponse(LangService.getValue("TICKET_NOT_ENOUGH"));
			}

			int bonus = 0;
			// 有人拍过，还钱...
			if (StringUtils.isNotEmpty(item.playerId)) {
				bonus = price - item.curPrice;
				AuctionService.getInstance().restitution(item);
			} else {
				bonus = item.curPrice;
			}
			player.guildManager.addAuctionBonus(bonus);

			// 修正竞拍物品信息
			item.curPrice = price;
			item.nextPrice = Math.min(item.maxPrice, item.curPrice + template.get().addPrice * item.db.groupCount);
			item.playerId = player.getId();
			item.diamond = result.getValue(VirtualItemType.DIAMOND);
			item.ticket = result.getValue(VirtualItemType.CASH);

			// 修正剩余时间
			LocalDateTime now = LocalDateTime.now();
			long timeleft = Duration.between(now, item.stateOverTime).getSeconds();
			if (timeleft < GlobalConfig.Auction_LastAddTimes) {
				item.stateOverTime = now.plusSeconds(GlobalConfig.Auction_PerAddTimes + timeleft);
				AuctionService.getInstance().resetDelayJob(now, item);
			}

			// 参与者.
			if (item.participant == null) {
				item.participant = new HashSet<>();
			}
			item.participant.add(item.playerId);

			// 如果当前价格已到达最大，就当他是一口价结算归属
			if (item.curPrice >= item.maxPrice) {
				AuctionService.getInstance().settlementAttribution(item);
			}
			// 没有结算，需要推送更新.
			else {
				GWorld.getInstance().ansycExec(() -> AuctionService.getInstance().syncAuctionItemInfo(item));
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AuctionResponse.Builder response = AuctionResponse.newBuilder();
				response.setS2CCode(OK);
				body.writeBytes(response.build().toByteArray());
			}
		};
	}
}