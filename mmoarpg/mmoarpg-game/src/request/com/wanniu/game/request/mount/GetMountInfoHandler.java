package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.GetMountInfoRequest;
import pomelo.area.MountHandler.GetMountInfoResponse;

/**
 * 坐骑皮肤
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.getMountInfoRequest")
public class GetMountInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		GetMountInfoRequest req = GetMountInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				GetMountInfoResponse.Builder res = GetMountInfoResponse.newBuilder();
				if(!player.mountManager.isOpenMount()){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MOUNT_NOT_HAVE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// logic
				res.setS2CCode(OK);
				res.setS2CData(player.mountManager.getMountData().build());				
				body.writeBytes(res.build().toByteArray());
				
//				player.getWnBag().addCodeItem("mstar" + "", 1500, ForceType.DEFAULT, null, null);
//				player.getWnBag().addCodeItem("mup" + "", 1500, ForceType.DEFAULT, null, null);
			}
		};
	}
}
