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

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.guidDepot.GuildAuctionLog;
import com.wanniu.game.player.WNPlayer;

import pomelo.auction.AuctionHandler.AuctionLog;
import pomelo.auction.AuctionHandler.AuctionLogResponse;

/**
 * 拉取竞拍日志列表.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@GClientEvent("auction.auctionHandler.auctionLogRequest")
public class AuctionLogHandle extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		String guildId = player.guildManager.getGuildId();
		if (StringUtils.isEmpty(guildId)) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AuctionLogResponse.Builder response = AuctionLogResponse.newBuilder();

				for (GuildAuctionLog log : GuildDao.getGuildAuctionLog(guildId)) {
					AuctionLog.Builder builder = AuctionLog.newBuilder();
					builder.setId(log.type);
					builder.setItem(MessageUtil.itemColorName(log.item.qColor, log.item.name));
					builder.setTime(log.time);
					builder.setNum(log.price);
					if (StringUtils.isNotEmpty(log.player)) {
						builder.setRole1(log.player);
					}
					response.addS2CLog(builder);
				}

				response.setS2CCode(OK);
				body.writeBytes(response.build().toByteArray());
			}
		};
	}
}