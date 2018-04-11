package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 幻化信息
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.petIllusionInfoRequest")
public class GetPetIllusionInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetIllusionInfoRequest msg = PetIllusionInfoRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//
//		final PetIllusionInfoResponse res = player.petManager.petIllusionInfo(petId);
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
