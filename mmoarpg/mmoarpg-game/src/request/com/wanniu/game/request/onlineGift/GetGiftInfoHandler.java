package com.wanniu.game.request.onlineGift;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.OnlineGiftHandler.GetGiftInfoResponse;
import pomelo.area.OnlineGiftHandler.OnlineGift;

/**
 * 获取礼物信息
 * @author haog
 *
 */
@GClientEvent("area.onlineGiftHandler.getGiftInfoRequest")
public class GetGiftInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
//		ReceiveGiftRequest req = ReceiveGiftRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// logic
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGiftInfoResponse.Builder res = GetGiftInfoResponse.newBuilder();
			    res.setS2CCode(OK);
			    OnlineGift gift = player.onlineGiftManager.toJson4Payload();
			    res.setS2CGift(gift);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
