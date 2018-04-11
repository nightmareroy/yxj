package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.ChangeGuildNameRequest;
import pomelo.area.GuildHandler.ChangeGuildNameResponse;

@GClientEvent("area.guildHandler.changeGuildNameRequest")
public class ChangeGuildNameHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ChangeGuildNameRequest req = ChangeGuildNameRequest.parseFrom(pak.getRemaingBytes());
		String name = req.getName();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeGuildNameResponse.Builder res = ChangeGuildNameResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult ret = player.guildManager.changeGuildName(name);
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
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_TOO_SHORT"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_TOO_LONG"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_MATERIAL_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -8) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_SPECIAL_CHAR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -9) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_BLACK_STRING"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 2) {
					GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NAME_CHANGE_CD").replace("{cd}", String.valueOf(prop.changeNameCD)));
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
