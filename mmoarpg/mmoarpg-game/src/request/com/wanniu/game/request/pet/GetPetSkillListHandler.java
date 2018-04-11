package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 技能列表信息
 * @author c
 *
 */

@GClientEvent("area.petHandler.petSkillListRequest")
public class GetPetSkillListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetSkillListRequest msg = PetSkillListRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//		final List<PetSkill> data = player.petManager.petSkillList(petId);
//
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				PetSkillListResponse.Builder res = PetSkillListResponse.newBuilder();
//				res.setS2CCode(Const.CODE.OK);
//				for (PetSkill ps : data) {
//					SkillInfo.Builder skillBuilder = SkillInfo.newBuilder();
//					skillBuilder.setId(ps.id);
//					skillBuilder.setLevel(ps.level);
//					skillBuilder.setPos(ps.pos);
//					skillBuilder.setInborn(ps.inborn);
//					res.addS2CData(skillBuilder.build());
//				}
//				body.writeBytes(res.build().toByteArray());
//			}
//		};

		return null;
	}

}
