package com.wanniu.game.request.guild.guildTech;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.guildTech.GuildTechManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildTechHandler.GuildSkill;
import pomelo.area.GuildTechHandler.UpgradeGuildSkillRequest;
import pomelo.area.GuildTechHandler.UpgradeGuildSkillResponse;

@GClientEvent("area.guildTechHandler.upgradeGuildSkillRequest")
public class UpgradeGuildSkillHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		UpgradeGuildSkillRequest req = UpgradeGuildSkillRequest.parseFrom(pak.getRemaingBytes());
		int skillId = req.getSkillId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeGuildSkillResponse.Builder res = UpgradeGuildSkillResponse.newBuilder();

				GuildTechManager guildTechManager = player.guildManager.guildTechManager;
				GuildResult ret = guildTechManager.upgradeSkillLevel(skillId);
				int result = ret.result;
				if (result == 0) {
					GuildSkill skillData = guildTechManager.getOneSkillData(skillId);
					player.taskManager.dealTaskEvent(TaskType.GUILD_TECH_UP, 1);
					res.setS2CCode(OK);
					res.setS2CSkillInfo(skillData);
					res.setS2CContribution(player.guildManager.getContribution());
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_SKILL_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TECH_SKILL_LEVEL_FULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(ret.des);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_CONTRIBUTION_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
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