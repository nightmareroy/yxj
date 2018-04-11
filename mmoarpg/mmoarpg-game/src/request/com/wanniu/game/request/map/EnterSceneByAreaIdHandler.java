package com.wanniu.game.request.map;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.player.ChangeAreaFilter;

import pomelo.area.MapHandler.GnterSceneByAreaIdRequest;
import pomelo.area.MapHandler.GnterSceneByAreaIdResponse;

/**
 * 进入场景
 * 
 * @author agui
 *
 */
@GClientEvent("area.mapHandler.enterSceneByAreaIdRequest")
public class EnterSceneByAreaIdHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		GnterSceneByAreaIdRequest req = GnterSceneByAreaIdRequest.parseFrom(pak.getRemaingBytes());
		int areaId = req.getC2SAreaId();
		if (areaId == 0) {
			return new ErrorResponse(LangService.getValue("AREA_ID_NULL"));
		}
		MapBase prop = AreaDataConfig.getInstance().get(areaId);
		if (prop == null) {
			return new ErrorResponse(LangService.getValue("AREA_ID_NULL"));
		}
		
		AreaUtil.enterArea(player, areaId);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GnterSceneByAreaIdResponse.Builder res = GnterSceneByAreaIdResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}