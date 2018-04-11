package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityLsResponse;

/**
 * 获取活动列表
 * 
 * @author jjr
 */
@GClientEvent("area.activityHandler.activityLsRequest")
public class ActivityLsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (null == player) {
			return new ErrorResponse(LangService.getValue("SOMETHING_ERR"));
		}
		ActivityManager activityMgr = player.activityManager;
		if (null == activityMgr) {
			return new ErrorResponse(LangService.getValue("SOMETHING_ERR"));
		}

		ActivityLsResponse.Builder res = ActivityLsResponse.newBuilder();
		res.addAllS2CWelfareLs(activityMgr.getVailyActivityLs());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				activityMgr.updateSuperScriptList();
			}
		};
	}
}