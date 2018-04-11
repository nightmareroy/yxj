package com.wanniu.game.request.guild.guildDepot;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.guidDepot.GuildCond;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildDepotHandler;
import pomelo.area.GuildDepotHandler.SetConditionRequest;
import pomelo.area.GuildDepotHandler.SetConditionResponse;

@GClientEvent("area.guildDepotHandler.setConditionRequest")
public class SetConditionHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SetConditionRequest req = SetConditionRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SetConditionResponse.Builder res = SetConditionResponse.newBuilder();

				int useLevel = req.getUseLevel();
				int useUpLevel = req.getUseUpLevel();
				int useJob = req.getUseJob();
				int minLevel = req.getMinLevel();
				int minUpLevel = req.getMinUpLevel();
				int minqColor = req.getMinqColor();
				int maxLevel = req.getMaxLevel();
				int maxUpLevel = req.getMaxUpLevel();
				int maxqColor = req.getMaxqColor();

				GuildDepotCondition cond = new GuildDepotCondition();
				cond.useCond = new GuildCond(useLevel, useUpLevel, useJob, 0);
				cond.minCond = new GuildCond(minLevel, minUpLevel, 0, minqColor);
				cond.maxCond = new GuildCond(maxLevel, maxUpLevel, 0, maxqColor);

				GuildResult resData = player.guildManager.setDepotCondition(cond);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					// 转换 注意此处的condition 用的是GuildDepotHandler包里的结构
					GuildDepotHandler.DepotCondition condition = GuildCommonUtil.toHandlerDepot(resData.newCondition);
					res.setS2CCondition(condition);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEPOT_QUALITY_CONDITION_INVALID"));
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
				}
				res.setS2CCode(FAIL);
				res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}