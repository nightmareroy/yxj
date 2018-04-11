package com.wanniu.game.request.onlineGift;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.OnlineGiftHandler.OnlineGift;
import pomelo.area.OnlineGiftHandler.ReceiveGiftRequest;
import pomelo.area.OnlineGiftHandler.ReceiveGiftResponse;

/**
 * 信息获取
 * 
 * @author haog
 *
 */
@GClientEvent("area.onlineGiftHandler.receiveGiftRequest")
public class ReceiveGiftHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		ReceiveGiftRequest req = ReceiveGiftRequest.parseFrom(pak.getRemaingBytes());
		// logic
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ReceiveGiftResponse.Builder res = ReceiveGiftResponse.newBuilder();
				// logic
				if (req.getC2SId() == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				int resultCode = player.onlineGiftManager.receiveGift(req.getC2SId());
				if (resultCode == 0) {
					OnlineGift giftData = player.onlineGiftManager.toJson4Payload();
					res.setS2CGift(giftData);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					Out.info(player.getId(),":领取在线礼包，礼包id:",req.getC2SId());
					return;
				} else {
					String msg;
					if (resultCode == -1) {
						msg = LangService.getValue("ONLINE_HAVE_RECEIVED");
					} else if (resultCode == -2) {
						msg = LangService.getValue("ONLINE_UPLEVEL_NOT_MATCH");
					} else if (resultCode == -3) {
						msg = LangService.getValue("ONLINE_LEVEL_NOT_MATCH");
					} else if (resultCode == -4) {
						msg = LangService.getValue("ONLINE_TIME_NOT_ENOUGH");
					} else if (resultCode == -5) {
						msg = LangService.getValue("BAG_NOT_ENOUGH_POS");
					} else if (resultCode == -6) {
						msg = LangService.getValue("PARAM_ERROR");
					} else {
						msg = LangService.getValue("SOMETHING_ERR");
					}
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
					body.writeBytes(res.build().toByteArray());
					return;
				}
			}
		};
	}
}
