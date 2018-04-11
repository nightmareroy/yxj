package com.wanniu.game.request.playerSkills;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.playerSkill.SkillManager;

import pomelo.area.SkillHandler.UpgradeSkillOneKeyResponse;

@GClientEvent("area.skillHandler.upgradeSkillOneKeyRequest")
public class UpgradeSkillOneKeyHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UpgradeSkillOneKeyResponse.Builder res = UpgradeSkillOneKeyResponse.newBuilder();
				SkillManager skillManager = player.skillManager;
//				skillManager.upgradeSkillOneKey();
//				res.setS2CCode(OK);
//		    	body.writeBytes(res.build().toByteArray());
		    	
		    	// 这里代码等策划改了在开放
		    	int result  = skillManager.upgradeSkillOneKey2();
		    	if(result ==0)
		    	{
		    		res.setS2CCode(OK);
		    		res.addAllSkillList(player.skillManager.getSkillsBasicList());
			    	body.writeBytes(res.build().toByteArray());
		    	}
		    	else if(result == -1){
		    		res.setS2CCode(FAIL);
		    		res.setS2CMsg(LangService.getValue("SKILL_UPGRADE_NONE"));
		    		body.writeBytes(res.build().toByteArray());
		    	}
		    	else if(result == -2){
		    		res.setS2CCode(FAIL);
		    		res.setS2CMsg(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
		    		body.writeBytes(res.build().toByteArray());
		    	}
		    	
			}
		};
	}

}
