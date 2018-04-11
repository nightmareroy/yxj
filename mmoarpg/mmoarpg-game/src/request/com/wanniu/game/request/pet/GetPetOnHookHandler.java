package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 获取宠物挂机设置信息
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.petOnHookGetRequest")
public class GetPetOnHookHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		PetOnHookGetRequest msg = PetOnHookGetRequest.parseFrom(pak.getRemaingBytes());
//		
//		String petId = msg.getS2CPetId();
//		final String data = player.petManager.petOnHookGet(petId);
//
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				PetOnHookGetResponse.Builder res = PetOnHookGetResponse.newBuilder();
//				res.setS2CCode(Const.CODE.OK);
//				res.setS2CData(data);
//				body.writeBytes(res.build().toByteArray());
//			}
//		};

		return null;
	}

}
