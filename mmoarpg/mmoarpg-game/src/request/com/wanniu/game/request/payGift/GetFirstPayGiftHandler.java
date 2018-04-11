package com.wanniu.game.request.payGift;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
@GClientEvent("area.payGiftHandler.getFirstPayGiftRequest")
public class GetFirstPayGiftHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		
		return null;
	}

}


/*
Handler.prototype.getFirstPayGiftRequest = function(msg, session, next) {
var player = session.player;
var retCode = player.payGiftManager.getFirstPayGiftRequest();

if(retCode === payGiftManager.RetCode.PAY_GIFT_OK) {
    next(null, {s2c_code: consts.CODE.OK});
    logger.debug('Player ', player.uid, ' getFirstPayGiftRequest: ', {s2c_code: consts.CODE.OK});
}
else if(retCode === payGiftManager.RetCode.PAY_GIFT_ERROR_GIFT_GET) {
    next(null, {s2c_code: consts.CODE.FAIL, s2c_msg: strList.SIGN_HAVE_RECEIVED});
    logger.debug('Player ', player.uid, ' getFirstPayGiftRequest: ', {s2c_code: consts.CODE.FAIL, s2c_msg: strList.SIGN_HAVE_RECEIVED});
}
else if(retCode === payGiftManager.RetCode.PAY_GIFT_ERROR_CAN_NOT_GET) {
    next(null, {s2c_code: consts.CODE.FAIL, s2c_msg: strList.SIGN_NOT_EXIST});
    logger.debug('Player ', player.uid, ' getFirstPayGiftRequest: ', {s2c_code: consts.CODE.FAIL, s2c_msg: strList.SIGN_NOT_EXIST});
}
};
*/