package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.OneKeyTrainingRequest;

/**
 * 一键培养坐骑
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.oneKeyTrainingRequest")
public class OneKeyTrainingHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		OneKeyTrainingRequest req = OneKeyTrainingRequest.parseFrom(pak.getRemaingBytes());
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {

			}
		};
	}
}
