package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.UpgradeClassResponse;

@GClientEvent("area.playerHandler.upgradeClassRequest")
public class UpgradeClassHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				UpgradeClassResponse.Builder res = UpgradeClassResponse.newBuilder();
				res.setS2CCode(OK);
				String result = null;
				if(player!=null)
					result = player.baseDataManager.upgradeClass();
				if(result!=null){
					res.setS2CCode(FAIL);
					res.setS2CMsg(result);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
