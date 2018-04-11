package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 获取所有宠物Id
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.getAllPetsBaseInfoRequest")
public class GetAllPetsBaseInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		
//		final GetAllPetsBaseInfoResponse res = player.petManager.getAllPetsBaseInfo();
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
