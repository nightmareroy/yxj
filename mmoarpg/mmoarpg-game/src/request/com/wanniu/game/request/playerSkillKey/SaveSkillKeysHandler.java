package com.wanniu.game.request.playerSkillKey;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SkillKeysHandler.SaveSkillKeysRequest;
import pomelo.area.SkillKeysHandler.SaveSkillKeysResponse;

@GClientEvent("area.skillKeysHandler.saveSkillKeysRequest")
public class SaveSkillKeysHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		SaveSkillKeysRequest req = SaveSkillKeysRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SaveSkillKeysResponse.Builder res = SaveSkillKeysResponse.newBuilder();
				int result = player.skillManager.changeSkillsPos(req);
				
			    if(result == 0){
			    	res.setS2CCode(OK);
			    	WNNotifyManager.getInstance().pushSkillKeysUpdate(player, player.skillKeyManager.toJson4Payload());
			    }else if(result == -1){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILLKEY_SET_ERROR_SKILL_REPEAT");
			    }else if(result == -2){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILLKEY_SET_ERROR_KEY_LOCK");
			    }else if(result == -3){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILLKEY_SET_ERROR_SKILL_NOT_CAN_SET");
			    }else if(result == -4){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILLKEY_SET_ERROR_NOT_FIND_SKILL");
			    }else if(result == -5){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILLKEY_SET_ERROR_SKILL_NOT_LEARN");
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

	
}
