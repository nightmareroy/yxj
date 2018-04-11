package com.wanniu.game.request.playerSkills;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.ext.SkillDataExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.playerSkill.SkillManager;
import com.wanniu.game.playerSkill.SkillUtil;
import com.wanniu.game.playerSkill.po.SkillDB;

import pomelo.area.SkillHandler.UpgradeSkillRequest;
import pomelo.area.SkillHandler.UpgradeSkillResponse;

@GClientEvent("area.skillHandler.upgradeSkillRequest")
public class UpgradeSkillHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		UpgradeSkillRequest req = UpgradeSkillRequest.parseFrom(pak.getRemaingBytes());
		int skillId = req.getS2CSkillId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeSkillResponse.Builder res = UpgradeSkillResponse.newBuilder();
				SkillManager skillManager = player.skillManager;
				SkillDB skill = skillManager.getSkill(skillId);
			    if (skill == null) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    if (skill.flag == 0) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SKILL_LOCK"));
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    com.wanniu.game.playerSkill.SkillManager.CheckSkillData data = skillManager.checkSkillReq(skill);
			    int result = data.result;
			    if (result == 0) {
			        if (skillManager.upgradeOneSkill(skill.id)) {
			        	SkillDataExt prop = SkillUtil.getProp(skillId);
			            if(prop.skillType == Const.SkillType.EFFECT_PASSIVE.getValue()){
			                player.initAndCalAllInflu(null);
			                player.pushAndRefreshEffect(false);
			            }
			            res.setS2CCode(OK);
			        } else {
			        	res.setS2CCode(FAIL);
				    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			        }
			        player.getPlayerTasks().dealTaskEvent(TaskType.SKILL_UP, 1);
			    }else if(result == -1){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SKILL_MAX_LEVEL"));
			    }else if(result == -2){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SKILL_LEVEL_NOT_ENOUGH"));
			    }else if(result == -3){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
			    }else if(result == -4){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SKILL_PRESKILL_NOT_READY"));
			    }else if(result == -5){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SKILL_PRESKILL_NOT_READY"));
			    }else if(result == -6){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
			    }else if(result == -99){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
