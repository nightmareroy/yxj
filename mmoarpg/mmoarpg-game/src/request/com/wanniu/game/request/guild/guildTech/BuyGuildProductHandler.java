package com.wanniu.game.request.guild.guildTech;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.guildTech.GuildTechManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildTechHandler.BuyGuildProductRequest;
import pomelo.area.GuildTechHandler.BuyGuildProductResponse;

@GClientEvent("area.guildTechHandler.buyGuildProductRequest")
public class BuyGuildProductHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		BuyGuildProductRequest req = BuyGuildProductRequest.parseFrom(pak.getRemaingBytes());
		int productId = req.getProductId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BuyGuildProductResponse.Builder res = BuyGuildProductResponse.newBuilder();
				GuildTechManager guildTechManager = player.guildManager.guildTechManager;
				GuildResult ret = guildTechManager.buyGuildTechProduct(productId);
				int result = ret.result;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CId(Integer.parseInt(ret.id));
					res.setS2CState(ret.state);
					res.setS2CContribution(player.guildManager.getContribution());
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_PRODUCT_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_PRODUCT_SELL_OUT"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PRODUCT_NEED_JOB_TOO_LOW"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PRODUCT_NEED_LEVEL_TOO_LOW"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_CONTRIBUTION_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -7) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
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