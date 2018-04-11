package com.wanniu.game.request.prepaid;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.PrepaidHandler.PrepaidSDKResponse;
@GClientEvent("area.prepaidHandler.prepaidSDKRequest")
public class PrepaidSDKHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		PrepaidSDKRequest req = PrepaidSDKRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				PrepaidSDKResponse.Builder res = PrepaidSDKResponse.newBuilder();
				
//				JSONObject param = JSONObject.parseObject(req.getS2CParam());
//				res.setS2CData(player.prepaidManager.prepaidSDK(param));
//				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
