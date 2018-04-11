package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.solo.vo.ResultVO;

import pomelo.area.SoloHandler.LeaveSoloAreaResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.leaveSoloAreaRequest")
public class LeaveSoloAreaHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		if (player.getSceneType() != Const.SCENE_TYPE.SIN_COM.getValue()) {
			return new ErrorResponse(LangService.getValue("AREA_ID_NULL"));
		}
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {

				LeaveSoloAreaResponse.Builder res = LeaveSoloAreaResponse.newBuilder();

				ResultVO result = player.soloManager.handleLeaveSoloArea();
				if (result.result) {
					// TODO
//					if (player.getArea().getPlayerNum() <= 0) {
//						areaUtil.closeArea(area.instanceId);
//					}
					res.setS2CCode(Const.CODE.OK);
				} else {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(result.info);
				}

				body.writeBytes(res.build().toByteArray());
			}

		};
	}
}