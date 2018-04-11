package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.InvitePlayerJoinMyGuildRequest;
import pomelo.area.GuildHandler.InvitePlayerJoinMyGuildResponse;

@GClientEvent("area.guildHandler.invitePlayerJoinMyGuildRequest")
public class InvitePlayerJoinMyGuildHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		InvitePlayerJoinMyGuildRequest req = InvitePlayerJoinMyGuildRequest.parseFrom(pak.getRemaingBytes());
		String c2s_playerId = req.getC2SPlayerId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				InvitePlayerJoinMyGuildResponse.Builder res = InvitePlayerJoinMyGuildResponse.newBuilder();

				GuildResult resData = player.guildManager.invitePlayerJoinGuild(c2s_playerId);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				res.setS2CCode(FAIL);
				String errMsg = "";
				if (-1 == resData.result) {
					errMsg = LangService.getValue("GUILD_NOT_JOIN");
				} else if (-2 == resData.result) {
					errMsg = LangService.getValue("GUILD_NO_POWER");
				} else if (-3 == resData.result) {
					errMsg = LangService.getValue("GUILD_INVITE_OTHER_IN_GUILD");
				} else if (-4 == resData.result) {
					errMsg = LangService.getValue("GUILD_INVITE_OTHER_OFFLINE");
				} else if (-7 == resData.result) {
					errMsg = LangService.getValue("GUILD_NOT_EXIST");
				} else {
					errMsg = LangService.getValue("SOMETHING_ERR");
				}
				res.setS2CMsg(errMsg);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
