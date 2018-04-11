package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyDailyAwardRequest;
import pomelo.area.GuildFortHandler.ApplyDailyAwardResponse;


/**
 * 据点战请求领取占领据点的每日奖励协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyDailyAwardRequest")
public class ApplyDailyAwardHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ApplyDailyAwardRequest req = ApplyDailyAwardRequest.parseFrom(pak.getRemaingBytes());
		int fortId = req.getAreaId();//据点编号
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyDailyAwardResponse.Builder res = ApplyDailyAwardResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyDailyAward(fortId);
				if (msg != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
