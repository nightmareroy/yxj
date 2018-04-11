package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.guildDungeon.GuildDiceAwardResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler.DiceAwardRequest;
import pomelo.guild.GuildManagerHandler.DiceAwardResponse;

@GClientEvent("guild.guildManagerHandler.diceAwardRequest")
public class DiceAwardHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		DiceAwardRequest req = DiceAwardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DiceAwardResponse.Builder res = DiceAwardResponse.newBuilder();

				GuildDiceAwardResult data = GuildService.diceAward(player.getId(), req.getS2CPos());
				if (data.result) {
					res.setS2CCode(OK);
					res.setS2CItemInfo(data.itemInfo);
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