package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityAwardRequest;
import pomelo.area.ActivityHandler.ActivityAwardResponse;

@GClientEvent("area.activityHandler.activityAwardRequest")
public class ActivityAwardHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ActivityAwardRequest req = ActivityAwardRequest.parseFrom(pak.getRemaingBytes());
		int awardId = req.getS2CAwardId();
		int activityId = req.getS2CActivityId();

		ActivityAwardResponse.Builder res = player.activityManager.activityAward(awardId, activityId);

		return new PomeloResponse() {
			protected void write() throws IOException {
				if (res.getS2CCode() == OK) {
					player.activityManager.updateSuperScriptList();

					res.setS2CMsg(LangService.getValue("ACTIVITY_RECEIVE"));
					body.writeBytes(res.build().toByteArray());

					ActivityExt propCenter = player.activityManager.findActivityById(activityId);
					Out.info(player.getId(), ":", propCenter.activity, "领取成功,礼包id:", awardId);
				} else {
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}