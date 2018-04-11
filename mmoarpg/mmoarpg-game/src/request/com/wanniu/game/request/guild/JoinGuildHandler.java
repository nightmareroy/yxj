package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.JoinGuildRequest;
import pomelo.area.GuildHandler.JoinGuildResponse;

@GClientEvent("area.guildHandler.joinGuildRequest")
public class JoinGuildHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		JoinGuildRequest req = JoinGuildRequest.parseFrom(pak.getRemaingBytes());
		String c2s_guildId = req.getC2SGuildId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				JoinGuildResponse.Builder res = JoinGuildResponse.newBuilder();

				GuildResult resData = player.guildManager.joinGuild(c2s_guildId);
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
