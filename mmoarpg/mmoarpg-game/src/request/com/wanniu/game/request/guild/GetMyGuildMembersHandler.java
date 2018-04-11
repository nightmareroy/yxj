package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.MyGuildMember;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.GetMyGuildMembersResponse;

@GClientEvent("area.guildHandler.getMyGuildMembersRequest")
public class GetMyGuildMembersHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetMyGuildMembersRequest req = GetMyGuildMembersRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetMyGuildMembersResponse.Builder res = GetMyGuildMembersResponse.newBuilder();

				GuildResult ret = player.guildManager.getMyGuildMemberList();				
				if (0 != ret.result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
				}

				res.setS2CCode(OK);
				MyGuildMember data = (MyGuildMember) ret.data;
				if (null != data) {
					res.addAllS2CMemberList(data.list);
					res.setS2CLeftKickNum(data.leftKickNum);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
