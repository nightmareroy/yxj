package com.wanniu.game.request.guild;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.GetGuildListRequest;
import pomelo.area.GuildHandler.GetGuildListResponse;
import pomelo.area.GuildHandler.GuildInfo;

@GClientEvent("area.guildHandler.getGuildListRequest")
public class GetGuildListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetGuildListRequest req = GetGuildListRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildListResponse.Builder res = GetGuildListResponse.newBuilder();
				List<GuildInfo> guildList = player.guildManager.getGuildList(req.getC2SName());
				res.setS2CCode(OK);
				res.addAllS2CGuildList(guildList);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
