package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.SaveMountRequest;

/**
 * 保存
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.saveMountRequest")
public class SaveMountHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		SaveMountRequest req = SaveMountRequest.parseFrom(pak.getRemaingBytes());
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		int skinId = req.getC2SSkinId();
	    int mountId = req.getC2SMountId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {

			}
		};
	}
}
