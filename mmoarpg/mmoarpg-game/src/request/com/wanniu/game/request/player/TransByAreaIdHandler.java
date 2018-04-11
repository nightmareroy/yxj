package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.TransByAreaIdRequest;
import pomelo.area.PlayerHandler.TransByAreaIdResponse;

/**
 * 通过小地图直接传送
 * 
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.transByAreaIdRequest")
public class TransByAreaIdHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		TransByAreaIdRequest req = TransByAreaIdRequest.parseFrom(pak.getRemaingBytes());
		int areaId = req.getC2SAreaId();
		MapBase sceneProp = AreaUtil.getAreaProp(areaId);
		String result = AreaUtil.canTransArea(sceneProp, player);
		if (result != null) {
			return new ErrorResponse(result);
		}

		GWorld.getInstance().ansycExec(() -> {
			AreaUtil.disCardItemByTransArea(sceneProp, player);
			AreaUtil.enterArea(player, areaId, 0, 0);
		});

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TransByAreaIdResponse.Builder res = TransByAreaIdResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}