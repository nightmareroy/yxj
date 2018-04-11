package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.MyGuildMember;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.MyGuildInfo;
import pomelo.area.GuildHandler.TransferPresidentRequest;
import pomelo.area.GuildHandler.TransferPresidentResponse;

@GClientEvent("area.guildHandler.transferPresidentRequest")
public class TransferPresidentHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		TransferPresidentRequest req = TransferPresidentRequest.parseFrom(pak.getRemaingBytes());
		String memberId = req.getMemberId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TransferPresidentResponse.Builder res = TransferPresidentResponse.newBuilder();

				GuildResult ret = player.guildManager.transferGuildPresident(memberId);
				int result = ret.result;
				if (result == 0) {
					MyGuildMember data = (MyGuildMember) player.guildManager.getMyGuildMemberList().data;
					MyGuildInfo guildInfo = player.guildManager.getMyGuildInfo();
					res.setS2CCode(OK);
					res.addAllS2CMemberList(data.list);
					res.setS2CGuildInfo(guildInfo);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -20) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_MEMBER_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				res.setS2CCode(FAIL);
				res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
