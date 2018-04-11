package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.guild.guildDungeon.GuildDungeon;
import com.wanniu.game.guild.guildDungeon.GuildDungeonResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.LeaveGuildDungeonResponse;

@GClientEvent("area.guildHandler.leaveGuildDungeonRequest")
public class LeaveGuildDungeonHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// LeaveGuildDungeonRequest req = LeaveGuildDungeonRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				LeaveGuildDungeonResponse.Builder res = LeaveGuildDungeonResponse.newBuilder();

				GuildDungeon area = (GuildDungeon) player.getArea();
				if (area.sceneType != Const.SCENE_TYPE.GUILD_DUNGEON.getValue()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("AREA_ID_NULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildDungeonResult data = player.guildManager.leaveDungeon(player);

				if (data.result) {
					res.setS2CCode(OK);
					res.setS2CMsg(data.info);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(data.info);
					body.writeBytes(res.build().toByteArray());
					return;
				}
			}
		};
	}
}