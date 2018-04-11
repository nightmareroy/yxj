package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

/**
 * 七日登录入口.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@GClientEvent("area.activityFavorHandler.sevenDayPackageGetInfoRequest")
public class SevenDayPackageGetInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				body.writeBytes(player.activityManager.getSevenDayPackageGetInfo().toByteArray());
			}
		};
	}
}