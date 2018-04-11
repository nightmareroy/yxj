package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyFundRequest;
import pomelo.area.GuildFortHandler.ApplyFundResponse;


/**
 * 据点战请求押注资金协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyFundRequest")
public class ApplyFundHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ApplyFundRequest req = ApplyFundRequest.parseFrom(pak.getRemaingBytes());
		int fortId = req.getAreaId();//据点编号
		int fund = req.getApplyFund();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyFundResponse.Builder res = ApplyFundResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyFund(res, fortId, fund);
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
