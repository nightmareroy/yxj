package com.wanniu.game.request.guild.guildTech;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.guildTech.GuildTechManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildTechHandler.GetGuildTechInfoResponse;
import pomelo.area.GuildTechHandler.GuildTechInfo;

@GClientEvent("area.guildTechHandler.getGuildTechInfoRequest")
public class GetGuildTechInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetGuildTechInfoRequest req = GetGuildTechInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildTechInfoResponse.Builder res = GetGuildTechInfoResponse.newBuilder();

				GuildTechManager guildTechManager = player.guildManager.guildTechManager;
				guildTechManager.getGuildTechAsync();
				GuildTechInfo techInfo = guildTechManager.toJson4PayLoad();
				res.setS2CCode(OK);
				res.setS2CTechInfo(techInfo);
				res.setS2CContribution(player.guildManager.getContribution());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}