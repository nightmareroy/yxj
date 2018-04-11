package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.GetGuildAreaDetailRequest;
import pomelo.area.GuildFortHandler.GetGuildAreaDetailResponse;


/**
 * 据点战请求指定据点详情协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.getGuildAreaDetailRequest")
public class GetGuildAreaDetailHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		GetGuildAreaDetailRequest req = GetGuildAreaDetailRequest.parseFrom(pak.getRemaingBytes());
		int fortId = req.getAreaId();//据点编号
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildAreaDetailResponse.Builder res = GetGuildAreaDetailResponse.newBuilder();
				String msg = player.guildFortManager.handleGetGuildAreaDetail(res,fortId);
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
