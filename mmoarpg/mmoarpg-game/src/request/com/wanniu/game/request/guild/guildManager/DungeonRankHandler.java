package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;
import java.util.ArrayList;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler.DungeonRankRequest;
import pomelo.guild.GuildManagerHandler.DungeonRankResponse;
import pomelo.guild.GuildManagerHandler.RankInfo;

@GClientEvent("guild.guildManagerHandler.dungeonRankRequest")
public class DungeonRankHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		DungeonRankRequest req = DungeonRankRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DungeonRankResponse.Builder res = DungeonRankResponse.newBuilder();
				ArrayList<RankInfo> data = GuildService.dungeonRank(player.getId(), req.getS2CType());
				res.setS2CCode(OK);
				res.addAllS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
