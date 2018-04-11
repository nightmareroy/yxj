package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 进阶界面
 * @author c
 *
 */
@GClientEvent("area.petHandler.upGradeInfoRequest")
public class GetUpGradeInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		UpGradeInfoRequest msg = UpGradeInfoRequest.parseFrom(pak.getRemaingBytes());
//
//		String petId = msg.getS2CPetId();
//		final UpGradeInfoResponse res = player.petManager.upGradeInfo(petId);
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				body.writeBytes(res.toByteArray());
//			}
//		};
		

		return null;
	}
}