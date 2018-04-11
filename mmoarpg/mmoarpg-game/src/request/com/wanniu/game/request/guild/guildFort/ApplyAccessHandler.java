package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyAccessRequest;
import pomelo.area.GuildFortHandler.ApplyAccessResponse;


/**
 * 据点战请求进入战斗准备区的协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyAccessRequest")
public class ApplyAccessHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ApplyAccessRequest req = ApplyAccessRequest.parseFrom(pak.getRemaingBytes());
		int fortId = req.getAreaId();//据点编号
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyAccessResponse.Builder res = ApplyAccessResponse.newBuilder();
				String msg = player.guildFortManager.handleEnterPrepareArea(fortId);
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
