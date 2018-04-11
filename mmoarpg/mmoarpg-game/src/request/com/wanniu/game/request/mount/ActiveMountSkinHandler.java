package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.ActiveMountSkinRequest;
import pomelo.area.MountHandler.ActiveMountSkinResponse;

/**
 * 坐骑皮肤
 * 
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.activeMountSkinRequest")
public class ActiveMountSkinHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		ActiveMountSkinRequest req = ActiveMountSkinRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				int skinId = req.getC2SSkinId();
				ActiveMountSkinResponse.Builder res = ActiveMountSkinResponse.newBuilder();
				// logic
				int oldSkin = player.mountManager.mount.usingSkinId;
				int result = player.mountManager.changeSkin(skinId);
				if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MOUNT_SKIN_NOT_HAVE"));
				} else {
					res.setS2CCode(OK);
				}
				Out.info("坐骑换肤成功,roleId=", player.getId(), ",老坐骑外形Id=", oldSkin, "新坐骑外形Id=",
						player.mountManager.mount.usingSkinId);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
