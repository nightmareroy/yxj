package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.GetMyGuildInfoResponse;
import pomelo.area.GuildHandler.MyGuildInfo;

@GClientEvent("area.guildHandler.getMyGuildInfoRequest")
public class GetMyGuildInfoHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetMyGuildInfoRequest req = GetMyGuildInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetMyGuildInfoResponse.Builder res = GetMyGuildInfoResponse.newBuilder();

				MyGuildInfo guildInfo = player.guildManager.getMyGuildInfo();
				res.setS2CCode(OK);
				if (null != guildInfo) {
					res.setS2CGuildInfo(guildInfo);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
