package com.wanniu.game.request.guild.guildBless;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.UpgradeLevel;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBlessHandler.UpgradeBlessResponse;

@GClientEvent("area.guildBlessHandler.upgradeBlessRequest")
public class UpgradeBlessHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// UpgradeBlessRequest req = UpgradeBlessRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeBlessResponse.Builder res = UpgradeBlessResponse.newBuilder();

				GuildResult resData = player.guildManager.upgradeBlessLevel();
				UpgradeLevel data = (UpgradeLevel) resData.data;
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CLevel(data.level);
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
					res.setS2CMsg(LangService.getValue("BLESS_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BLESS_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_LEVEL_NOT_ENOUGH"));
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