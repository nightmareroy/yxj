package com.wanniu.game.request.guild;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.ChangeOfficeNameRequest;
import pomelo.area.GuildHandler.ChangeOfficeNameResponse;
import pomelo.area.GuildHandler.OfficeName;

@GClientEvent("area.guildHandler.changeOfficeNameRequest")
public class ChangeOfficeNameHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ChangeOfficeNameRequest req = ChangeOfficeNameRequest.parseFrom(pak.getRemaingBytes());
		List<OfficeName> officeNames = req.getOfficeNamesList();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeOfficeNameResponse.Builder res = ChangeOfficeNameResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult ret = player.guildManager.changeOfficeName(officeNames);
				int result = ret.result;
				if (result == 0) {
					res.setS2CCode(OK);
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
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NAME_EMPTY"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NAME_TOO_LONG"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -8) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NAME_SPECIAL_CHAR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -9) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NAME_BLACK_STRING"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOB_NAME_REPEATED"));
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
