package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 幻化预览
 * @author c
 *
 */
@GClientEvent("area.petHandler.petIllusionReviewRequest")
public class PetIllusionReviewHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetIllusionReviewRequest msg = PetIllusionReviewRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//		final PetIllusionReviewResponse res = player.petManager.petIllusionReview(petId);
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				body.writeBytes(res.toByteArray());
//			}
//		};

		return null;
	}
}