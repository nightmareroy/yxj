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

import pomelo.area.ActivityFavorHandler.LimitTimeGiftInfoRequest;
import pomelo.area.ActivityFavorHandler.LimitTimeGiftInfoResponse;

/**
 * 购买限时礼包
 *
 * @author liyue
 */
@GClientEvent("area.activityFavorHandler.limitTimeGiftInfoRequest")
public class LimitTimeGiftInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		LimitTimeGiftInfoRequest req = LimitTimeGiftInfoRequest.parseFrom(pak.getRemaingBytes());

		WNPlayer player = (WNPlayer) pak.getPlayer();

		ActivityManager activityManager = player.activityManager;
//		ActivityDataPO activityDataPO = activityManager.toJson4Serialize();

		

		LimitTimeGiftInfoResponse.Builder res = LimitTimeGiftInfoResponse.newBuilder();
		
		res.setS2CCode(OK);
		res.addAllLimitTimeGiftInfo(activityManager.getLimitTimeGiftInfos());
		

		return new PomeloResponse() {
			protected void write() throws IOException {
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}