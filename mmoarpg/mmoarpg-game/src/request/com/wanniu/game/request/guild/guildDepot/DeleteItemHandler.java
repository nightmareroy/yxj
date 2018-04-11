package com.wanniu.game.request.guild.guildDepot;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildDepotHandler.DeleteItemRequest;
import pomelo.area.GuildDepotHandler.DeleteItemResponse;

@GClientEvent("area.guildDepotHandler.deleteItemRequest")
public class DeleteItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		DeleteItemRequest req = DeleteItemRequest.parseFrom(pak.getRemaingBytes());
		int c2s_fromIndex = req.getC2SFromIndex();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DeleteItemResponse.Builder res = DeleteItemResponse.newBuilder();
				GuildResult resData = player.guildManager.deleteEquipFromDepot(c2s_fromIndex);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setDeleteCount(resData.deleteCount);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
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
					res.setS2CMsg(LangService.getValue("DEPOT_DELETE_T_COUNT_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EMPTY_DEPOT_BAG_INDEX"));
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