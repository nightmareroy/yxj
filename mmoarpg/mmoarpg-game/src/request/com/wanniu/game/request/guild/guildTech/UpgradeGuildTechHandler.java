package com.wanniu.game.request.guild.guildTech;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.UpgradeLevel;
import com.wanniu.game.guild.guildTech.GuildTechManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildTechHandler.UpgradeGuildTechResponse;

@GClientEvent("area.guildTechHandler.upgradeGuildTechRequest")
public class UpgradeGuildTechHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// UpgradeGuildTechRequest req = UpgradeGuildTechRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeGuildTechResponse.Builder res = UpgradeGuildTechResponse.newBuilder();

				GuildTechManager guildTechManager = player.guildManager.guildTechManager;
				GuildResult ret = guildTechManager.upgradeTechLevel();
				int result = ret.result;
				UpgradeLevel resData = (UpgradeLevel) ret.data;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CLevel(resData.level);
					res.setS2CFund((int) resData.fund);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NEED_GUILD_LEVEL").replace("{buildingLevel}", String.valueOf(ret.needLevel)));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
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
