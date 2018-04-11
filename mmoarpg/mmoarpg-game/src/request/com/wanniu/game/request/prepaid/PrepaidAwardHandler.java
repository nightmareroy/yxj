package com.wanniu.game.request.prepaid;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
@GClientEvent("area.prepaidHandler.prepaidAwardRequest")
public class PrepaidAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		PrepaidAwardRequest req = PrepaidAwardRequest.parseFrom(pak.getRemaingBytes());
//		return new PomeloResponse() {
//			protected void write() throws IOException {
//				WNPlayer player = (WNPlayer) pak.getPlayer();
//				PrepaidAwardResponse.Builder res = player.prepaidManager.prepaidAward();
//				
//				res.setS2CCode(OK);
//				body.writeBytes(res.build().toByteArray());
//			}
//		};
		
		return null;
	}

}

