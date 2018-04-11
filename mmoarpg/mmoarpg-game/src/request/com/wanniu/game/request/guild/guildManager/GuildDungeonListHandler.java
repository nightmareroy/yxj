package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler.DungeonList;
import pomelo.guild.GuildManagerHandler.GuildDungeonListResponse;

@GClientEvent("guild.guildManagerHandler.guildDungeonListRequest")
public class GuildDungeonListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GuildDungeonListResponse.Builder res = GuildDungeonListResponse.newBuilder();

				DungeonList data = GuildService.guildDungeonList(player.getId());
				if (null != data) {
					res.setS2CCode(OK);
					res.setS2CList(data);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}
