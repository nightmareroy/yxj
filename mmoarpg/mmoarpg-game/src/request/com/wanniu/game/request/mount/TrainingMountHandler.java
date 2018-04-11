package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.TrainingMountRequest;
import pomelo.area.MountHandler.TrainingMountResponse;

/**
 * 培养坐骑
 * 
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.trainingMountRequest")
public class TrainingMountHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		TrainingMountRequest req = TrainingMountRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				TrainingMountResponse.Builder res = TrainingMountResponse.newBuilder();
				if (!player.mountManager.isOpenMount()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MOUNT_NOT_HAVE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				int type = req.getC2SType();
				if (type == 1) { // 升阶
					int result = player.mountManager.upgradeLv();
					if (result == -1) {// 已结满阶
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_SKIN_HAVE_MAX_STAGE"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == -2) {// 未满十星
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_NOT_TEN_STAR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == -3) {// 道具不够
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_SKIN_NOT_ENOUGH_ITEM"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else {// 成功
						res.setS2CCode(OK);
						res.setS2CData(player.mountManager.getMountData().build());
						body.writeBytes(res.build().toByteArray());
						Out.info("坐骑升阶成功,roleId=", player.getId(), ",坐骑外形Id=", player.mountManager.mount.usingSkinId,
								"坐骑阶级=", player.mountManager.mount.rideLevel);
						return;
					}
				} else { // 升星
					int result = player.mountManager.upgradeStar();
					if (result == -1) {// 已结满星
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_ALREADY_TEN_STAR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (result == -2) {// 道具不够
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_SKIN_NOT_ENOUGH_ITEM"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else {// 成功
						res.setS2CCode(OK);
						res.setS2CData(player.mountManager.getMountData().build());
						body.writeBytes(res.build().toByteArray());
						Out.info("坐骑升星成功,roleId=", player.getId(), ",坐骑外形Id=", player.mountManager.mount.usingSkinId,
								"坐骑星级=", player.mountManager.mount.starLv);
						return;
					}
				}

			}
		};
	}
}
