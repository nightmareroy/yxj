package com.wanniu.game.request.playerSkills;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SkillHandler.GetSkillDetailRequest;
import pomelo.area.SkillHandler.GetSkillDetailResponse;
import pomelo.area.SkillHandler.SkillDetail;

@GClientEvent("area.skillHandler.getSkillDetailRequest")
public class GetSkillDetailHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		GetSkillDetailRequest req = GetSkillDetailRequest.parseFrom(pak.getRemaingBytes());
		int skillId = req.getS2CSkillId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetSkillDetailResponse.Builder res = GetSkillDetailResponse.newBuilder();
				SkillDetail skillDetail = player.skillManager.getSkillDetail4PayLoad(skillId);
			    if (skillDetail != null) {
			    	res.setS2CCode(OK);
			    	res.setS2CSkill(skillDetail);
			    } else {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SKILL_NULL");
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
