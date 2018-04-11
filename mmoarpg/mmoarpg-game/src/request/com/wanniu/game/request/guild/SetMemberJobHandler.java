package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.SetMemberJobRequest;
import pomelo.area.GuildHandler.SetMemberJobResponse;

@GClientEvent("area.guildHandler.setMemberJobRequest")
public class SetMemberJobHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SetMemberJobRequest req = SetMemberJobRequest.parseFrom(pak.getRemaingBytes());
		String memberId = req.getMemberId();
		int job = req.getJob();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SetMemberJobResponse.Builder res = SetMemberJobResponse.newBuilder();
				GuildResult ret = player.guildManager.setMemberJob(memberId, job);
				int result = ret.result;
				if (result == 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -20) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -21) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NOT_SET_SELF"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_MEMBER_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_HIGHER_THAN_YOU"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_FULL"));
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
