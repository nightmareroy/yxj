package com.wanniu.game.request.guild.guildDepot;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildDepotHandler.DepositItemRequest;
import pomelo.area.GuildDepotHandler.DepositItemResponse;

@GClientEvent("area.guildDepotHandler.depositItemRequest")
public class DepositItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		DepositItemRequest req = DepositItemRequest.parseFrom(pak.getRemaingBytes());
		int c2s_fromIndex = req.getC2SFromIndex();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DepositItemResponse.Builder res = DepositItemResponse.newBuilder();
				if (c2s_fromIndex == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult resData = player.guildManager.depositEquipToDepot(c2s_fromIndex);
				int result = resData.result;
				if (result == 0) {
					player.taskManager.dealTaskEvent(TaskType.GUILD_CONTRIBUTE, 1);
					res.setS2CCode(OK);
					res.setS2CBagGrid(resData.bagGrid);
					res.setDepositCount(resData.depositCount);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EMPTY_BAG_INDEX"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_NOT_EQUIP"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_IS_BIND"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_CANNOT_DEPOSIT"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_DEPOSIT_COUNT_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -7) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_LEVEL_NOT_NOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -8) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_COLOR_NOT_NOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -9) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_CONFIG_NOT_EXIST"));
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
					res.setS2CMsg(LangService.getValue("DEPOT_UPLEVEL_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_LEVEL_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_JOB_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_EQUIP_QUALITY_NOT_IN_CONDITION"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == 7) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_SPACE_NOT_ENOUGH"));
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
