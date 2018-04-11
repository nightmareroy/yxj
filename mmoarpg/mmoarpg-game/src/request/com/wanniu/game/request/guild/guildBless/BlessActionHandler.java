package com.wanniu.game.request.guild.guildBless;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.GuildBlessActionData;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBlessHandler.BlessActionRequest;
import pomelo.area.GuildBlessHandler.BlessActionResponse;
import pomelo.area.GuildBlessHandler.BlessResult;

/**
 * 祈福
 * 
 * @author jjr
 *
 */
@GClientEvent("area.guildBlessHandler.blessActionRequest")
public class BlessActionHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		BlessActionRequest req = BlessActionRequest.parseFrom(pak.getRemaingBytes());
		int id = req.getId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BlessActionResponse.Builder res = BlessActionResponse.newBuilder();

					if (id <= 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					GuildResult resData = player.guildManager.blessAction(id, 1);
					int result = resData.result;
					if (result == 0) {
						player.taskManager.dealTaskEvent(TaskType.GUILD_PRAY, 1);
						GuildBlessActionData data = (GuildBlessActionData) resData.data;
						res.setS2CCode(OK);
						BlessResult.Builder s2c_result = BlessResult.newBuilder();
						s2c_result.setBlessCount(data.blessCount);
						s2c_result.setBlessValue(data.blessValue);
						s2c_result.setId(data.id);
						s2c_result.setFinishNum(data.finishNum);
						s2c_result.addAllFinishState(GuildCommonUtil.toList(data.finishState));
						res.setS2CResult(s2c_result.build());
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == -1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == -2) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("NOT_ENOUGH_ITEM"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == 2) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BLESS_TIMES_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == 3) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BLESS_ITEM_NOT_EXIST"));
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
