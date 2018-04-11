package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.SevenDayPackageAwardRequest;
import pomelo.area.ActivityFavorHandler.SevenDayPackageAwardResponse;

/**
 * 七日登录领取.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@GClientEvent("area.activityFavorHandler.sevenDayPackageAwardRequest")
public class SevenDayPackageAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		SevenDayPackageAwardRequest req = SevenDayPackageAwardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				SevenDayPackageAwardResponse response = player.activityManager
						.receiveSevenDayPackageAward(req.getPackageId());
				body.writeBytes(response.toByteArray());
			}
		};
	}
}