package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.GetGuildAreaApplyListRequest;
import pomelo.area.GuildFortHandler.GetGuildAreaApplyListResponse;


/**
 * 据点战请求指定据点押注列表协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.getGuildAreaApplyListRequest")
public class GetGuildAreaApplyListHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetGuildAreaApplyListRequest req = GetGuildAreaApplyListRequest.parseFrom(pak.getRemaingBytes());
		int fortId = req.getAreaId();//据点编号
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildAreaApplyListResponse.Builder res = GetGuildAreaApplyListResponse.newBuilder();
				String msg = player.guildFortManager.handleGetGuildAreaApplyList(res,fortId);
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
