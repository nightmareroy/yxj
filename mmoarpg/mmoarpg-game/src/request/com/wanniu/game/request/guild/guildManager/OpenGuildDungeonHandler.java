package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.guildDungeon.OpenGuildDungeonResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler.OpenGuildDungeonResponse;

@GClientEvent("guild.guildManagerHandler.openGuildDungeonRequest")
public class OpenGuildDungeonHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// OpenGuildDungeonRequest req = OpenGuildDungeonRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				OpenGuildDungeonResponse.Builder res = OpenGuildDungeonResponse.newBuilder();

				OpenGuildDungeonResult data = GuildService.openGuildDungeon(player.getId());
				if (data.result) {
					res.setS2CCode(OK);
					res.setS2CWaitTime((int) data.waitTime);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(data.info);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}