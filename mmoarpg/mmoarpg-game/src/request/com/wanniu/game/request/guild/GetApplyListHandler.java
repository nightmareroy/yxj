package com.wanniu.game.request.guild;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.ApplyInfo;
import pomelo.area.GuildHandler.GetApplyListResponse;

@GClientEvent("area.guildHandler.getApplyListRequest")
public class GetApplyListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetApplyListRequest req = GetApplyListRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetApplyListResponse.Builder res = GetApplyListResponse.newBuilder();

				List<ApplyInfo> applyList = player.guildManager.getMyGuildApplyList();
				res.setS2CCode(OK);
				res.addAllS2CApplyList(applyList);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
