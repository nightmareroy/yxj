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
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.auction.AuctionConst;
import com.wanniu.game.auction.AuctionService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AuctionItemPO;

import pomelo.auction.AuctionHandler.AuctionListRequest;
import pomelo.auction.AuctionHandler.AuctionListResponse;

/**
 * 拉取竞拍列表.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@GClientEvent("auction.auctionHandler.auctionListRequest")
public class AuctionListHandle extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		AuctionListRequest request = AuctionListRequest.parseFrom(pak.getRemaingBytes());
		int type = request.getC2SType();// 竞拍大类(1=仙盟竞拍，2=世界竞拍)

		WNPlayer player = (WNPlayer) pak.getPlayer();

		AuctionListResponse.Builder response = AuctionListResponse.newBuilder();

		List<AuctionItemPO> items = new ArrayList<>();
		switch (type) {
		case AuctionConst.TYPE_GUILD_AUCTION:
			response.setS2CBonus(player.guildManager.calAuctionBonus());
			items = AuctionService.getInstance().getGuildAuctionItemList(player);
			break;
		case AuctionConst.TYPE_WORLD_AUCTION:
			items = AuctionService.getInstance().getWorldAuctionItemList(player);
			break;
		case AuctionConst.TYPE_SELF_AUCTION:
			items = AuctionService.getInstance().getSelfAuctionItemList(player);
			break;
		default:
			Out.warn("拉取竞拍列表大类错误 type=", type);
			break;
		}

		for (AuctionItemPO item : items) {
			response.addS2CData(AuctionService.getInstance().toAuctionItem(player, item));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				response.setS2CCode(OK);
				body.writeBytes(response.build().toByteArray());
			}
		};
	}
}