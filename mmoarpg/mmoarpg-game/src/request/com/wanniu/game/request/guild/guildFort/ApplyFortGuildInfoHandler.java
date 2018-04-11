package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyFortGuildInfoRequest;
import pomelo.area.GuildFortHandler.ApplyFortGuildInfoResponse;


/**
 * 据点战在进入采集场景和战斗场景是请求参战双方的协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyFortGuildInfoRequest")
public class ApplyFortGuildInfoHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyFortGuildInfoResponse.Builder res = ApplyFortGuildInfoResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyFortGuildInfo(res);
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
