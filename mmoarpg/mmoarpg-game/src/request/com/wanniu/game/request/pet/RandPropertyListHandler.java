package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;


/**
 * 洗练属性列表
 * @author c
 *
 */
@GClientEvent("area.petHandler.randPropertyListRequest")
public class RandPropertyListHandler extends PomeloRequest {


	@Override
	public PomeloResponse request() throws Exception {
//		RandPropertyListRequest msg = RandPropertyListRequest.parseFrom(pak.getRemaingBytes());
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		
//	    String petId = msg.getS2CPetId();
//
//	    final RandPropertyListResponse res = player.petManager.randPropertyList(petId);
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