package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.AdventureItemAddCO;
import com.wanniu.game.data.AdventureItemCO;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ActivityDataPO;

import pomelo.area.ActivityFavorHandler.LimitTimeGiftBuyRequest;
import pomelo.area.ActivityFavorHandler.LimitTimeGiftBuyResponse;

/**
 * 购买限时礼包
 *
 * @author liyue
 */
@GClientEvent("area.activityFavorHandler.limitTimeGiftBuyRequest")
public class LimitTimeGiftBuyHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		LimitTimeGiftBuyRequest req = LimitTimeGiftBuyRequest.parseFrom(pak.getRemaingBytes());
		int id=req.getId();

		WNPlayer player = (WNPlayer) pak.getPlayer();

		ActivityManager activityManager = player.activityManager;
//		ActivityDataPO activityDataPO = activityManager.toJson4Serialize();

		

		LimitTimeGiftBuyResponse.Builder res = LimitTimeGiftBuyResponse.newBuilder();
		
		int result=activityManager.BugLimitTimeGift(id);
		
		switch (result) {
		case 0:
			break;
		case 2:
			return new ErrorResponse(LangService.getValue("LIMIT_TIME_GIFT_BOUGHT"));
		case 3:
			return new ErrorResponse(LangService.getValue("LIMIT_TIME_GIFT_NO_ENOUTH_DIAMOND"));

		default:
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}
		

		

		return new PomeloResponse() {
			protected void write() throws IOException {
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}