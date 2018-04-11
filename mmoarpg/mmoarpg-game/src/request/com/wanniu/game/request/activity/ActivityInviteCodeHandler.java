package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityInviteCodeResponse;

@GClientEvent("area.activityHandler.activityInviteCodeRequest")
public class ActivityInviteCodeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		// ActivityInviteCodeRequest req = ActivityInviteCodeRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				ActivityInviteCodeResponse.Builder res = ActivityInviteCodeResponse.newBuilder();
				ActivityExt prop = player.activityManager.findActivityByType(Const.ACTIVITY_CENTER_TYPE.INVITE_CODE.getValue());
				if (prop == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CUR_NO_ACTIVITY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
			}
		};
	}

}
