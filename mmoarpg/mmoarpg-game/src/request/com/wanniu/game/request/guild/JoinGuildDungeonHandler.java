package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.guild.guildDungeon.GuildDungeonResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.JoinGuildDungeonRequest;
import pomelo.area.GuildHandler.JoinGuildDungeonResponse;

@GClientEvent("area.guildHandler.joinGuildDungeonRequest")
public class JoinGuildDungeonHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		JoinGuildDungeonRequest req = JoinGuildDungeonRequest.parseFrom(pak.getRemaingBytes());
		int c2s_type = req.getC2SType();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				JoinGuildDungeonResponse.Builder res = JoinGuildDungeonResponse.newBuilder();
				Area area = player.getArea();
				GuildDungeonResult data = player.guildManager.joinGuildDungeon(area, c2s_type);
				if (data.result) {
					res.setS2CCode(OK);
					res.setS2CType(data.type);
					body.writeBytes(res.build().toByteArray());
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(data.info);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}