package com.wanniu.game.request.guild.guildDepot;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.DepotUpgradeLevelData;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildDepotHandler.UpgradeDepotResponse;

@GClientEvent("area.guildDepotHandler.upgradeDepotRequest")
public class UpgradeDepotHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// UpgradeDepotRequest req = UpgradeDepotRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeDepotResponse.Builder res = UpgradeDepotResponse.newBuilder();

				GuildResult resData = player.guildManager.upgradeDepotLevel();
				int result = resData.result;
				DepotUpgradeLevelData data = (DepotUpgradeLevelData) resData.data;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CLevel(data.newLevel);
					res.setS2CFund((int) data.fund);
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
					res.setS2CMsg(LangService.getValue("DEPOT_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_LEVEL_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 7) {
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
