package com.wanniu.game.request.vip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.VipHandler.GetEveryDayGiftRequest;
import pomelo.area.VipHandler.GetEveryDayGiftResponse;

@GClientEvent("area.vipHandler.getEveryDayGiftRequest")
public class GetEveryDayGiftHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				GetEveryDayGiftRequest req = GetEveryDayGiftRequest.parseFrom(pak.getRemaingBytes());
				int type = req.getC2SType();
				int result = player.vipManager.takeDailyReward(type);
				GetEveryDayGiftResponse.Builder res = GetEveryDayGiftResponse.newBuilder();
				if(result == 0){
					res.setS2CCode(OK);
				}else if(result == -1){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CARD_MONTH_NONE"));
				}else if(result == -2){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CARD_FOREVER_NONE"));
				}else if(result == -3){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CARD_RECEIVED"));
				}else if(result == -4){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				}
//				PrepaidListResponse.Builder res = PrepaidListResponse.newBuilder();
//				List<FeeItem> items = player.prepaidManager.getPrepaidList();
//				res.addAllS2CItems(items);
//				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
