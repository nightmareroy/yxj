package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.RidingMountRequest;
import pomelo.area.MountHandler.RidingMountResponse;

/**
 * 坐骑皮肤
 * @author haog
 *
 */
@GClientEvent("area.mountHandler.ridingMountRequest")
public class RidingMountHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		RidingMountRequest req = RidingMountRequest.parseFrom(pak.getRemaingBytes());
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		int isUp = req.getC2SIsUp();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RidingMountResponse.Builder res = RidingMountResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					PlayerUtil.logWarnIfPlayerNull(pak);
					return;
				}
				// logic
				if(!AreaUtil.canRideMount(wPlayer.getAreaId())){
					res.setS2CCode(FAIL);
					MapBase map = AreaUtil.getAreaProp(wPlayer.getAreaId());
					if(map.type == SCENE_TYPE.FIVE2FIVE.getValue()||map.type == SCENE_TYPE.ALLY_FIGHT.getValue()||map.type == SCENE_TYPE.ARENA.getValue()||map.type == SCENE_TYPE.CROSS_SERVER.getValue()||map.type == SCENE_TYPE.SIN_COM.getValue())
				    	res.setS2CMsg("");
					else
						res.setS2CMsg(LangService.getValue("MOUNT_THIS_SENCE_CANNOT_USE_MOUNT"));
			    	body.writeBytes(res.build().toByteArray());
			    	return;
//			        return next(null, {s2c_code: codeData.FAIL, s2c_msg: strList.MOUNT_THIS_SENCE_CANNOT_USE_MOUNT});
			    }
			    int result = wPlayer.mountManager.ridingMount(isUp);

			    if (result == 0) {
			    	res.setS2CCode(OK);
			    }
			    else{
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("SOMETHING_ERR");
			    }				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
