package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityFavorHandler.RecoveredRequest;

/**
 * 资源找回入口.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@GClientEvent("area.activityFavorHandler.recoveredRequest")
public class RecoveredRequestHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		RecoveredRequest request = RecoveredRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				ActivityManager m = ((WNPlayer) pak.getPlayer()).activityManager;
				body.writeBytes(m.recoveredRequest(request.getId(), request.getType()).toByteArray());
			}
		};
	}
}