package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildSetData;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.SetGuildInfoRequest;
import pomelo.area.GuildHandler.SetGuildInfoResponse;

@GClientEvent("area.guildHandler.setGuildInfoRequest")
public class SetGuildInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SetGuildInfoRequest req = SetGuildInfoRequest.parseFrom(pak.getRemaingBytes());
		// String c2s_playerId = req.getC2SPlayerId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SetGuildInfoResponse.Builder res = SetGuildInfoResponse.newBuilder();
				GuildSetData params = new GuildSetData();
				params.entryLevel = req.getEntryLevel();
				params.entryUpLevel = req.getEntryUpLevel();
				params.guildMode = req.getGuildMode();
				GuildResult ret = player.guildManager.setGuildInfo(params);
				int result = ret.result;
				if (result == 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_ENTRY_MIN_LEVEL").replace("{roleLevel}", String.valueOf(prop.joinLv)));
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
