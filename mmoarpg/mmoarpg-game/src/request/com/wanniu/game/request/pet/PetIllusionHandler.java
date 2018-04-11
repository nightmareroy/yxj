package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;


/**
 * 幻化
 * @author c
 *
 */
@GClientEvent("area.petHandler.petIllusionRequest")
public class PetIllusionHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetIllusionRequest msg = PetIllusionRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//		final PetIllusionResponse res = player.petManager.petIllusion(petId);
//		
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				body.writeBytes(res.toByteArray());
//			}
//		};

		return null;
	}
}