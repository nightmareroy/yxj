package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.MyGuildInfo;
import pomelo.area.GuildHandler.UpgradeGuildLevelResponse;

@GClientEvent("area.guildHandler.upgradeGuildLevelRequest")
public class UpgradeGuildLevelHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// UpgradeGuildLevelRequest req =
		// UpgradeGuildLevelRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeGuildLevelResponse.Builder res = UpgradeGuildLevelResponse.newBuilder();

				GuildResult ret = player.guildManager.upgradeGuildLevel();
				int result = ret.result;
				if (result == 0) {
					MyGuildInfo myGuildInfo = player.guildManager.getMyGuildInfo();
					res.setS2CCode(OK);
					res.setS2CGuildInfo(myGuildInfo);
					body.writeBytes(res.build().toByteArray());
					player.guildManager.update();
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
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NEED_BLESS_LEVEL").replace("{buildingLevel}", String.valueOf(ret.needLevel)));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NEED_TECH_LEVEL").replace("{buildingLevel}", String.valueOf(ret.needLevel)));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NEED_DEPOT_LEVEL").replace("{buildingLevel}", String.valueOf(ret.needLevel)));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_EXP_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_FUND_NOT_ENOUGH"));
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
