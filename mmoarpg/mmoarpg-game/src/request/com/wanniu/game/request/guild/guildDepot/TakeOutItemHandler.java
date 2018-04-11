package com.wanniu.game.request.guild.guildDepot;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildDepotHandler.TakeOutItemRequest;
import pomelo.area.GuildDepotHandler.TakeOutItemResponse;

@GClientEvent("area.guildDepotHandler.takeOutItemRequest")
public class TakeOutItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		TakeOutItemRequest req = TakeOutItemRequest.parseFrom(pak.getRemaingBytes());
		int c2s_fromIndex = req.getC2SFromIndex();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TakeOutItemResponse.Builder res = TakeOutItemResponse.newBuilder();

				if (c2s_fromIndex == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult resData = player.guildManager.takeOutEquipFromDepot(c2s_fromIndex);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EMPTY_DEPOT_BAG_INDEX"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_PAWN_GOLD_NOT_ENOUGH"));
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
