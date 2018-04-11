package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.JoinGuildOfPlayerRequest;
import pomelo.area.GuildHandler.JoinGuildOfPlayerResponse;

@GClientEvent("area.guildHandler.joinGuildOfPlayerRequest")
public class JoinGuildOfPlayerHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		JoinGuildOfPlayerRequest req = JoinGuildOfPlayerRequest.parseFrom(pak.getRemaingBytes());
		String c2s_playerId = req.getC2SPlayerId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				JoinGuildOfPlayerResponse.Builder res = JoinGuildOfPlayerResponse.newBuilder();

				GuildResult resData = player.guildManager.joinGuildByPlayerId(c2s_playerId);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				res.setS2CCode(FAIL);
				String errMsg = GuildCommonUtil.getJoinGuildErrorMsg(resData);
				res.setS2CMsg(errMsg);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
