package com.wanniu.game.request.payGift;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
@GClientEvent("area.payGiftHandler.firstPayGiftInfoRequest")
public class FirstPayGiftInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		FirstPayGiftInfoRequest req = FirstPayGiftInfoRequest.parseFrom(pak.getRemaingBytes());
//		return new PomeloResponse() {
//			protected void write() throws IOException {
//				WNPlayer player = (WNPlayer) pak.getPlayer();
//				Builder data = player.payGiftManager.getFirstPayInfo();
//				data.setS2CCode(OK);
//				body.writeBytes(data.build().toByteArray());
//			}
//		};
		return null;
	}

}

/*
Handler.prototype.firstPayGiftInfoRequest = function(msg, session, next) {
var player = session.player;
var data = player.payGiftManager.getFirstPayInfo();
next(null, {s2c_code : consts.CODE.OK, s2c_data : data.gift});

logger.debug('Player ', player.uid, ' firstPayGiftInfoRequest: ', {s2c_code : consts.CODE.OK, s2c_data : data.gift});
};
*/