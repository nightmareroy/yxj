package com.wanniu.game.request.payGift;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
@GClientEvent("area.payGiftHandler.dailyPayGiftInfoRequest")
public class DailyPayGiftInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		DailyPayGiftInfoRequest req = DailyPayGiftInfoRequest.parseFrom(pak.getRemaingBytes());
//		return new PomeloResponse() {
//			protected void write() throws IOException {
//				WNPlayer player = (WNPlayer) pak.getPlayer();
//				pomelo.area.PayGiftHandler.DailyPayGiftInfoResponse.Builder res = player.payGiftManager.getDailyPayInfo();
//				res.setS2CCode(OK);
//				body.writeBytes(res.build().toByteArray());
//			}
//		};
		
		return null;
	}

}


/*

Handler.prototype.dailyPayGiftInfoRequest = function(msg, session, next) {
    var player = session.player;
    var data = player.payGiftManager.getDailyPayInfo();
    next(null, {
        s2c_code : consts.CODE.OK,
        s2c_dayIndex: data.dayIndex,
        s2c_dailyDiamondCount: data.dailyDiamondCount,
        s2c_dailyMoneyCount: data.dailyMoneyCount,
        s2c_data : data.gift});

    logger.debug('Player ', player.uid, ' dailyPayGiftInfoRequest: ', {s2c_code : consts.CODE.OK, s2c_data : data.gift});
};
*/