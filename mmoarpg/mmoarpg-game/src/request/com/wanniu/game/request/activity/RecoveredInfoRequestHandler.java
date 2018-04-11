package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

/**
 * 资源找回入口.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@GClientEvent("area.activityFavorHandler.recoveredInfoRequest")
public class RecoveredInfoRequestHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				body.writeBytes(player.activityManager.getRecoveredGetInfo().toByteArray());
			}
		};
	}
}