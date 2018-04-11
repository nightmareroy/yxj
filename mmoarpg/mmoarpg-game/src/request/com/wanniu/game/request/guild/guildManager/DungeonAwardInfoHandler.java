package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.guildDungeon.GuildDungeonAward;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler.DungeonAwardInfoResponse;

@GClientEvent("guild.guildManagerHandler.dungeonAwardInfoRequest")
public class DungeonAwardInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// DungeonAwardInfoRequest req = DungeonAwardInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DungeonAwardInfoResponse.Builder res = DungeonAwardInfoResponse.newBuilder();

				GuildDungeonAward data = GuildService.dungeonAwardInfo(player.getId());
				res.setS2CCode(OK);
				res.addAllItemInfos(data.itemInfos);
				res.setDiceLeftTime(data.diceLeftTime);
				res.addAllGetDungeonScoreInfo(data.getDungeonScoreInfo);
				res.setIsFightOver(data.isFightOver);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}