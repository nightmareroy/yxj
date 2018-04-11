package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.GetGuildAreaListResponse;


/**
 * 据点战请求据点列表协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.getGuildAreaListRequest")
public class GetGuildAreaListHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildAreaListResponse.Builder res = GetGuildAreaListResponse.newBuilder();
				String msg = player.guildFortManager.handleGetGuildAreaList(res);
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
