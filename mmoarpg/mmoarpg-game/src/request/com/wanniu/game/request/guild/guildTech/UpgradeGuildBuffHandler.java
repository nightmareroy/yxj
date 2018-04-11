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

import pomelo.area.GuildTechHandler.GuildBuff;
import pomelo.area.GuildTechHandler.UpgradeGuildBuffResponse;

@GClientEvent("area.guildTechHandler.upgradeGuildBuffRequest")
public class UpgradeGuildBuffHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// UpgradeGuildBuffRequest req = UpgradeGuildBuffRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeGuildBuffResponse.Builder res = UpgradeGuildBuffResponse.newBuilder();

				GuildTechManager guildTechManager = player.guildManager.guildTechManager;
				GuildResult ret = guildTechManager.upgradeBuffLevel();
				UpgradeLevel resData = (UpgradeLevel) ret.data;
				int result = ret.result;
				if (result == 0) {
					GuildBuff buffData = guildTechManager.getBuffData();
					res.setS2CCode(OK);
					res.setS2CBuffInfo(buffData);
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
					res.setS2CMsg(LangService.getValue("TECH_BUFF_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_BUFF_NEED_TECH_LEVEL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
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
