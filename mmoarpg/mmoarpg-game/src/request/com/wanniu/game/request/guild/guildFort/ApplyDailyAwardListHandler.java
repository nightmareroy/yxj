package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyDailyAwardListResponse;


/**
 * 据点战请求每日可领取奖励列表协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyDailyAwardListRequest")
public class ApplyDailyAwardListHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyDailyAwardListResponse.Builder res = ApplyDailyAwardListResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyDailyAwardList(res);
				if (msg != null) {
					res.setS2CCode(FAIL);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
