package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 领悟技能
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.petComprehendSkillRequest")
public class PetComprehendSkillHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetComprehendSkillRequest msg = PetComprehendSkillRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//		String skillBookCode = msg.getS2CSkillBookCode();
//		List<Integer> lockPos = msg.getS2CLockPosList();
//
//		PetComprehendSkillResponse res = player.petManager.petComprehendSkill(petId, skillBookCode,
//				Utils.listToArray(lockPos));
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				body.writeBytes(res.toByteArray());
//			}
//		};

		return null;
	}
}
